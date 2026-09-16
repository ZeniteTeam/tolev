# Deploy do Tolev no Azure

| Fase | Script | O que faz |
|------|--------|-----------|
| 01 | `01-provision-postgres.sh` | Cria o Flexible Server e as duas regras de firewall |
| 02 | `02-run-local-against-azure.sh` | Sobe o backend **na sua máquina** contra o banco do Azure |
| 03 | `03-deploy-app-service.sh` | Publica o backend no App Service |
| 04 | — | Apontar o Expo para a API nova (`EXPO_PUBLIC_API_URL`) |

Runbook completo: <https://claude.ai/code/artifact/e46050ee-3ab2-4350-a772-ebde566981c6>

---

## Antes de rodar o Phase 03

### 1. Instalar o Azure CLI

Não está instalado nesta máquina (conferido em 11/09/2026 — nem no PATH do Git
Bash nem no do PowerShell). O Phase 01 provavelmente foi feito pelo portal ou de
outra máquina.

```powershell
winget install -e --id Microsoft.AzureCLI
```

Abra um terminal **novo** depois — o instalador mexe no PATH e a sessão atual
não enxerga a mudança. Então:

```bash
az login
az account show -o table    # confirme que é a subscription Azure for Students
```

### 2. Ligar o banco

Se você parou o servidor para esticar o crédito, ligue antes:

```bash
az postgres flexible-server start -g rg-tolev -n tolev-pg-zenite
```

Não é opcional. O perfil `azure` sobe com Flyway ligado e Hibernate em
`validate`: sem banco alcançável a aplicação não passa do boot, e o deploy
termina com a instância em loop de restart.

### 3. Conferir a regra de firewall dos serviços do Azure

```bash
az postgres flexible-server firewall-rule list \
  -g rg-tolev -n tolev-pg-zenite -o table
```

Precisa existir a `allow-azure-services` (`0.0.0.0`–`0.0.0.0`), criada no Phase
01. É por ela que o App Service alcança o banco. Sem ela o sintoma é um timeout
de conexão no boot, que se parece com senha errada e não é.

### 4. Preencher o `.env.azure`

O script lê tudo daqui — é a fonte de verdade única, e é de propósito: o mesmo
arquivo que o Phase 02 provou funcionar é o que sobe para o App Service.
Ninguém redigita hostname nem usuário.

| Variável | Observação |
|----------|-----------|
| `SPRING_DATASOURCE_*` | Já preenchidas desde o Phase 02 |
| `JWT_SECRET` | **Permanente.** Trocar desloga todo mundo |
| `GEMINI_API_KEY` | Sem ela a importação de extrato quebra em produção |
| `APP_NAME` | Único **no mundo** — vira `<nome>.azurewebsites.net` |
| `PLAN_SKU` | `B1` padrão |

### 5. Saber o que vai custar

O App Service B1 sai por ~US$13/mês. Somado aos US$17,56 do Postgres, dá
~US$31/mês contra os ~US$100 de crédito: o crédito acaba por volta de
**dezembro/2026**, que é exatamente a janela de migração já escolhida. Parar o
banco nos períodos sem desenvolvimento é o que dá folga nessa conta.

`F1` é grátis, mas dorme depois de 20 min de ociosidade e o cold start do Spring
Boot passa de um minuto — ruim para demonstrar o TCC ao vivo.

---

## Rodar

```bash
./scripts/azure/03-deploy-app-service.sh
```

É idempotente. A primeira execução cria o plano e o web app; as seguintes só
recompilam e republicam o jar, sem recriar recurso nem trocar segredo.

No fim ele espera a aplicação responder e confere o checkpoint sozinho: um
`GET /transactions` sem token tem que voltar `401` com o corpo
`{"status":401,...,"message":"Sessão expirada. Faça login novamente."}`. Essa
mensagem é a prova de que é o `SecurityConfig` deste repositório no ar, e não
uma página de erro da plataforma.

---

## Correções em relação ao runbook

O documento foi escrito em 28/08/2026 e envelheceu em cinco pontos:

1. **Usuário do banco.** O runbook usa `tolevadmin`; o real é `tolev_admin`, com
   underscore. Copiar o comando de lá dá `password authentication failed`.
2. **`JWT_SECRET` sorteado no comando.** O runbook põe
   `JWT_SECRET="$(openssl rand -base64 48)"` direto no `appsettings set` — o que
   significa que **todo redeploy troca a chave e desloga a base inteira**. Aqui
   a chave mora no `.env.azure`, e o script mantém a que já estiver publicada.
3. **Health check.** O runbook diz que não há endpoint e sugere deixar a sonda
   desligada. Agora existe: `spring-boot-starter-actuator` está no `pom.xml` e a
   sonda aponta para `/actuator/health/liveness` — não para o `/actuator/health`
   cheio, que inclui o indicador do banco e devolveria `503` toda vez que o
   Postgres estivesse parado de propósito.
4. **Contagem de migrations.** O runbook fala em 13 (V1–V13). Já são 15, com a
   `V14__importacao_extrato` e a `V15__extrato_assincrono`.
5. **Testes no CI (Phase 05).** O runbook afirma que
   `application-test.properties` roda tudo em H2 sem banco. Esse arquivo não
   existe, e `TolevBackApplicationTests` é um `@SpringBootTest` sem perfil: ele
   tenta o Postgres local e falha sem Docker de pé. O workflow do Phase 05 **vai
   quebrar** sem `-DskipTests`, ou sem alguém dar um perfil de teste a essa
   classe primeiro.
