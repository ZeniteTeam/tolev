# Arquitetura — Backend (visão atual)

> Atualizado em 2026-09-16 a partir do código em `tolevBack/`.
> Stack: **Java 21 · Spring Boot 3.5.0 · PostgreSQL 16 · Flyway · Docker**.
> Comunicação entre domínios: **integration APIs** (chamada direta tipada) e
> **Spring Events** (reação assíncrona). Não há acoplamento de banco entre domínios.
>
> **Legenda:** caixas marcadas com *(planejado)* e contorno tracejado estão
> desenhadas e previstas na arquitetura, mas ainda não implementadas no código.

## 1. Visão geral

```mermaid
%%{init: {'theme':'dark'}}%%
graph TB

    subgraph CLIENTE["Cliente"]
        APP["App Mobile<br/>Expo · React Native · TypeScript"]
    end

    subgraph EDGE["Borda HTTP"]
        FILTER["JwtAuthenticationFilter<br/>(OncePerRequest)"]
        SEC["SecurityConfig<br/>stateless · BCrypt · /auth/** público"]
        CTRL["18 @RestControllers<br/>/auth /users /transactions /accounts<br/>/dividas /debts /progression /analysis<br/>/recommendations /graphs /simulations"]
    end

    APP -->|"HTTPS · Authorization: Bearer JWT"| FILTER
    FILTER --> SEC
    SEC --> CTRL

    subgraph APPLICATION["Monólito modular — domínios isolados"]

        subgraph D_USERS["Domínio Users"]
            U_API["AuthController · UserController<br/>PreferenceController"]
            U_UC["Register · Authenticate · CRUD<br/>Preferência Financeira"]
        end

        subgraph D_FINANCE["Domínio Finance"]
            F_API["Transaction · Account · Bank<br/>Category · Extrato · FinancialOverview"]
            F_UC["CreateTransaction · UpdateCategory<br/>Import/Process/Confirm/Undo Extrato"]
        end

        subgraph D_PROG["Domínio Progression"]
            P_API["Divida · Debt · Map<br/>Module · Progression"]
            P_UC["CRUD Dívida · Parcelas<br/>Pagamento · Projeção · Estratégia"]
        end

        subgraph D_ANALYSIS["Domínio Analysis"]
            A_API["Analysis · Recommendation"]
            A_UC["GenerateAnalysis<br/>RecommendationEngine"]
            A_AN["Analyzers<br/>Consumo · Inadimplência<br/>Previsão · Saúde · Risco"]
        end

        subgraph D_GRAPHS["Domínio Graphs"]
            G_API["GraphController<br/>spending · debt-evolution · risk<br/>score-evolution · impact-ranking"]
        end

        subgraph D_SIM["Domínio Simulations"]
            S_API["SimulationController<br/>debt-payoff · future-balance<br/>purchase-impact · financial-future"]
        end

        subgraph D_SUP["Domínio Support"]
            SUP["Entidades Ticket / Feedback<br/>(sem API ainda)"]
            NOTIF["Notificação Service<br/>(planejado)"]
        end

        BUS(["Event Bus interno<br/>ApplicationEventPublisher"])
        POOL["AsyncConfig<br/>analysisExecutor · extratoExecutor"]
    end

    CTRL --> D_USERS
    CTRL --> D_FINANCE
    CTRL --> D_PROG
    CTRL --> D_ANALYSIS
    CTRL --> D_GRAPHS
    CTRL --> D_SIM

    subgraph PERSIST["Persistência"]
        JPA["Spring Data JPA · Hibernate<br/>ddl-auto=validate"]
        FLY["Flyway<br/>V1 … V15"]
        PG[("PostgreSQL 16")]
    end

    subgraph EXT["Serviços externos"]
        GEM["Google Gemini API<br/>common/gemini/GeminiClient"]
        MAIL["Provedor de e-mail<br/>(planejado)"]
    end

    D_USERS --> JPA
    D_FINANCE --> JPA
    D_PROG --> JPA
    D_ANALYSIS --> JPA
    JPA --> PG
    FLY -->|"versiona o schema no boot"| PG

    %% Leitura entre domínios: integration API tipada, nunca tabela alheia
    D_ANALYSIS -.->|"FinanceIntegrationApi"| D_FINANCE
    D_ANALYSIS -.->|"ProgressionIntegrationApi"| D_PROG
    D_ANALYSIS -.->|"UserIntegrationApi"| D_USERS
    D_GRAPHS -.->|"AnalysisIntegrationApi"| D_ANALYSIS
    D_GRAPHS -.->|"ProgressionIntegrationApi"| D_PROG

    %% Escrita entre domínios: evento, nunca chamada
    D_FINANCE -.->|"DadosFinanceirosAlteradosEvent"| BUS
    D_PROG -.->|"DadosFinanceirosAlteradosEvent"| BUS
    D_USERS -.->|"DadosFinanceirosAlteradosEvent"| BUS
    BUS -->|"@TransactionalEventListener AFTER_COMMIT + @Async"| A_UC
    POOL -.-> A_UC
    POOL -.-> F_UC

    A_UC --> A_AN
    A_UC -->|"texto da recomendação"| GEM
    F_UC -->|"leitura de extrato PDF"| GEM

    %% Notificação por e-mail — previsto, ainda não implementado
    SUP -.->|"TicketCriadoEvent"| BUS
    A_UC -.->|"RecomendacaoGeradaEvent"| BUS
    BUS -.-> NOTIF
    NOTIF -.->|"template + envio"| MAIL
    NOTIF -.->|"registra envio / retry"| JPA

    classDef planejado stroke-dasharray: 6 4, opacity:0.75
    class NOTIF,MAIL planejado
```

> O caminho do e-mail (ticket → evento → serviço de notificação → provedor,
> com registro de envio e retry em falha) está detalhado em
> [../sequence/email-sequence.md](../sequence/email-sequence.md).

## 2. Anatomia de um domínio (fatia vertical)

Todo domínio repete a mesma estrutura — o que muda é só a regra de negócio.

```mermaid
%%{init: {'theme':'dark'}}%%
graph LR

    REQ["HTTP Request"] --> C

    subgraph API["api/"]
        C["controller<br/>@RestController<br/>valida entrada"]
        FAC["facade<br/>@Service<br/>orquestra casos de uso"]
    end

    subgraph APPL["application/"]
        CMD["usecase/commands<br/>escrita"]
        QRY["usecase/queries<br/>leitura"]
        DTO["dto/request · dto/response<br/>records"]
    end

    subgraph INT["internal/"]
        ENT["entity<br/>@Entity JPA"]
        MAP["mapper<br/>entidade ↔ DTO"]
        REPO["repository<br/>I*Repository"]
    end

    subgraph INTEG["integration/"]
        IAPI["api<br/>interface pública do domínio"]
        IIMP["implementation<br/>@Service"]
    end

    C --> FAC --> CMD
    FAC --> QRY
    CMD --> REPO
    QRY --> REPO
    REPO --> ENT
    CMD --> MAP
    QRY --> MAP
    MAP --> DTO
    DTO --> RESP["HTTP Response"]
    ENT --> DB[("PostgreSQL")]

    IIMP --> QRY
    OUTRO["Outro domínio"] -.->|"só enxerga a interface"| IAPI
    IAPI --> IIMP
```

## 3. Infraestrutura e deploy

```mermaid
%%{init: {'theme':'dark'}}%%
graph LR

    subgraph DEV["Desenvolvimento local"]
        COMPOSE["docker-compose.yml<br/>Postgres 16 + backend"]
    end

    subgraph BUILD["Build"]
        MULTI["Dockerfile multi-stage<br/>temurin 21-jdk → 21-jre<br/>usuário não-root"]
    end

    subgraph CLOUD["Azure"]
        APPSVC["App Service (container)<br/>perfil SPRING_PROFILES_ACTIVE=azure"]
        AZPG[("Azure Database for PostgreSQL<br/>Flexible Server B1ms<br/>Hikari pool = 5")]
        HEALTH["/actuator/health/liveness"]
    end

    COMPOSE --> MULTI --> APPSVC
    APPSVC -->|"TLS"| AZPG
    APPSVC --> HEALTH
    ENVVAR["Variáveis de ambiente<br/>JWT_SECRET · GEMINI_API_KEY<br/>SPRING_DATASOURCE_*<br/>(sem fallback em produção)"] --> APPSVC
```
