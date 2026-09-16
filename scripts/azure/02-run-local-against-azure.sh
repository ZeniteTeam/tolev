#!/usr/bin/env bash
# Phase 02 -- roda o backend NA SUA MAQUINA apontando para o Postgres do Azure.
#
# E o checkpoint mais importante do processo: valida Flyway, SSL, firewall e o
# perfil `azure` num terminal seu, onde voce ve o stack trace, antes de existir
# qualquer coisa de compute na nuvem.
#
# Credenciais: crie .env.azure (copie de .env.azure.example) e preencha a senha.
# O arquivo e gitignored. Alternativa: exportar PGPASS antes de rodar.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
ENV_FILE="$ROOT/.env.azure"

if [ -f "$ENV_FILE" ]; then
  echo ">> Credenciais: .env.azure"
  set -a
  . "$ENV_FILE"
  set +a
else
  PG=${PG:-tolev-pg-zenite}
  PGUSER=${PGUSER:-tolev_admin}
  PGDB=${PGDB:-tolev}
  if [ -z "${PGPASS:-}" ]; then
    echo "ERRO: crie .env.azure (cp .env.azure.example .env.azure) ou exporte PGPASS." >&2
    exit 1
  fi
  # sslmode=require nao e opcional: o Flexible Server recusa conexao sem TLS.
  # Usuario e `tolev_admin` puro -- o formato user@servername era do Single Server.
  export SPRING_DATASOURCE_URL="jdbc:postgresql://$PG.postgres.database.azure.com:5432/$PGDB?sslmode=require"
  export SPRING_DATASOURCE_USERNAME="$PGUSER"
  export SPRING_DATASOURCE_PASSWORD="$PGPASS"
fi

export SPRING_PROFILES_ACTIVE=azure

if [ -z "${JWT_SECRET:-}" ]; then
  export JWT_SECRET=$(openssl rand -base64 48)
  echo ">> JWT_SECRET gerado so para esta execucao"
fi

: "${SPRING_DATASOURCE_URL:?falta SPRING_DATASOURCE_URL}"
: "${SPRING_DATASOURCE_USERNAME:?falta SPRING_DATASOURCE_USERNAME}"
: "${SPRING_DATASOURCE_PASSWORD:?falta SPRING_DATASOURCE_PASSWORD}"

echo ">> Perfil : azure"
echo ">> Banco  : ${SPRING_DATASOURCE_URL#jdbc:postgresql://}"
echo

cd "$ROOT/tolevBack"
./mvnw -B spring-boot:run
