export const transacaoKeys = {
  all: ["transacoes"] as const,
  lists: () => [...transacaoKeys.all, "list"] as const,
  list: (userId: number) => [...transacaoKeys.lists(), userId] as const,
};

/** Categorias e contas alimentam o formulário, por isso têm chave própria. */
export const categoriaKeys = {
  all: ["categorias"] as const,
  list: (userId: number) => [...categoriaKeys.all, "list", userId] as const,
};

export const contaKeys = {
  all: ["contas"] as const,
  list: (userId: number) => [...contaKeys.all, "list", userId] as const,
};
