export const dividaKeys = {
  all: ["dividas"] as const,
  lists: () => [...dividaKeys.all, "list"] as const,
  list: (userId: number) => [...dividaKeys.lists(), userId] as const,
  details: () => [...dividaKeys.all, "detail"] as const,
  detail: (id: number) => [...dividaKeys.details(), id] as const,
};
