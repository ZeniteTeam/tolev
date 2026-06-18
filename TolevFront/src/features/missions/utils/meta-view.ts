import type { MetaResponse } from "../../../types/meta";

export function metaProgressPct(meta: MetaResponse): number {
  if (meta.percentualQuitado != null) {
    return Math.max(0, Math.min(100, Math.round(meta.percentualQuitado)));
  }
  if (meta.valorMeta && meta.valorMeta > 0 && meta.progresso != null) {
    return Math.max(
      0,
      Math.min(100, Math.round((meta.progresso / meta.valorMeta) * 100)),
    );
  }
  return 0;
}
