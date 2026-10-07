import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
  addValueToDivida,
  type AddValueToDividaPayload,
} from "../../../api/divida/add-value-to-divida";
import { graphKeys } from "../../analysis/hooks/graphKeys";
import { dividaKeys } from "./dividaKeys";

export function useAddValueToDivida() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: AddValueToDividaPayload) => addValueToDivida(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dividaKeys.all });
      // A projeção de quitação sai do cronograma de parcelas: mexer na
      // dívida muda a data prevista, o saldo e todas as barras.
      queryClient.invalidateQueries({ queryKey: graphKeys.all });
    },
  });
}
