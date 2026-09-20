export const gastoFixoKeys = {
  all: ["gastos-fixos"] as const,
  list: (userId: number) => [...gastoFixoKeys.all, "list", userId] as const,
};
