import type { ArquivoExtrato, ImportacaoExtratoResponse } from "../../types/extrato";
import api from "../axios";

/**
 * Sobe o PDF e volta na hora, com a importação em PROCESSANDO — a leitura pelo
 * Gemini acontece no servidor, depois da resposta.
 */
export async function importExtrato(
  idUsuario: number,
  idBanco: number,
  arquivo: ArquivoExtrato,
): Promise<ImportacaoExtratoResponse> {
  const form = new FormData();

  if (arquivo.file) {
    // Web: o navegador já entregou um File, e é isso que o FormData dele aceita.
    form.append("arquivo", arquivo.file, arquivo.name);
  } else {
    // Celular: o FormData do React Native monta a parte do arquivo a partir de
    // `{ uri, name, type }`, não de um Blob. Daí o cast — o tipo do DOM não
    // descreve esse objeto, embora seja o que o runtime espera.
    form.append("arquivo", {
      uri: arquivo.uri,
      name: arquivo.name,
      type: arquivo.mimeType,
    } as unknown as Blob);
  }

  const response = await api.post<ImportacaoExtratoResponse>(
    "/transactions/extrato",
    form,
    {
      params: { idUsuario, idBanco },
      // Deixar o axios definir o Content-Type: ele precisa acrescentar o
      // `boundary` do multipart, e o padrão application/json do cliente faria o
      // servidor recusar o corpo inteiro.
      headers: { "Content-Type": "multipart/form-data" },
      // O PDF pode ter alguns megabytes numa rede ruim; os 15s do cliente
      // derrubariam o envio antes de ele chegar ao fim.
      timeout: 60_000,
    },
  );
  return response.data;
}
