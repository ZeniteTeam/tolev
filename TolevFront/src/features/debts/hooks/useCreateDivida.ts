import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createDivida } from "../../../api/divida/create-divida";
import type { DividaRequest } from "../../../types/divida";
import { dividaKeys } from "./dividaKeys";

/** Creates a debt and refreshes the debt list. */
export function useCreateDivida() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: DividaRequest) => createDivida(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dividaKeys.lists() });
    },
  });
}
