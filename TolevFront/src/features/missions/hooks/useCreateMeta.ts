import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createMeta } from "../../../api/meta/create-meta";
import type { MetaRequest } from "../../../types/meta";
import { metaKeys } from "./metaKeys";

/** Creates a goal and refreshes the goal list on success. */
export function useCreateMeta() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (request: MetaRequest) => createMeta(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: metaKeys.lists() });
    },
  });
}
