import { useMutation, useQueryClient } from "@tanstack/react-query";
import { registrarPagamento } from "../../../api/divida/registrar-pagamento";
import type { RegistrarPagamentoRequest } from "../../../types/divida";
import { graphKeys } from "../../analysis/hooks/graphKeys";
import { dividaKeys } from "./dividaKeys";

export function useRegistrarPagamento() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: RegistrarPagamentoRequest) => registrarPagamento(payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dividaKeys.all });
      // A projeção de quitação sai do cronograma de parcelas: mexer na
      // dívida muda a data prevista, o saldo e todas as barras.
      queryClient.invalidateQueries({ queryKey: graphKeys.all });
    },
  });
}
