#!/usr/bin/env bash
# Phase 03 -- publica o backend no Azure App Service (rota A: jar no runtime
# Java 21 gerenciado, sem registry e sem container).
#
#   ./scripts/azure/03-deploy-app-service.sh
#
# Pre-requisitos: `az login` feito e o Phase 02 verde -- se o backend nao sobe
# na sua maquina contra o banco do Azure, ele tambem nao vai subir la.
#
# Idempotente de proposito: rodar de novo republica o jar sem recriar recurso
# nenhum e sem trocar segredo nenhum. E o caminho normal de um novo deploy.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
ENV_FILE="$ROOT/.env.azure"

# ---------------------------------------------------------------- credenciais
# Uma fonte de verdade so. Os mesmos valores que o Phase 02 provou funcionar sao
# os que sobem para o App Service -- ninguem redigita hostname nem usuario, que
# e de onde saem os "password authentication failed" de `tolevadmin` sem
# underscore.
if [ ! -f "$ENV_FILE" ]; then
  echo "ERRO: $ENV_FILE nao existe. Copie de .env.azure.example e preencha." >&2
  exit 1
fi
set -a
. "$ENV_FILE"
set +a

RG=${RG:-rg-tolev}
LOC=${LOC:-brazilsouth}
APP=${APP_NAME:-}
PLAN=${PLAN_NAME:-plan-tolev}
SKU=${PLAN_SKU:-B1}

falta() { echo "ERRO: $1 nao esta definido em .env.azure" >&2; exit 1; }
[ -n "$APP" ] || falta APP_NAME
[ -n "${SPRING_DATASOURCE_URL:-}" ] || falta SPRING_DATASOURCE_URL
[ -n "${SPRING_DATASOURCE_USERNAME:-}" ] || falta SPRING_DATASOURCE_USERNAME
[ -n "${SPRING_DATASOURCE_PASSWORD:-}" ] || falta SPRING_DATASOURCE_PASSWORD
[ -n "${GEMINI_API_KEY:-}" ] || falta GEMINI_API_KEY

# O perfil `azure` le jwt.secret sem fallback: sem esta variavel o boot morre na
# resolucao de placeholder, com um stack trace que nao diz o nome dela.
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
  # A string de runtime mudou de formato entre versoes da CLI ("JAVA|21-java21"
  # nas antigas, "JAVA:21-java21" nas novas). Perguntar evita adivinhar errado.
  # O `-o tsv` devolve a linha inteira (runtime, fim do suporte, SO, ...):
  # sem o `cut -f1` o nome sai grudado no resto e a CLI recusa o runtime.
  RUNTIME=${RUNTIME:-$(az webapp list-runtimes --os linux -o tsv \
    | grep -iE 'java.*21' | head -1 | cut -f1)}
  RUNTIME=${RUNTIME:-"JAVA|21-java21"}
  echo ">> Criando web app $APP (runtime $RUNTIME)"
  az webapp create \
    --resource-group "$RG" --plan "$PLAN" --name "$APP" \
    --runtime "$RUNTIME" -o none
fi

# --------------------------------------------------------------------- segredos
# JWT_SECRET ja publicado vence o do arquivo. Trocar a chave invalida todo token
# em circulacao: quem estava logado leva "Sessao expirada" no proximo toque. Um
# redeploy nao pode deslogar a base inteira.
PUBLICADO=$(az webapp config appsettings list \
  --resource-group "$RG" --name "$APP" \
  --query "[?name=='JWT_SECRET'].value | [0]" -o tsv 2>/dev/null || true)

if [ -n "$PUBLICADO" ] && [ "$PUBLICADO" != "$JWT_SECRET" ]; then
  echo ">> JWT_SECRET ja publicado: mantendo o que esta la (nao desloga ninguem)"
  JWT_SECRET="$PUBLICADO"
fi

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

# A B1 tem 1,75 GB divididos com a plataforma. Sem teto, a JVM dimensiona a heap
# pelo host e leva OOM kill sob carga -- os restarts "misteriosos" das 3h.

echo ">> Forcando HTTPS"
az webapp update --resource-group "$RG" --name "$APP" --https-only true -o none

# Liveness, e nao o /actuator/health cheio: o health completo inclui o indicador
# do banco, e o banco fica parado de proposito para esticar o credito. A sonda
# responderia DOWN com a aplicacao sa e a plataforma reiniciaria a toa.
echo ">> Apontando o health check para /actuator/health/liveness"
az webapp config set --resource-group "$RG" --name "$APP" -o none \
  --generic-configurations '{"healthCheckPath": "/actuator/health/liveness"}' \
  || echo "   AVISO: nao consegui configurar o health check (siga sem ele)"

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

# A prova de que e o SecurityConfig deste repo respondendo, e nao uma pagina de
# erro da plataforma: o 401 tem corpo proprio, em portugues.
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
echo "Proximo passo -- Phase 04: aponte o Expo para a API nova."
echo "  TolevFront/.env -> EXPO_PUBLIC_API_URL=$BASE"
