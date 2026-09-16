import api from "../axios";

/**
 * Apaga as transações daquele upload e devolve o período do banco.
 *
 * Serve também para dispensar uma importação que falhou: não há nada a apagar,
 * mas o registro sai do histórico e o aviso de erro some da tela.
 */
export async function desfazerImportacao(
  idUsuario: number,
  idImportacao: number,
): Promise<void> {
  await api.delete(`/transactions/extrato/${idImportacao}`, {
    params: { idUsuario },
  });
}
