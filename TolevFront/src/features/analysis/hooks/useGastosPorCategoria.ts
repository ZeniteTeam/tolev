import { useMemo, useState } from "react";
import type { BancoUsuarioResponse } from "../../../types/extrato";
import type { PeriodoGastos } from "../../../types/graphs";
import { useBancosUsuario } from "../../extrato/hooks/useBancosUsuario";
import { useSpendingByCategory } from "./useSpendingByCategory";

type Faixa = { inicio: string; fim: string };

/**
 * Os gastos por categoria, com uma saída para o caso que mais confunde: o
 * extrato entrou, e a tela continua vazia.
 *
 * Acontece porque as duas datas não têm relação nenhuma. O gráfico olha o mês
 * corrente; o extrato cobre o período que o banco exportou, que pode ser
 * qualquer pedaço do passado. Importar em setembro um PDF de julho grava tudo
 * certo e não muda um pixel — e a tela ainda diz "nenhuma despesa neste mês",
 * que é verdade e não ajuda em nada.
 *
 * Então quando o período escolhido vem vazio e existem gastos importados fora
 * dele, o gráfico passa a mostrar o período do extrato, dizendo qual é. É uma
 * troca visível e reversível, nunca silenciosa: quem quiser o mês corrente vazio
 * tem `verPeriodoEscolhido()` — mostrar o mês vazio sem avisar que existe dado
 * em outro lugar é justamente o problema.
 *
 * @param idBanco recorte por banco; a faixa do extrato acompanha o recorte
 */
export function useGastosPorCategoria(meses: number, idBanco: number | null) {
  const { bancos } = useBancosUsuario();
  // Pedido explícito de ficar no período escolhido, mesmo vazio. Guarda qual
  // período foi recusado, e não um sim/não: recusar o mês corrente não é dizer
  // nada sobre os últimos seis meses, que o usuário ainda não olhou.
  const [recusouExtrato, setRecusouExtrato] = useState<number | null>(null);

  const escolhido = useSpendingByCategory({ meses }, idBanco);

  const faixaExtrato = useMemo(() => faixaDe(bancos, idBanco), [bancos, idBanco]);

  const escolhidoVazio = escolhido.data != null && escolhido.data.totalTransacoes === 0;
  const deveSeguirExtrato =
    escolhidoVazio && faixaExtrato != null && recusouExtrato !== meses;

  const doExtrato = useSpendingByCategory(
    faixaExtrato ?? { meses },
    idBanco,
    deveSeguirExtrato,
  );

  const seguindoExtrato = deveSeguirExtrato && doExtrato.data != null;

  return {
    data: seguindoExtrato ? doExtrato.data : escolhido.data,
    isLoading: escolhido.isLoading || (deveSeguirExtrato && doExtrato.isLoading),
    /** O gráfico está mostrando o período do extrato, não o que foi escolhido. */
    seguindoExtrato,
    /** A faixa coberta pelos gastos importados, quando existe. */
    faixaExtrato,
    /** Volta para o período escolhido, mesmo vazio. */
    verPeriodoEscolhido: () => setRecusouExtrato(meses),
  };
}

/**
 * Até onde vão os gastos importados: de um banco, ou de todos.
 *
 * Sai das transações já gravadas (`primeiraTransacao`/`ultimaTransacao`) e não
 * da última importação — dois extratos do mesmo banco cobrem um período maior do
 * que qualquer um deles sozinho, e é o período todo que tem dado para mostrar.
 */
function faixaDe(bancos: BancoUsuarioResponse[], idBanco: number | null): Faixa | null {
  const relevantes = (idBanco == null ? bancos : bancos.filter((b) => b.idBanco === idBanco))
    .filter((b) => b.primeiraTransacao != null && b.ultimaTransacao != null);

  if (relevantes.length === 0) {
    return null;
  }

  // Datas ISO ("2026-07-02") comparam certo como texto — mesma largura, do ano
  // para o dia. Nada de Date aqui: converter só para comparar traria fuso junto.
  return {
    inicio: relevantes.reduce(
      (menor, b) => (b.primeiraTransacao! < menor ? b.primeiraTransacao! : menor),
      relevantes[0].primeiraTransacao!,
    ),
    fim: relevantes.reduce(
      (maior, b) => (b.ultimaTransacao! > maior ? b.ultimaTransacao! : maior),
      relevantes[0].ultimaTransacao!,
    ),
  };
}
