# Arquitetura — Front-end (visão atual)

> Atualizado em 2026-09-16 a partir do código em `TolevFront/src/`.
> Stack: **Expo 57 · React Native 0.86 · React 19 · TypeScript · NativeWind 4 ·
> React Navigation 7 · TanStack Query 5 · Zustand 5 · Axios · React Hook Form + Zod ·
> Reanimated 4 / Moti · Lucide · Gifted Charts · Expo Secure Store**.

## 1. Camadas

```mermaid
%%{init: {'theme':'dark'}}%%
graph TB

    subgraph NAV["navigation/"]
        ROOT["RootNavigator<br/>gate: autenticado?"]
        TABS["MainTabs + TabBar"]
    end

    subgraph FEAT["features/ — fatia por domínio"]
        SCR["screens/<br/>composição da tela"]
        COMP["components/<br/>UI da feature"]
        HOOK["hooks/<br/>useQuery · useMutation · queryKeys"]
        SCH["schema/<br/>Zod → tipos inferidos"]
    end

    subgraph SHARED["Compartilhado"]
        UI["components/<br/>Button · Card · Field · Screen<br/>LineChart · Donut · Progress"]
        THEME["theme/<br/>colors · spacing · typography<br/>radius · shadows · motion"]
        TYPES["types/<br/>contratos da API"]
        UTIL["util/<br/>currency · date · masks · apiError"]
    end

    subgraph STATE["Estado"]
        ZUS["store/authStore.ts<br/>Zustand — sessão"]
        RQ["TanStack Query<br/>cache de servidor"]
        SEC["expo-secure-store<br/>JWT persistido"]
    end

    subgraph APIL["api/ — camada de acesso"]
        AX["api/axios.ts<br/>baseURL · interceptors"]
        FN["api/auth · conta · transacao · divida<br/>categoria · banco · extrato · graphs<br/>preferencias"]
    end

    BACK["Spring Boot API"]

    ROOT --> TABS --> SCR
    ROOT -.->|"lê sessão"| ZUS
    SCR --> COMP
    SCR --> HOOK
    SCR --> SCH
    COMP --> UI
    UI --> THEME
    HOOK --> RQ
    RQ --> FN
    FN --> AX
    AX -->|"HTTPS + Bearer"| BACK
    ZUS -->|"setAuthToken()"| AX
    ZUS <--> SEC
    FN --> TYPES
    SCH --> TYPES
```

**Regras que o desenho impõe**

- Nenhuma tela chama `axios` direto: passa sempre por `api/<recurso>/<operação>.ts`.
- Estado de servidor vive no TanStack Query; o Zustand guarda só sessão.
- Nada de cor/espaçamento hardcoded: tudo sai de `theme/` via NativeWind.

## 2. Ciclo de uma tela (leitura e escrita)

```mermaid
%%{init: {'theme':'dark'}}%%
sequenceDiagram
    autonumber
    participant S as Screen
    participant H as hook (useDividas)
    participant Q as TanStack Query
    participant A as api/divida/*.ts
    participant X as axios.ts
    participant B as Backend

    Note over S,B: Leitura
    S->>H: monta
    H->>Q: useQuery(dividaKeys.list(id))
    Q-->>S: cache quente → render imediato
    Q->>A: fetch em background
    A->>X: GET /dividas?idUsuario=
    X->>X: interceptor injeta Bearer
    X->>B: HTTPS
    B-->>X: 200 + JSON
    X-->>Q: DividaResponse[]
    Q-->>S: re-render com dado fresco

    Note over S,B: Escrita
    S->>S: React Hook Form + Zod validam
    S->>H: useCreateDivida.mutate(payload)
    H->>A: POST /dividas
    A->>X: body validado
    X->>B: HTTPS + Bearer
    B-->>X: 201 Created
    H->>Q: invalidateQueries(dividaKeys)
    Q->>B: refetch
    Q-->>S: lista atualizada
```

## 3. Sessão e resiliência

```mermaid
%%{init: {'theme':'dark'}}%%
graph LR
    LOGIN["POST /auth/login"] -->|"JWT"| ZUS["authStore"]
    ZUS --> SEC["SecureStore<br/>(sobrevive ao fechar o app)"]
    ZUS --> MEM["setAuthToken()<br/>cópia em memória"]
    MEM --> REQI["interceptor de request<br/>Authorization: Bearer"]
    RESI["interceptor de response"] -->|"401"| LOGOUT["onUnauthorized()<br/>limpa sessão → tela de login"]
    BOOT["Boot do app"] --> SEC
    SEC -->|"token válido"| MEM
    ENVU["EXPO_PUBLIC_API_URL<br/>(mesmo bundle: local ou Azure)"] --> BASE["baseURL"]
```
