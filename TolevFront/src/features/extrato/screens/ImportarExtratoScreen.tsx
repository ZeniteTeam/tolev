import { useNavigation } from "@react-navigation/native";
import { FileText, RefreshCw, Upload } from "lucide-react-native";
import { useMemo, useState } from "react";
import { Pressable, Text, View } from "react-native";
import { StepScaffold } from "../../../components";
import { colors, shadows } from "../../../theme";
import { getApiErrorMessage } from "../../../util/apiError";
import BancoPicker from "../components/BancoPicker";
import { comoEncontrarExtrato } from "../constants/bancos";
import { useBancos } from "../hooks/useBancos";
import { useImportarExtrato } from "../hooks/useImportarExtrato";
import { useSelecionarPdf } from "../hooks/useSelecionarPdf";

const TOTAL_ETAPAS = 3;

/**
 * O caminho até o PDF chegar ao servidor, em três telas.
 *
 * A do meio é o motivo de existirem três: quase ninguém sabe de cabeça onde o
 * app do banco esconde o extrato em PDF, e mandar a pessoa procurar sozinha é
 * onde o recurso morre. As instruções vêm depois da escolha do banco justamente
 * para poderem ser específicas daquele app, em vez de um texto genérico que não
 * ajuda em nenhum.
 *
 * O envio não espera a leitura: assim que o servidor aceita o arquivo, a tela de
 * acompanhamento assume e esta sai de cena.
 */
export default function ImportarExtratoScreen() {
  const navigation = useNavigation<any>();
  const { bancos, isLoading: bancosCarregando } = useBancos();
  const { arquivo, erro: erroArquivo, selecionar, limpar } = useSelecionarPdf();
  const importar = useImportarExtrato();

  const [etapa, setEtapa] = useState(1);
  const [idBanco, setIdBanco] = useState<number | null>(null);
  const [erroEnvio, setErroEnvio] = useState<string | null>(null);

  const banco = useMemo(
    () => bancos.find((b) => b.id === idBanco) ?? null,
    [bancos, idBanco],
  );
  const instrucoes = comoEncontrarExtrato(banco?.codigoBanco);

  function voltar() {
    if (etapa === 1) {
      navigation.goBack();
      return;
    }
    setErroEnvio(null);
    setEtapa((e) => e - 1);
  }

  async function enviar() {
    if (idBanco == null || arquivo == null) {
      return;
    }
    setErroEnvio(null);
    try {
      const importacao = await importar.mutateAsync({ idBanco, arquivo });
      // `replace` e não `navigate`: voltar para o formulário depois de o
      // arquivo já ter sido aceito só levaria a um segundo envio do mesmo PDF.
      navigation.replace("ExtratoProcessando", { idImportacao: importacao.id });
    } catch (error) {
      setErroEnvio(
        getApiErrorMessage(error, "Não foi possível enviar seu extrato. Tente de novo."),
      );
    }
  }

  if (etapa === 1) {
    return (
      <StepScaffold
        step={1}
        total={TOTAL_ETAPAS}
        onBack={voltar}
        title="De qual banco é o extrato?"
        subtitle="Cada banco exporta o extrato de um jeito. Escolha o seu para a gente te mostrar onde encontrar."
        onContinue={() => setEtapa(2)}
        continueDisabled={idBanco == null}
      >
        <BancoPicker
          bancos={bancos}
          selecionado={idBanco}
          onChange={setIdBanco}
          carregando={bancosCarregando}
        />
      </StepScaffold>
    );
  }

  if (etapa === 2) {
    return (
      <StepScaffold
        step={2}
        total={TOTAL_ETAPAS}
        onBack={voltar}
        title={`Onde achar seu extrato${banco ? ` no ${banco.titulo}` : ""}`}
        subtitle="Baixe o extrato em PDF no app do banco. Depois é só voltar aqui."
        onContinue={() => setEtapa(3)}
        continueLabel="Já tenho o PDF"
      >
        <View className="gap-4">
          {instrucoes.passos.map((passo, i) => (
            <View key={i} className="flex-row gap-3.5">
              <View className="w-7 h-7 rounded-full bg-primary-100 items-center justify-center mt-0.5">
                <Text className="text-[13px] font-bold text-primary-700">{i + 1}</Text>
              </View>
              <Text className="flex-1 text-[15px] text-ink font-regular leading-[22px]">
                {passo}
              </Text>
            </View>
          ))}

          {instrucoes.observacao ? (
            <View className="bg-primary-25 rounded-[14px] px-4 py-3.5 mt-1">
              <Text className="text-[13px] text-ink font-regular leading-[19px]">
                {instrucoes.observacao}
              </Text>
            </View>
          ) : null}
        </View>
      </StepScaffold>
    );
  }

  return (
    <StepScaffold
      step={3}
      total={TOTAL_ETAPAS}
      onBack={voltar}
      title="Envie o arquivo"
      subtitle="Só PDF por enquanto — é o formato que todos os bancos exportam."
      onContinue={enviar}
      continueLabel={importar.isPending ? "Enviando…" : "Importar extrato"}
      continueDisabled={arquivo == null || importar.isPending}
      error={erroEnvio ?? erroArquivo ?? undefined}
      help={{
        label: "Não achei o PDF",
        title: `Onde achar seu extrato${banco ? ` no ${banco.titulo}` : ""}`,
        body: "O extrato precisa estar salvo no celular antes de você escolher o arquivo aqui.",
        ondeEncontrar: instrucoes.passos,
        footer: instrucoes.observacao,
      }}
    >
      {arquivo ? (
        <ArquivoEscolhido
          nome={arquivo.name}
          tamanho={arquivo.size}
          onTrocar={selecionar}
          onRemover={limpar}
        />
      ) : (
        <Pressable
          onPress={selecionar}
          className="items-center justify-center rounded-[18px] bg-surface px-6 py-9 active:opacity-90"
          style={{
            borderWidth: 1.5,
            borderStyle: "dashed",
            borderColor: colors.primary[200],
          }}
        >
          <View className="w-14 h-14 rounded-full bg-primary-100 items-center justify-center mb-3.5">
            <Upload size={24} color={colors.primary[700]} strokeWidth={2} />
          </View>
          <Text className="text-[16px] font-bold text-ink">Escolher PDF</Text>
          <Text className="text-[13px] text-muted font-regular mt-1 text-center leading-[19px]">
            Selecione o extrato que você baixou{banco ? ` do ${banco.titulo}` : ""}
          </Text>
        </Pressable>
      )}

      <View className="bg-primary-25 rounded-[14px] px-4 py-3.5 mt-4">
        <Text className="text-[13px] text-ink font-regular leading-[19px]">
          A leitura demora um pouco. Você pode sair desta tela e continuar usando
          o app — a gente te avisa quando terminar.
        </Text>
      </View>
    </StepScaffold>
  );
}

function ArquivoEscolhido({
  nome,
  tamanho,
  onTrocar,
  onRemover,
}: {
  nome: string;
  tamanho: number | null;
  onTrocar: () => void;
  onRemover: () => void;
}) {
  return (
    <View className="bg-surface rounded-[18px] p-5" style={shadows.card}>
      <View className="flex-row items-center gap-3.5">
        <View className="w-11 h-11 rounded-[12px] bg-primary-100 items-center justify-center">
          <FileText size={20} color={colors.primary[700]} strokeWidth={2} />
        </View>
        <View className="flex-1">
          <Text className="text-[15px] font-semibold text-ink" numberOfLines={1}>
            {nome}
          </Text>
          <Text className="text-[12px] text-muted font-regular mt-0.5">
            {formatarTamanho(tamanho)}
          </Text>
        </View>
      </View>

      <View className="flex-row gap-2.5 mt-4">
        <Pressable
          onPress={onTrocar}
          className="flex-1 h-11 rounded-pill bg-primary-100 flex-row items-center justify-center gap-2 active:opacity-90"
        >
          <RefreshCw size={15} color={colors.primary[700]} strokeWidth={2} />
          <Text className="text-[14px] font-bold text-primary-700">Trocar</Text>
        </Pressable>
        <Pressable
          onPress={onRemover}
          className="px-5 h-11 rounded-pill items-center justify-center active:opacity-70"
        >
          <Text className="text-[14px] font-bold text-muted">Remover</Text>
        </Pressable>
      </View>
    </View>
  );
}

function formatarTamanho(bytes: number | null): string {
  if (bytes == null) {
    return "PDF";
  }
  const mb = bytes / (1024 * 1024);
  return mb >= 1
    ? `PDF · ${mb.toFixed(1).replace(".", ",")} MB`
    : `PDF · ${Math.max(1, Math.round(bytes / 1024))} KB`;
}
