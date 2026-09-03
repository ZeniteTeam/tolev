export const preferenciasKeys = {
  all: ["preferencias"] as const,
  detail: (userId: number) => [...preferenciasKeys.all, userId] as const,
};
