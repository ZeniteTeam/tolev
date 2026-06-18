import { useQuery } from "@tanstack/react-query";
import { getMetaById } from "../../../api/meta/get-meta-by-id";
import { metaKeys } from "./metaKeys";

/** Fetches a single goal by id. */
export function useMeta(id: number | undefined) {
  return useQuery({
    queryKey: metaKeys.detail(id ?? 0),
    queryFn: () => getMetaById(id as number),
    enabled: id != null,
  });
}
