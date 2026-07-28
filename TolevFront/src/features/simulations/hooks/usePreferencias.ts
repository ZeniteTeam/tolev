import { useQuery } from "@tanstack/react-query";
import { getPreferencias } from "../../../api/preferencias/get-preferencias";
import { useAuthStore } from "../../../store/authStore";
import { preferenciasKeys } from "./preferenciasKeys";

/** Server state for the authenticated user's financial preferences. */
export function usePreferencias() {
  const userId = useAuthStore((s) => s.userId);
  return useQuery({
    queryKey: preferenciasKeys.detail(userId ?? 0),
    queryFn: () => getPreferencias(userId as number),
    enabled: userId != null,
    staleTime: 60_000,
  });
}
