import type { DividaResponse, ParcelaResponse } from "../../../types/divida";
import { bankColor, TIPO_ICON, type DividaView, type ParcelaView } from "../constants/dividas";
import { MoreHorizontal } from "lucide-react-native";

function toParcelaView(p: ParcelaResponse): ParcelaView {
  const valor = p.valorTotal ?? 0;
  return {
    numero: p.numeroParcela,
    valor,
    // Parcelas criadas antes do cálculo de amortização não têm a quebra:
    // nelas o valor cheio era principal.
    principal: p.valorPrincipal ?? valor,
    juros: p.valorJuros ?? 0,
    status: p.status,
    vencimento: p.dataVencimento,
    pagamento: p.dataPagamento,
  };
}

export function toDividaView(d: DividaResponse): DividaView {
  const cronograma = (d.parcelas ?? []).map(toParcelaView);
  const somaParcelas = cronograma.reduce((s, p) => s + p.valor, 0);

  return {
    id: d.id,
    nome: d.nome,
    banco: d.banco,
    bankColor: bankColor(d.banco),
    saldo: d.saldo,
    juros: d.juros,
    min: d.parcelaMinima,
    emocional: d.pesoEmocional,
    parcelas: d.quantidadeParcelas ?? cronograma.length,
    parcelasPagas: cronograma.filter((p) => p.status === "PAGA").map((p) => p.numero),
    cronograma,
    totalAPagar: d.totalAPagar ?? somaParcelas,
    totalJuros: d.totalJuros ?? Math.max(0, somaParcelas - d.saldo),
    multaAtraso: d.multaAtraso ?? 0,
    jurosMora: d.jurosMora ?? 0,
    sistema: d.sistemaAmortizacao ?? "PRICE",
    regime: d.regimeJuros ?? "COMPOSTO",
    icon: d.tipo ? TIPO_ICON[d.tipo] : MoreHorizontal,
    tipo: d.tipo ?? "OUTROS",
  };
}
