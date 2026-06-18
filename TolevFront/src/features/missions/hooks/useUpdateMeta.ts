import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateMeta } from "../../../api/meta/update-meta";
import type { MetaRequest } from "../../../types/meta";
import { metaKeys } from "./metaKeys";

/** Updates a goal and refreshes the affected queries on success. */
export function useUpdateMeta() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: MetaRequest }) =>
      updateMeta(id, request),
    onSuccess: (_data, { id }) => {
      queryClient.invalidateQueries({ queryKey: metaKeys.lists() });
      queryClient.invalidateQueries({ queryKey: metaKeys.detail(id) });
    },
  });
}
