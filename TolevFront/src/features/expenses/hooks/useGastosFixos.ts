import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  confirmarGastosFixos,
  createGastoFixo,
  deleteGastoFixo,
  getGastosFixos,
  updateGastoFixo,
} from "../../../api/gasto-fixo/gastos-fixos";
import { useAuthStore } from "../../../store/authStore";
import type { GastoFixoRequest } from "../../../types/gastoFixo";
import { graphKeys } from "../../analysis/hooks/graphKeys";
import { gastoFixoKeys } from "./gastoFixoKeys";

/** Os gastos fixos do usuário, com o total já somado. */
export function useGastosFixos() {
  const userId = useAuthStore((s) => s.userId);
  const query = useQuery({
    queryKey: gastoFixoKeys.list(userId ?? 0),
    queryFn: () => getGastosFixos(userId as number),
    enabled: userId != null,
    retry: false,
  });

  const gastos = query.data ?? [];
  const total = gastos.reduce((s, g) => s + g.valor, 0);

  return { ...query, gastos, total };
}

/**
 * Invalida a lista e os gráficos juntos.
 *
 * Todo gasto fixo entra na conta de sobra da aba Projeções. Cadastrar um e ver
 * o "livre por dia" antigo seria pior do que não ter o número: sugeriria que o
 * cadastro não serviu para nada.
 */
function useInvalidarGastosFixos() {
  const queryClient = useQueryClient();
  return () => {
    queryClient.invalidateQueries({ queryKey: gastoFixoKeys.all });
    queryClient.invalidateQueries({ queryKey: graphKeys.all });
  };
}

export function useCreateGastoFixo() {
  const invalidar = useInvalidarGastosFixos();
  return useMutation({
    mutationFn: (payload: GastoFixoRequest) => createGastoFixo(payload),
    onSuccess: invalidar,
  });
}

export function useUpdateGastoFixo() {
  const invalidar = useInvalidarGastosFixos();
  return useMutation({
    mutationFn: ({ id, ...payload }: GastoFixoRequest & { id: number }) =>
      updateGastoFixo(id, payload),
    onSuccess: invalidar,
  });
}

export function useDeleteGastoFixo() {
  const userId = useAuthStore((s) => s.userId);
  const invalidar = useInvalidarGastosFixos();
  return useMutation({
    mutationFn: (id: number) => deleteGastoFixo(id, userId as number),
    onSuccess: invalidar,
  });
}

export function useConfirmarGastosFixos() {
  const userId = useAuthStore((s) => s.userId);
  const invalidar = useInvalidarGastosFixos();
  return useMutation({
    mutationFn: () => confirmarGastosFixos(userId as number),
    onSuccess: invalidar,
  });
}
