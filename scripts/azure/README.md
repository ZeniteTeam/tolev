# Deploy do Tolev no Azure

Backend Spring Boot publicado como jar no runtime Java 21 gerenciado do App
Service, apontando para um Azure Database for PostgreSQL Flexible Server.

## Recursos

Todos no resource group `tolev-pg`, em Canada Central.

| Recurso | Nome |
|---|---|
| PostgreSQL Flexible Server | `tolev-pg-zenite` (Burstable B1ms, PG 16, banco `tolev`) |
| App Service Plan | `plan-tolev` (B1, Linux) |
| Web App | `tolev-api-zenite` |

API: <https://tolev-api-zenite.azurewebsites.net>

## Scripts

| Script | O que faz |
|---|---|
| `01-provision-postgres.sh` | Cria o resource group, o Flexible Server e as duas regras de firewall. Já executado |
| `02-run-local-against-azure.sh` | Sobe o backend na máquina local contra o banco do Azure |
| `03-deploy-app-service.sh` | Compila o jar e publica no App Service |

O Phase 04 não tem script: consiste em definir `EXPO_PUBLIC_API_URL` no
`TolevFront/.env` e no perfil de build do `eas.json`.

## Pré-requisitos

```bash
az login
az account show -o table
```

O banco precisa estar ligado antes do Phase 02 ou 03. O perfil `azure` sobe com
Flyway ativo e Hibernate em `validate`: sem banco alcançável a aplicação não
passa do boot.

```bash
az postgres flexible-server start -g tolev-pg -n tolev-pg-zenite
```

O App Service alcança o banco pela regra de firewall que libera serviços do
Azure (`0.0.0.0`–`0.0.0.0`, o marcador especial da plataforma). Rodar o backend
localmente contra o Azure exige uma regra adicional com o IP da máquina, que
muda quando o IP residencial muda.

```bash
az postgres flexible-server firewall-rule list -g tolev-pg -n tolev-pg-zenite -o table
```

## Configuração

Os dois scripts leem o `.env.azure` na raiz do repositório, que é gitignored.
Modelo em `.env.azure.example`. É a fonte única: os mesmos valores validados no
Phase 02 são os que sobem para o App Service.

| Variável | Observação |
|---|---|
| `SPRING_DATASOURCE_URL` | `sslmode=require` é obrigatório no Flexible Server |
| `SPRING_DATASOURCE_USERNAME` | `tolev_admin`, com underscore |
| `SPRING_DATASOURCE_PASSWORD` | |
| `JWT_SECRET` | Base64 de no mínimo 256 bits. Permanente: trocar invalida todo token emitido |
| `JWT_EXPIRATION` | Padrão `86400000` |
| `GEMINI_API_KEY` | Sem ela a importação de extrato falha em produção |
| `GEMINI_MODEL` | Padrão `gemini-3.5-flash` |
| `RG`, `LOC` | `tolev-pg`, `canadacentral` |
| `APP_NAME` | Único em `azurewebsites.net` |
| `PLAN_NAME`, `PLAN_SKU` | `plan-tolev`, `B1` |

## Rodar

```bash
./scripts/azure/02-run-local-against-azure.sh   # checkpoint local
./scripts/azure/03-deploy-app-service.sh        # publica
```

O Phase 03 é idempotente: a primeira execução cria o plano e o web app, as
seguintes só recompilam e republicam o jar, sem recriar recurso nem trocar
segredo. Um `JWT_SECRET` já publicado vence o do arquivo, para que um redeploy
não deslogue a base inteira.

## O que o Phase 03 publica

- `SPRING_PROFILES_ACTIVE=azure` e as três `SPRING_DATASOURCE_*`
- `JWT_SECRET`, `JWT_EXPIRATION`, `GEMINI_API_KEY`, `GEMINI_MODEL`
- `JAVA_OPTS=-Xms256m -Xmx1024m` — sem teto de heap a JVM se dimensiona pelo
  host e leva OOM kill na B1
- HTTPS obrigatório
- Sonda de saúde em `/actuator/health/liveness`, e não no `/actuator/health`
  completo, que inclui o indicador do banco e devolveria `503` sempre que o
  Postgres estivesse parado de propósito

## Verificação automática

Ao final, o script espera o liveness responder e confere que
`GET /transactions` sem token devolve `401` com o corpo
`{"status":401,...,"message":"Sessão expirada. Faça login novamente."}`. Esse
corpo é o do `SecurityConfig` deste repositório, e não uma página de erro da
plataforma.

## Limitação conhecida

`TolevBackApplicationTests` é um `@SpringBootTest` sem perfil de teste e exige
Postgres alcançável. O deploy compila com `-DskipTests`, mas qualquer pipeline
de CI quebra enquanto essa classe não tiver um perfil próprio.
