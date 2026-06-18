import { useQuery } from "@tanstack/react-query";
import { getMetasByUser } from "../../../api/meta/get-metas-by-user";
import { useAuthStore } from "../../../store/authStore";
import { metaKeys } from "./metaKeys";

/** Lists the goals of the authenticated user. */
export function useMetas() {
  const userId = useAuthStore((s) => s.userId);
  return useQuery({
    queryKey: metaKeys.list(userId ?? 0),
    queryFn: () => getMetasByUser(userId as number),
    enabled: userId != null,
  });
}
