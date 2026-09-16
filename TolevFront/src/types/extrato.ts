/**
 * Espelha a importação de extrato do módulo `finance` do backend
 * (ExtratoImportacaoResponse / BancoUsuarioResponse / BankResponse).
 */

/**
 * Em que pé está um upload.
 *
 * O ciclo é sempre PROCESSANDO → CONCLUIDA | FALHOU, e FALHOU cobre tanto erro
 * técnico quanto "esse extrato não trouxe nada novo": nos dois casos nada foi
 * gravado e o `erro` explica o porquê em português.
 */
export type StatusImportacao = "PROCESSANDO" | "CONCLUIDA" | "FALHOU";

/** GET /banks — o catálogo, para escolher de qual banco é o PDF. */
export interface BancoResponse {
  id: number;
  titulo: string;
  codigoBanco: string | null;
  agencia: number | null;
}

/**
 * POST /transactions/extrato e GET /transactions/extrato[/{id}].
 *
 * Uma forma só para as três fases: o campo `status` diz o que já está
 * preenchido. Todo o bloco de resultado vem `null` enquanto processa.
 */
export interface ImportacaoExtratoResponse {
  id: number;
  idBanco: number | null;
  nomeBanco: string | null;
  status: StatusImportacao;
  nomeArquivo: string | null;
  erro: string | null;
  /** O usuário já viu este resultado e mandou atualizar o app. */
  confirmada: boolean;
  criadoEm: string;
  concluidoEm: string | null;
  /** Até que data o banco já estava importado quando o PDF chegou. */
  marcoAnterior: string | null;
  periodoInicio: string | null;
  periodoFim: string | null;
  lancamentosLidos: number | null;
  lancamentosImportados: number | null;
  lancamentosPulados: number | null;
  semCategoria: number | null;
  totalEntradas: number | null;
  totalSaidas: number | null;
}

/**
 * GET /transactions/extrato/bancos — os bancos que este usuário de fato usa.
 *
 * Diferente de {@link BancoResponse}, que é o catálogo: aqui só aparece banco
 * com transação importada.
 */
export interface BancoUsuarioResponse {
  idBanco: number;
  nome: string;
  codigoBanco: string | null;
  quantidadeTransacoes: number;
  totalEntradas: number;
  totalSaidas: number;
  primeiraTransacao: string | null;
  ultimaTransacao: string | null;
  /** O marco: subir o extrato de novo só acrescenta o que for posterior. */
  importadoAte: string | null;
  ultimaImportacao: string | null;
}

/**
 * O PDF escolhido no seletor.
 *
 * Carrega as duas formas porque o `FormData` não é o mesmo nos dois lugares: no
 * celular a parte do arquivo é `{ uri, name, type }`, e no navegador tem de ser
 * um `File` de verdade. O seletor devolve os dois quando existem, e quem envia
 * escolhe.
 */
export interface ArquivoExtrato {
  uri: string;
  name: string;
  mimeType: string;
  size: number | null;
  /** Só na web: o `File` que o navegador entregou. */
  file?: File;
}
