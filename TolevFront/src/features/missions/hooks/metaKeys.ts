/** Centralised React Query keys for the Meta (goal) feature. */
export const metaKeys = {
  all: ["metas"] as const,
  lists: () => [...metaKeys.all, "list"] as const,
  list: (userId: number) => [...metaKeys.lists(), userId] as const,
  details: () => [...metaKeys.all, "detail"] as const,
  detail: (id: number) => [...metaKeys.details(), id] as const,
};
