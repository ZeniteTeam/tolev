import { Text, View } from "react-native";
import { Stagger } from "../../../components";
import { colors, shadows } from "../../../theme";
import type { BancoUsuarioResponse } from "../../../types/extrato";
import { bancoVisual } from "../constants/bancos";
import { useBancosUsuario } from "../hooks/useBancosUsuario";

/**
 * O que a plataforma já sabe sobre os bancos deste usuário.
 *
 * Só aparece depois do primeiro extrato, porque antes disso não há nada de
 * verdadeiro a dizer: o app não conecta contas, e o banco de um lançamento
 * digitado à mão é desconhecido. O número que mais importa é o "importado até" —
 * é ele que explica, de antemão, por que parte do próximo extrato será pulada.
 */
export default function SeusBancosCard() {
  const { bancos, isLoading } = useBancosUsuario();

  if (isLoading) {
    return (
      <View className="bg-surface rounded-[18px] p-5 mb-3.5" style={shadows.card}>
        <View className="h-[120px] rounded-[12px] bg-[#F1F5F3]" />
      </View>
    );
  }

  if (bancos.length === 0) {
    return null;
  }

  return (
    <View className="bg-surface rounded-[18px] p-5 mb-3.5" style={shadows.card}>
      <Text className="font-bold text-[16px] text-ink">Seus bancos</Text>
      <Text className="text-[12px] text-muted mt-0.5 font-regular">
        O que já veio dos extratos que você importou
      </Text>

      <Stagger className="mt-4">
        {bancos.map((banco, i) => (
          <LinhaBanco
            key={banco.idBanco}
            banco={banco}
            ultimo={i === bancos.length - 1}
          />
        ))}
      </Stagger>
    </View>
  );
}

function LinhaBanco({
  banco,
  ultimo,
}: {
  banco: BancoUsuarioResponse;
  ultimo: boolean;
}) {
  const visual = bancoVisual(banco.codigoBanco);

  return (
    <View
      className="py-3"
      style={ultimo ? undefined : { borderBottomWidth: 1, borderBottomColor: "#F1F5F3" }}
    >
      <View className="flex-row items-center gap-3">
        <View
          className="w-9 h-9 rounded-full items-center justify-center"
          style={{ backgroundColor: visual.cor }}
        >
          <Text className="font-bold text-[13px]" style={{ color: visual.corTexto }}>
            {visual.sigla}
          </Text>
        </View>

        <View className="flex-1">
          <Text className="text-[14px] font-semibold text-ink" numberOfLines={1}>
            {banco.nome}
          </Text>
          <Text className="text-[12px] text-muted font-regular mt-0.5">
            {banco.quantidadeTransacoes}{" "}
            {banco.quantidadeTransacoes === 1 ? "lançamento" : "lançamentos"}
            {banco.importadoAte ? ` · até ${dataCurta(banco.importadoAte)}` : ""}
          </Text>
        </View>

        <View className="items-end">
          <Text className="text-[13px] font-bold text-coral-500">
            − {dinheiro(banco.totalSaidas)}
          </Text>
          <Text className="text-[12px] font-bold" style={{ color: colors.primary[500] }}>
            + {dinheiro(banco.totalEntradas)}
          </Text>
        </View>
      </View>
    </View>
  );
}

function dinheiro(valor: number): string {
  return `R$ ${valor.toLocaleString("pt-BR", { maximumFractionDigits: 0 })}`;
}

/** "2026-08-16" → "16/08/26". */
function dataCurta(iso: string): string {
  const [ano, mes, dia] = iso.split("-");
  return `${dia}/${mes}/${ano.slice(2)}`;
}
