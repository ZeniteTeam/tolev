import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createDivida } from "../../../api/divida/create-divida";
import type { DividaRequest } from "../../../types/divida";
import { graphKeys } from "../../analysis/hooks/graphKeys";
import { dividaKeys } from "./dividaKeys";

export function useCreateDivida() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: DividaRequest) => createDivida(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dividaKeys.lists() });
      // A projeção de quitação sai do cronograma de parcelas: mexer na
      // dívida muda a data prevista, o saldo e todas as barras.
      queryClient.invalidateQueries({ queryKey: graphKeys.all });
    },
  });
}
