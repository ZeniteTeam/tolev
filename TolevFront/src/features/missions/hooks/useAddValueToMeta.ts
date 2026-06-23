import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
  addValueToMeta,
  type addValueToMetaRequest,
} from "../../../api/meta/add-value-to-meta";
import { metaKeys } from "./metaKeys";

export function useAddValueToMeta() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (request: addValueToMetaRequest) => addValueToMeta(request),
    onSuccess: (_data, { id }) => {
      queryClient.invalidateQueries({ queryKey: metaKeys.lists() });
      queryClient.invalidateQueries({ queryKey: metaKeys.detail(id) });
    },
  });
}
