import { useNavigation, useRoute } from "@react-navigation/native";
import { AnimatePresence, MotiView } from "moti";
import {
  ArrowLeft,
  CheckCircle2,
  FileSearch,
  RotateCcw,
  TriangleAlert,
} from "lucide-react-native";
import { Pressable, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { useMensagemTemporaria } from "../../../hooks";
import { colors, shadows } from "../../../theme";
import type { ImportacaoExtratoResponse } from "../../../types/extrato";
import { useConfirmarExtrato } from "../hooks/useConfirmarExtrato";
import { useDesfazerExtrato } from "../hooks/useDesfazerExtrato";
import { useImportacoesExtrato } from "../hooks/useImportacoesExtrato";

/**
 * Acompanha um extrato sendo lido — e é uma tela que dá para abandonar.
 *
 * Essa é a ideia inteira: enquanto o Gemini lê o PDF, ninguém precisa ficar
 * olhando. O estado mora no servidor, então sair daqui (ou fechar o app) não
 * cancela nada, e o aviso de "pronto" espera o usuário voltar. Por isso o botão
 * de sair é destacado em vez de escondido: ficar parado aqui não acelera nada.
 *
 * Quando termina, nada muda sozinho embaixo do usuário. Ele toca em "Atualizar
 * meus dados" e só então as telas recarregam — trocar os números de alguém que
 * está no meio de outra coisa seria pior do que esperar um toque.
 */
export default function ExtratoProcessandoScreen() {
  const navigation = useNavigation<any>();
  const route = useRoute<any>();
  const insets = useSafeAreaInsets();

  const idImportacao: number | undefined = route.params?.idImportacao;
  const { importacoes, isLoading } = useImportacoesExtrato();
  const confirmar = useConfirmarExtrato();
  const desfazer = useDesfazerExtrato();

  const importacao =
    importacoes.find((i) => i.id === idImportacao) ??
    // Chegou aqui pelo aviso, sem id: mostra a mais recente que espera resposta.
    importacoes.find((i) => !i.confirmada) ??
    null;

  async function atualizarApp() {
    if (!importacao) {
      return;
    }
    try {
      await confirmar.mutateAsync(importacao.id);
    } catch {
      // Confirmar é conveniência: se o servidor não responder, as telas ainda
      // recarregam na próxima abertura. Não vale barrar a saída por isso.
    }
    voltarAoApp();
  }

  async function descartar() {
    if (!importacao) {
      return;
    }
    try {
      await desfazer.mutateAsync(importacao.id);
    } catch {
      // Mesmo raciocínio: o registro fica, e o aviso volta depois.
    }
    voltarAoApp();
  }

  function voltarAoApp() {
    if (navigation.canGoBack()) {
      navigation.goBack();
      return;
    }
    navigation.navigate("Main", { screen: "Financas" });
  }

  return (
    <View className="flex-1 bg-bg">
      <View
        className="flex-row items-center px-5 pb-2"
        style={{ paddingTop: insets.top + 8 }}
      >
        <Pressable
          onPress={voltarAoApp}
          hitSlop={10}
          className="w-9 h-9 rounded-full items-center justify-center active:opacity-70"
        >
          <ArrowLeft size={24} color={colors.text.secondary} strokeWidth={2.4} />
        </Pressable>
      </View>

      <View className="flex-1 px-6 pt-6">
        {isLoading && !importacao ? (
          <Aguardando />
        ) : !importacao ? (
          <SemImportacao onVoltar={voltarAoApp} />
        ) : importacao.status === "PROCESSANDO" ? (
          <Processando importacao={importacao} onSair={voltarAoApp} />
        ) : importacao.status === "FALHOU" ? (
          <Falhou
            importacao={importacao}
            ocupado={desfazer.isPending}
            onDescartar={descartar}
            onTentarDeNovo={() => navigation.replace("ImportarExtrato")}
          />
        ) : (
          <Concluida
            importacao={importacao}
            ocupado={confirmar.isPending}
            onAtualizar={atualizarApp}
          />
        )}
      </View>
    </View>
  );
}

function Aguardando() {
  return (
    <View className="gap-3">
      <View className="h-[120px] rounded-[18px] bg-[#F1F5F3]" />
      <View className="h-[180px] rounded-[18px] bg-[#F1F5F3]" />
    </View>
  );
}

function SemImportacao({ onVoltar }: { onVoltar: () => void }) {
  return (
    <View className="flex-1 items-center justify-center gap-4">
      <Text className="text-[16px] text-muted font-regular text-center leading-[23px]">
        Não encontramos nenhuma importação em andamento.
      </Text>
      <BotaoPrimario label="Voltar" onPress={onVoltar} />
    </View>
  );
}

function Processando({
  importacao,
  onSair,
}: {
  importacao: ImportacaoExtratoResponse;
  onSair: () => void;
}) {
  return (
    <View className="flex-1">
      <View className="items-center pt-6">
        <MotiView
          from={{ scale: 0.94, opacity: 0.75 }}
          animate={{ scale: 1.04, opacity: 1 }}
          transition={{ type: "timing", duration: 1400, loop: true }}
          style={{
            width: 96,
            height: 96,
            borderRadius: 48,
            backgroundColor: colors.primary[100],
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          <FileSearch size={38} color={colors.primary[700]} strokeWidth={1.9} />
        </MotiView>

        <Text className="font-bold text-[24px] text-ink mt-7 text-center leading-[31px]">
          Estamos lendo seu extrato
        </Text>
        <Text className="text-[15px] text-muted font-regular mt-2.5 text-center leading-[22px]">
          {importacao.nomeBanco
            ? `Analisando o PDF do ${importacao.nomeBanco} e separando cada lançamento por categoria.`
            : "Analisando o PDF e separando cada lançamento por categoria."}
        </Text>
      </View>

      <View className="bg-surface rounded-[18px] p-5 mt-8" style={shadows.card}>
        <Text className="font-bold text-[15px] text-ink">
          Pode fechar esta tela
        </Text>
        <Text className="text-[14px] text-muted font-regular mt-2 leading-[21px]">
          A leitura continua no servidor mesmo com o app fechado. Quando terminar,
          um aviso aparece para você atualizar seus dados.
        </Text>
      </View>

      <View className="flex-1 justify-end pb-8">
        <BotaoPrimario label="Continuar usando o app" onPress={onSair} />
      </View>
    </View>
  );
}

function Concluida({
  importacao,
  ocupado,
  onAtualizar,
}: {
  importacao: ImportacaoExtratoResponse;
  ocupado: boolean;
  onAtualizar: () => void;
}) {
  const importados = importacao.lancamentosImportados ?? 0;
  const pulados = importacao.lancamentosPulados ?? 0;
  const semCategoria = importacao.semCategoria ?? 0;

  return (
    <View className="flex-1">
      <View className="items-center pt-6">
        <MotiView
          from={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ type: "spring", damping: 14 }}
          style={{
            width: 96,
            height: 96,
            borderRadius: 48,
            backgroundColor: colors.primary[100],
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          <CheckCircle2 size={40} color={colors.primary[700]} strokeWidth={1.9} />
        </MotiView>

        <Text className="font-bold text-[24px] text-ink mt-7 text-center leading-[31px]">
          Extrato importado
        </Text>
        <Text className="text-[15px] text-muted font-regular mt-2.5 text-center leading-[22px]">
          {importados === 1
            ? "1 lançamento novo entrou nas suas finanças."
            : `${importados} lançamentos novos entraram nas suas finanças.`}
        </Text>
      </View>

      <View className="bg-surface rounded-[18px] p-5 mt-8" style={shadows.card}>
        <Linha rotulo="Banco" valor={importacao.nomeBanco ?? "—"} />
        <Linha
          rotulo="Período importado"
          valor={periodo(importacao.periodoInicio, importacao.periodoFim)}
        />
        {pulados > 0 ? (
          <Linha
            rotulo="Já estavam aqui"
            valor={`${pulados} ${pulados === 1 ? "lançamento" : "lançamentos"}`}
            // O número mais confuso da tela: o extrato mensal sempre repete o
            // começo do período, e sem essa linha o total "some" sem explicação.
            nota={
              importacao.marcoAnterior
                ? `Esse banco já estava importado até ${dataCurta(importacao.marcoAnterior)}.`
                : undefined
            }
          />
        ) : null}
        {semCategoria > 0 ? (
          <Linha
            rotulo="Sem categoria"
            valor={`${semCategoria} ${semCategoria === 1 ? "lançamento" : "lançamentos"}`}
            nota="Você pode categorizar depois, em Finanças."
          />
        ) : null}
        <Linha rotulo="Entradas" valor={dinheiro(importacao.totalEntradas)} ultimo={semCategoria === 0 && pulados === 0} />
        <Linha rotulo="Saídas" valor={dinheiro(importacao.totalSaidas)} ultimo />
      </View>

      <View className="flex-1 justify-end pb-8">
        <BotaoPrimario
          label={ocupado ? "Atualizando…" : "Atualizar meus dados"}
          onPress={ocupado ? undefined : onAtualizar}
          desabilitado={ocupado}
        />
      </View>
    </View>
  );
}

function Falhou({
  importacao,
  ocupado,
  onDescartar,
  onTentarDeNovo,
}: {
  importacao: ImportacaoExtratoResponse;
  ocupado: boolean;
  onDescartar: () => void;
  onTentarDeNovo: () => void;
}) {
  // A explicação some em 3s: ela diz o que fazer uma vez, e depois disso só
  // repete a má notícia acima dos dois botões que resolvem de fato.
  const motivo = useMensagemTemporaria(
    importacao.erro ??
      "Algo deu errado ao ler esse extrato. Tente enviar o arquivo de novo.",
    importacao.id,
  );

  return (
    <View className="flex-1">
      <View className="items-center pt-6">
        <View
          className="w-24 h-24 rounded-full items-center justify-center"
          style={{ backgroundColor: "#FEE7E1" }}
        >
          <TriangleAlert size={38} color={colors.coral[500]} strokeWidth={1.9} />
        </View>

        <Text className="font-bold text-[24px] text-ink mt-7 text-center leading-[31px]">
          Não deu para importar
        </Text>
        <AnimatePresence>
          {motivo ? (
            <MotiView
              from={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              transition={{ type: "timing", duration: 200 }}
            >
              <Text className="text-[15px] text-muted font-regular mt-2.5 text-center leading-[22px]">
                {motivo}
              </Text>
            </MotiView>
          ) : null}
        </AnimatePresence>
      </View>

      <View className="flex-1 justify-end pb-8 gap-2.5">
        <BotaoPrimario
          label="Enviar outro extrato"
          onPress={onTentarDeNovo}
          icone={<RotateCcw size={17} color="#fff" strokeWidth={2.2} />}
        />
        <Pressable
          onPress={ocupado ? undefined : onDescartar}
          className="h-12 rounded-pill items-center justify-center active:opacity-70"
        >
          <Text className="text-[15px] font-bold text-muted">
            {ocupado ? "Dispensando…" : "Dispensar"}
          </Text>
        </Pressable>
      </View>
    </View>
  );
}

function Linha({
  rotulo,
  valor,
  nota,
  ultimo = false,
}: {
  rotulo: string;
  valor: string;
  nota?: string;
  ultimo?: boolean;
}) {
  return (
    <View
      className="py-[11px]"
      style={ultimo ? undefined : { borderBottomWidth: 1, borderBottomColor: "#F1F5F3" }}
    >
      <View className="flex-row items-center justify-between gap-3">
        <Text className="text-[14px] text-muted font-regular">{rotulo}</Text>
        <Text className="text-[14px] font-bold text-ink text-right flex-shrink" numberOfLines={1}>
          {valor}
        </Text>
      </View>
      {nota ? (
        <Text className="text-[12px] text-muted font-regular mt-1 leading-[17px]">{nota}</Text>
      ) : null}
    </View>
  );
}

function BotaoPrimario({
  label,
  onPress,
  desabilitado = false,
  icone,
}: {
  label: string;
  onPress?: () => void;
  desabilitado?: boolean;
  icone?: React.ReactNode;
}) {
  return (
    <Pressable
      onPress={onPress}
      className="h-[54px] rounded-pill flex-row items-center justify-center gap-2 active:scale-[0.99]"
      style={{
        backgroundColor: desabilitado ? colors.primary[100] : colors.coral[500],
      }}
    >
      {icone}
      <Text
        className="font-bold text-[17px]"
        style={{ color: desabilitado ? colors.text.secondary : colors.surface }}
      >
        {label}
      </Text>
    </Pressable>
  );
}

function dinheiro(valor: number | null): string {
  if (valor == null) {
    return "—";
  }
  return `R$ ${valor.toLocaleString("pt-BR", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })}`;
}

/** "2026-08-16" → "16/08". O ano é ruído quando tudo é do mesmo período. */
function dataCurta(iso: string): string {
  const [, mes, dia] = iso.split("-");
  return `${dia}/${mes}`;
}

function periodo(inicio: string | null, fim: string | null): string {
  if (!inicio || !fim) {
    return "—";
  }
  return inicio === fim
    ? dataCurta(inicio)
    : `${dataCurta(inicio)} a ${dataCurta(fim)}`;
}
