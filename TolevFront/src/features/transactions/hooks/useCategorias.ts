import { useQuery } from "@tanstack/react-query";
import { getCategorias } from "../../../api/categoria/get-categorias";
import { useAuthStore } from "../../../store/authStore";
import type { TipoCategoriaGasto } from "../../../types/transacao";
import { categoriaKeys } from "./transacaoKeys";

/**
 * `tipo` filtra a lista para o lado que a pessoa está lançando — categoria de
 * despesa não aparece ao registrar receita. O catálogo muda muito pouco, então
 * fica em cache por bastante tempo.
 */
export function useCategorias(tipo?: TipoCategoriaGasto) {
  const userId = useAuthStore((s) => s.userId);
  const query = useQuery({
    queryKey: categoriaKeys.list(userId ?? 0),
    queryFn: () => getCategorias(userId as number),
    enabled: userId != null,
    staleTime: 5 * 60 * 1000,
    retry: false,
  });

  // Categoria antiga pode ter vindo sem tipo. Tratada como despesa em vez de
  // descartada: sumir da grade sem explicação é pior do que aparecer no lado
  // mais provável — quase tudo que se lança à mão é despesa.
  const categorias = (query.data ?? []).filter(
    (c) => tipo == null || (c.tipo ?? "DESPESA") === tipo,
  );

  return { ...query, categorias };
}
