import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deleteMeta } from "../../../api/meta/delete-meta";
import { metaKeys } from "./metaKeys";

/** Deletes a goal and refreshes the goal list on success. */
export function useDeleteMeta() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => deleteMeta(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: metaKeys.lists() });
    },
  });
}
