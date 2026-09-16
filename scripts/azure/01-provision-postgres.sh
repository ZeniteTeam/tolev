#!/usr/bin/env bash
# Phase 01 -- provisiona o Azure Database for PostgreSQL Flexible Server.
# Pre-requisitos: az CLI instalado e `az login` feito.
#
#   PGPASS='SuaSenhaForte123!' ./scripts/azure/01-provision-postgres.sh
#
# A senha vem do ambiente de proposito: nao fica no script nem no historico do shell
# (note o espaco antes do comando para omiti-lo do history do bash).
set -euo pipefail

RG=${RG:-rg-tolev}
LOC=${LOC:-brazilsouth}
PG=${PG:-tolev-pg-zenite}          # precisa ser globalmente unico
PGUSER=${PGUSER:-tolev_admin}
PGDB=${PGDB:-tolev}

if [ -z "${PGPASS:-}" ]; then
  echo "ERRO: exporte PGPASS antes de rodar. 8-128 chars, 3 das 4 classes." >&2
  exit 1
fi

echo ">> Subscription ativa:"
az account show --query "{nome:name, id:id}" -o table

echo ">> Criando resource group $RG em $LOC"
az group create --name "$RG" --location "$LOC" -o none

echo ">> Criando servidor $PG (leva alguns minutos)"
az postgres flexible-server create \
  --resource-group "$RG" \
  --name "$PG" \
  --location "$LOC" \
  --admin-user "$PGUSER" \
  --admin-password "$PGPASS" \
  --tier Burstable \
  --sku-name Standard_B1ms \
  --storage-size 32 \
  --version 16 \
  --database-name "$PGDB" \
  --public-access None \
  -o none

# Regra 1: a sua maquina, para conseguir testar no Phase 02.
MYIP=$(curl -fsS ifconfig.me)
echo ">> Liberando seu IP atual ($MYIP)"
az postgres flexible-server firewall-rule create \
  --resource-group "$RG" --name "$PG" \
  --rule-name dev-machine \
  --start-ip-address "$MYIP" --end-ip-address "$MYIP" -o none

# Regra 2: 0.0.0.0/0.0.0.0 e o marcador especial do Azure para "qualquer servico
# Azure", NAO para "a internet inteira". E assim que o App Service alcanca o banco.
echo ">> Liberando servicos do Azure"
az postgres flexible-server firewall-rule create \
  --resource-group "$RG" --name "$PG" \
  --rule-name allow-azure-services \
  --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0 -o none

echo
echo "================ PRONTO ================"
echo "Host : $PG.postgres.database.azure.com"
echo "JDBC : jdbc:postgresql://$PG.postgres.database.azure.com:5432/$PGDB?sslmode=require"
echo
echo "Verificando conexao..."
az postgres flexible-server connect \
  --name "$PG" --admin-user "$PGUSER" --database-name "$PGDB" \
  --querytext "SELECT version();"
