import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createCategoria } from "../../../api/categoria/create-categoria";
import { deleteCategoria } from "../../../api/categoria/delete-categoria";
import { updateCategoria } from "../../../api/categoria/update-categoria";
import { useAuthStore } from "../../../store/authStore";
import type { CategoriaRequest } from "../../../types/transacao";
import { graphKeys } from "../../analysis/hooks/graphKeys";
import { categoriaKeys, transacaoKeys } from "./transacaoKeys";

/**
 * O catálogo alimenta tanto a grade de escolha quanto os gráficos por
 * categoria, então qualquer escrita invalida os três: categorias, transações
 * (que carregam nome e cor da categoria em cada linha) e gráficos.
 */
function useInvalidarCatalogo() {
  const queryClient = useQueryClient();
  return () => {
    queryClient.invalidateQueries({ queryKey: categoriaKeys.all });
    queryClient.invalidateQueries({ queryKey: transacaoKeys.all });
    queryClient.invalidateQueries({ queryKey: graphKeys.all });
  };
}

export function useCreateCategoria() {
  const invalidar = useInvalidarCatalogo();
  const userId = useAuthStore((s) => s.userId);
  return useMutation({
    mutationFn: (payload: Omit<CategoriaRequest, "idUsuario">) =>
      createCategoria({ ...payload, idUsuario: userId as number }),
    onSuccess: invalidar,
  });
}

export function useUpdateCategoria() {
  const invalidar = useInvalidarCatalogo();
  const userId = useAuthStore((s) => s.userId);
  return useMutation({
    mutationFn: ({ id, ...payload }: Omit<CategoriaRequest, "idUsuario"> & { id: number }) =>
      updateCategoria(id, { ...payload, idUsuario: userId as number }),
    onSuccess: invalidar,
  });
}

export function useDeleteCategoria() {
  const invalidar = useInvalidarCatalogo();
  const userId = useAuthStore((s) => s.userId);
  return useMutation({
    mutationFn: (id: number) => deleteCategoria(id, userId as number),
    onSuccess: invalidar,
  });
}
