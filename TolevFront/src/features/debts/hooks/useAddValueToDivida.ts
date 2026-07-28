import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
  addValueToDivida,
  type AddValueToDividaPayload,
} from "../../../api/divida/add-value-to-divida";
import { dividaKeys } from "./dividaKeys";

/** Registers a payment against a debt and refreshes the debt data. */
export function useAddValueToDivida() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: AddValueToDividaPayload) => addValueToDivida(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dividaKeys.all });
    },
  });
}
