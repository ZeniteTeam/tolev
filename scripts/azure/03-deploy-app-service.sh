#!/usr/bin/env bash
# Phase 03 -- publica o backend no App Service (jar no runtime Java 21 gerenciado).
#
#   ./scripts/azure/03-deploy-app-service.sh
#
# Pre-requisitos: `az login` e o Phase 02 verde.
# Idempotente: reexecutar republica o jar sem recriar recurso nem trocar segredo.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
ENV_FILE="$ROOT/.env.azure"

# ---------------------------------------------------------------- credenciais
# Fonte unica: os mesmos valores validados no Phase 02 sobem para o App Service.
if [ ! -f "$ENV_FILE" ]; then
  echo "ERRO: $ENV_FILE nao existe. Copie de .env.azure.example e preencha." >&2
  exit 1
fi
set -a
. "$ENV_FILE"
set +a

RG=${RG:-tolev-pg}
LOC=${LOC:-canadacentral}
APP=${APP_NAME:-}
PLAN=${PLAN_NAME:-plan-tolev}
SKU=${PLAN_SKU:-B1}

falta() { echo "ERRO: $1 nao esta definido em .env.azure" >&2; exit 1; }
[ -n "$APP" ] || falta APP_NAME
[ -n "${SPRING_DATASOURCE_URL:-}" ] || falta SPRING_DATASOURCE_URL
[ -n "${SPRING_DATASOURCE_USERNAME:-}" ] || falta SPRING_DATASOURCE_USERNAME
[ -n "${SPRING_DATASOURCE_PASSWORD:-}" ] || falta SPRING_DATASOURCE_PASSWORD
[ -n "${GEMINI_API_KEY:-}" ] || falta GEMINI_API_KEY

# O perfil `azure` le jwt.secret sem fallback: sem esta variavel o boot falha.
[ -n "${JWT_SECRET:-}" ] || falta JWT_SECRET
if [ "$(printf %s "$JWT_SECRET" | base64 -d 2>/dev/null | wc -c)" -lt 32 ]; then
  echo "ERRO: JWT_SECRET precisa ser Base64 de pelo menos 256 bits." >&2
  echo "      Gere com: openssl rand -base64 48" >&2
  exit 1
fi

echo ">> Subscription ativa:"
az account show --query "{nome:name, id:id}" -o table

# ------------------------------------------------------------------ App Service
if az appservice plan show --resource-group "$RG" --name "$PLAN" -o none 2>/dev/null; then
  echo ">> Plano $PLAN ja existe"
else
  echo ">> Criando plano $PLAN ($SKU, Linux)"
  az appservice plan create \
    --resource-group "$RG" --name "$PLAN" \
    --location "$LOC" --is-linux --sku "$SKU" -o none
fi

if az webapp show --resource-group "$RG" --name "$APP" -o none 2>/dev/null; then
  echo ">> Web app $APP ja existe"
else
  # O formato da string de runtime varia entre versoes da CLI, e `-o tsv` devolve
  # a linha inteira -- sem o `cut -f1` a CLI recusa o runtime.
  RUNTIME=${RUNTIME:-$(az webapp list-runtimes --os linux -o tsv \
    | grep -iE 'java.*21' | head -1 | cut -f1)}
  RUNTIME=${RUNTIME:-"JAVA|21-java21"}
  echo ">> Criando web app $APP (runtime $RUNTIME)"
  az webapp create \
    --resource-group "$RG" --plan "$PLAN" --name "$APP" \
    --runtime "$RUNTIME" -o none
fi

# --------------------------------------------------------------------- segredos
# JWT_SECRET ja publicado vence o do arquivo: trocar a chave invalida todo token
# em circulacao, e um redeploy nao pode deslogar a base inteira.
PUBLICADO=$(az webapp config appsettings list \
  --resource-group "$RG" --name "$APP" \
  --query "[?name=='JWT_SECRET'].value | [0]" -o tsv 2>/dev/null || true)

if [ -n "$PUBLICADO" ] && [ "$PUBLICADO" != "$JWT_SECRET" ]; then
  echo ">> JWT_SECRET ja publicado: mantendo o que esta la (nao desloga ninguem)"
  JWT_SECRET="$PUBLICADO"
fi

# JAVA_OPTS: teto de heap obrigatorio na B1. Sem ele a JVM se dimensiona pelo
# host e leva OOM kill sob carga.
echo ">> Publicando app settings"
az webapp config appsettings set \
  --resource-group "$RG" --name "$APP" -o none --settings \
  SPRING_PROFILES_ACTIVE=azure \
  SPRING_DATASOURCE_URL="$SPRING_DATASOURCE_URL" \
  SPRING_DATASOURCE_USERNAME="$SPRING_DATASOURCE_USERNAME" \
  SPRING_DATASOURCE_PASSWORD="$SPRING_DATASOURCE_PASSWORD" \
  JWT_SECRET="$JWT_SECRET" \
  JWT_EXPIRATION="${JWT_EXPIRATION:-86400000}" \
  GEMINI_API_KEY="$GEMINI_API_KEY" \
  GEMINI_MODEL="${GEMINI_MODEL:-gemini-3.5-flash}" \
  JAVA_OPTS="-Xms256m -Xmx1024m"

echo ">> Forcando HTTPS"
az webapp update --resource-group "$RG" --name "$APP" --https-only true -o none

# Liveness, e nao o /actuator/health cheio: o health completo inclui o indicador
# do banco, e devolveria DOWN com a aplicacao sa sempre que o banco estivesse parado.
echo ">> Apontando o health check para /actuator/health/liveness"
az webapp config set --resource-group "$RG" --name "$APP" -o none \
  --generic-configurations '{"healthCheckPath": "/actuator/health/liveness"}' \
  || echo "   AVISO: health check nao configurado (siga sem ele)"

# ----------------------------------------------------------------- build/deploy
echo ">> Compilando o jar"
cd "$ROOT/tolevBack"
./mvnw -B clean package -DskipTests

JAR=$(ls target/*.jar | grep -v 'sources\|javadoc' | head -1)
[ -n "$JAR" ] || { echo "ERRO: jar nao encontrado em target/" >&2; exit 1; }

echo ">> Publicando $JAR"
az webapp deploy \
  --resource-group "$RG" --name "$APP" \
  --src-path "$JAR" --type jar

# ------------------------------------------------------------------- checkpoint
BASE="https://$APP.azurewebsites.net"
echo
echo ">> Esperando a aplicacao responder em $BASE"
echo "   (primeiro boot leva um ou dois minutos)"

for i in $(seq 1 40); do
  if curl -fsS --max-time 10 "$BASE/actuator/health/liveness" >/dev/null 2>&1; then
    echo "   liveness OK"
    break
  fi
  [ "$i" = 40 ] && {
    echo "   nao respondeu em ~5min. Veja o log com:" >&2
    echo "   az webapp log tail --resource-group $RG --name $APP" >&2
    exit 1
  }
  sleep 8
done

# O 401 com corpo proprio em portugues prova que e o SecurityConfig deste
# repositorio respondendo, e nao uma pagina de erro da plataforma.
echo ">> Conferindo o 401 de rota protegida"
CORPO=$(curl -s --max-time 15 "$BASE/transactions")
CODIGO=$(curl -s -o /dev/null -w '%{http_code}' --max-time 15 "$BASE/transactions")

echo
echo "================ PRONTO ================"
echo "API    : $BASE"
echo "Health : $BASE/actuator/health/liveness"
echo "Status : HTTP $CODIGO em /transactions"
echo "Corpo  : $CORPO"
echo
if [ "$CODIGO" = "401" ] && printf %s "$CORPO" | grep -q "Sessão expirada"; then
  echo "Checkpoint OK -- o SecurityConfig do repositorio esta no ar."
else
  echo "ATENCAO: esperava 401 com \"Sessão expirada. Faça login novamente.\""
  echo "Veja o log: az webapp log tail --resource-group $RG --name $APP"
fi
echo
echo "Proximo passo -- Phase 04: apontar o Expo para a API nova."
echo "  TolevFront/.env -> EXPO_PUBLIC_API_URL=$BASE"
