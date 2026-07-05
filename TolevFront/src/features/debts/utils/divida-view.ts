import type { DividaResponse } from "../../../types/divida";
import { bankColor, TIPO_ICON, type DividaView } from "../constants/dividas";
import { MoreHorizontal } from "lucide-react-native";

/** Maps a backend debt into the view-model used by the screens. */
export function toDividaView(d: DividaResponse): DividaView {
  return {
    id: d.id,
    nome: d.nome,
    banco: d.banco,
    bankColor: bankColor(d.banco),
    saldo: d.saldo,
    juros: d.juros,
    min: d.parcelaMinima,
    emocional: d.pesoEmocional,
    parcelas: d.quantidadeParcelas ?? Math.max(1, Math.ceil(d.saldo / Math.max(1, d.parcelaMinima))),
    parcelasPagas: (d.parcelas ?? [])
      .filter((p) => p.status === "PAGA")
      .map((p) => p.numeroParcela),
    icon: d.tipo ? TIPO_ICON[d.tipo] : MoreHorizontal,
    tipo: d.tipo ?? "OUTROS",
  };
}
