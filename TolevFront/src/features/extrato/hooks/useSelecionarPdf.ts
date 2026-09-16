import * as DocumentPicker from "expo-document-picker";
import { useCallback, useState } from "react";
import type { ArquivoExtrato } from "../../../types/extrato";

/** O backend recusa acima disso (spring.servlet.multipart.max-file-size). */
const TAMANHO_MAXIMO_BYTES = 10 * 1024 * 1024;

/**
 * Abre o seletor de arquivos do sistema e devolve o PDF escolhido.
 *
 * Só PDF por enquanto — é o formato que todo banco exporta e o único que o
 * Gemini lê com a estrutura do extrato preservada. O filtro do seletor já
 * restringe, mas conferimos de novo: no Android o filtro por tipo é uma
 * sugestão, e há gerenciadores de arquivo que deixam escolher qualquer coisa.
 */
export function useSelecionarPdf() {
  const [arquivo, setArquivo] = useState<ArquivoExtrato | null>(null);
  const [erro, setErro] = useState<string | null>(null);

  const selecionar = useCallback(async () => {
    setErro(null);
    const resultado = await DocumentPicker.getDocumentAsync({
      type: "application/pdf",
      copyToCacheDirectory: true,
      multiple: false,
    });

    if (resultado.canceled) {
      return;
    }

    const escolhido = resultado.assets[0];
    if (!escolhido) {
      return;
    }

    const ehPdf =
      escolhido.mimeType === "application/pdf" ||
      escolhido.name?.toLowerCase().endsWith(".pdf");

    if (!ehPdf) {
      setErro("Por enquanto só aceitamos extrato em PDF. Baixe o arquivo em PDF no app do seu banco.");
      return;
    }

    if (escolhido.size != null && escolhido.size > TAMANHO_MAXIMO_BYTES) {
      setErro("Esse arquivo passa de 10 MB. Tente exportar um período menor no app do banco.");
      return;
    }

    setArquivo({
      uri: escolhido.uri,
      name: escolhido.name ?? "extrato.pdf",
      mimeType: "application/pdf",
      size: escolhido.size ?? null,
      // Só vem preenchido na web; no celular fica undefined e o envio usa a uri.
      file: escolhido.file,
    });
  }, []);

  const limpar = useCallback(() => {
    setArquivo(null);
    setErro(null);
  }, []);

  return { arquivo, erro, selecionar, limpar };
}
