export const extratoKeys = {
  all: ["extrato"] as const,
  /** Histórico de importações — é esta query que o app fica relendo. */
  importacoes: (userId: number) => [...extratoKeys.all, "importacoes", userId] as const,
  /** Os bancos que já trouxeram transação para este usuário. */
  bancosUsuario: (userId: number) => [...extratoKeys.all, "bancos-usuario", userId] as const,
};

/** Catálogo de bancos: é global e praticamente imutável, então não leva userId. */
export const bancoKeys = {
  all: ["bancos"] as const,
};
