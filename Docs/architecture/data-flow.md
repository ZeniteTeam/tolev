# Fluxo técnico de acesso aos dados (app ↔ nuvem)

> Atualizado em 2026-09-16. Três fluxos reais do sistema, na ordem em que são
> demonstrados no pitch: **gravação**, **leitura** e **gravação assíncrona com IA**.

## 1. Request de GRAVAÇÃO — `POST /transactions`

```mermaid
%%{init: {'theme':'dark'}}%%
sequenceDiagram
    autonumber
    participant APP as App (RN)
    participant F as JwtAuthenticationFilter
    participant C as TransactionController
    participant FAC as TransactionFacade
    participant UC as CreateTransactionService
    participant R as ITransacaoRepository (JPA)
    participant DB as PostgreSQL (Azure)
    participant BUS as Spring Events
    participant AN as Motor de Análise

    APP->>F: POST /transactions + Bearer JWT
    F->>F: valida assinatura e expiração
    F->>C: SecurityContext populado
    C->>C: desserializa TransactionRequest
    C->>FAC: request
    FAC->>UC: execute()
    UC->>R: save(Transacao)
    R->>DB: INSERT ... (transação aberta)
    DB-->>R: id gerado
    UC->>BUS: publish(DadosFinanceirosAlteradosEvent)
    UC-->>C: TransactionResponse
    C-->>APP: 201 Created (resposta imediata)
    Note over BUS,AN: COMMIT confirmado
    BUS->>AN: AFTER_COMMIT + @Async(analysisExecutor)
    AN->>DB: lê janela de 180 dias
    AN->>DB: grava Analise / Recomendacao
```

Pontos técnicos: o evento só dispara **depois do commit** (rollback não gera
análise sobre lançamento inexistente) e roda em **pool separado do Tomcat**, então
o usuário recebe o 201 sem esperar o recálculo.

## 2. Request de LEITURA — `GET /graphs/spending-by-category`

```mermaid
%%{init: {'theme':'dark'}}%%
sequenceDiagram
    autonumber
    participant APP as App (TanStack Query)
    participant F as JwtAuthenticationFilter
    participant C as GraphController
    participant Q as GetSpendingByCategoryService
    participant I as FinanceIntegrationApi
    participant R as Repositories (JPA)
    participant DB as PostgreSQL (Azure)

    APP->>APP: cache quente? render otimista
    APP->>F: GET /graphs/spending-by-category?idUsuario= + Bearer
    F->>C: autenticado
    C->>Q: execute(idUsuario)
    Q->>I: dados do domínio Finance
    I->>R: query derivada / JPQL
    R->>DB: SELECT ... WHERE id_usuario = ?
    DB-->>R: linhas
    R-->>Q: entidades
    Q->>Q: Mapper → DTO de resposta
    Q-->>C: GraphResponse
    C-->>APP: 200 OK + JSON
    APP->>APP: atualiza cache e re-renderiza o gráfico
```

Ponto técnico: Graphs **não lê tabela de outro domínio** — pede pela
`FinanceIntegrationApi` / `AnalysisIntegrationApi`. Trocar a implementação de um
domínio não quebra os outros.

## 3. Gravação assíncrona com IA — importação de extrato

```mermaid
%%{init: {'theme':'dark'}}%%
sequenceDiagram
    autonumber
    participant APP as App
    participant C as ExtratoController
    participant IMP as ImportExtratoService
    participant DB as PostgreSQL
    participant P as ProcessExtratoService (@Async)
    participant G as Gemini API
    participant GRAV as ExtratoGravacaoService

    APP->>C: POST /transactions/extrato (multipart PDF, até 10MB)
    C->>IMP: bytes do arquivo
    IMP->>DB: INSERT ImportacaoExtrato (status=PROCESSANDO)
    IMP-->>APP: 202 — id da importação
    IMP->>P: dispara no extratoExecutor
    P->>G: PDF + prompt (timeout 120s)
    G-->>P: lançamentos estruturados (JSON)
    P->>GRAV: normaliza e deduplica
    GRAV->>DB: INSERT transações + status=CONCLUIDA
    GRAV->>GRAV: publica DadosFinanceirosAlteradosEvent
    loop polling
        APP->>C: GET /transactions/extrato/{idImportacao}
        C->>DB: SELECT status
        C-->>APP: PROCESSANDO → CONCLUIDA / FALHOU
    end
    APP->>C: POST /{id}/confirmacao  (ou DELETE para desfazer)
```

Ponto técnico: o status é **persistido**, não guardado em memória — o usuário pode
fechar o app e, ao voltar, a tela sabe em que pé está a importação. E a importação
é reversível: `DELETE /transactions/extrato/{id}` desfaz o lote inteiro.
