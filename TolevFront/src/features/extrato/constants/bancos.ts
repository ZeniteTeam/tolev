/**
 * Identidade visual e instruções de cada banco.
 *
 * O catálogo em si vem do backend (`GET /banks`) — nome e id são de lá. Aqui
 * mora só o que é do app: a cor, a sigla e o passo a passo de onde baixar o
 * extrato. Casamos os dois pelo `codigoBanco` (o código COMPE), e não pelo nome:
 * casar por nome quebraria na primeira vez que "Itaú Unibanco" virasse "Itaú".
 *
 * Banco sem entrada aqui não é erro — aparece com o visual neutro e as
 * instruções genéricas, que valem para qualquer app de banco.
 */

export type BancoVisual = {
  /** Sigla curta para o círculo colorido. Uma ou duas letras. */
  sigla: string;
  cor: string;
  corTexto: string;
};

export type ComoEncontrar = {
  passos: string[];
  /** Fecho tranquilizador, quando o banco tem alguma pegadinha conhecida. */
  observacao?: string;
};

const VISUAL_NEUTRO: BancoVisual = {
  sigla: "$",
  cor: "#6B7D75",
  corTexto: "#FFFFFF",
};

const VISUAL: Record<string, BancoVisual> = {
  "001": { sigla: "BB", cor: "#FFEF38", corTexto: "#003876" },
  "104": { sigla: "C", cor: "#0070AF", corTexto: "#FF9E1B" },
  "237": { sigla: "B", cor: "#CC092F", corTexto: "#FFFFFF" },
  "341": { sigla: "i", cor: "#EC7000", corTexto: "#FFFFFF" },
  "033": { sigla: "S", cor: "#EC0000", corTexto: "#FFFFFF" },
  "260": { sigla: "N", cor: "#820AD1", corTexto: "#FFFFFF" },
  "077": { sigla: "I", cor: "#FF7A00", corTexto: "#FFFFFF" },
  "336": { sigla: "C6", cor: "#111111", corTexto: "#FFFFFF" },
  "380": { sigla: "P", cor: "#11C76F", corTexto: "#FFFFFF" },
};

/**
 * Serve para quando o banco não está mapeado e para o caso, nada raro, de o app
 * do banco ter mudado de lugar os menus desde a última vez que olhamos isto.
 */
const PASSOS_GENERICOS: ComoEncontrar = {
  passos: [
    "Abra o app do seu banco e procure por Extrato, Movimentações ou Lançamentos.",
    "Escolha o período que quer importar — o mês inteiro costuma ser o mais útil.",
    "Toque em Exportar, Compartilhar ou no ícone de download e escolha PDF.",
    "Salve o arquivo no celular e volte aqui para selecioná-lo.",
  ],
  observacao:
    "Não achou? Procure por “extrato em PDF” na busca do app do banco — quase todos têm.",
};

const COMO_ENCONTRAR: Record<string, ComoEncontrar> = {
  "260": {
    passos: [
      "Abra o app do Nubank e toque em “Ver extrato”, na tela inicial.",
      "Toque no ícone de compartilhar, no canto superior direito.",
      "Escolha o período e depois “Extrato em PDF”.",
      "Salve o arquivo no celular e volte aqui para selecioná-lo.",
    ],
    observacao:
      "O extrato da conta e a fatura do cartão são arquivos diferentes. Para gastos do dia a dia, use o extrato da conta.",
  },
  "341": {
    passos: [
      "Abra o app do Itaú e entre na sua conta corrente.",
      "Toque em “Extrato” e escolha o período desejado.",
      "Toque em “Compartilhar” ou no ícone de download e escolha PDF.",
      "Salve o arquivo no celular e volte aqui para selecioná-lo.",
    ],
  },
  "237": {
    passos: [
      "Abra o app do Bradesco e toque em “Saldo e extrato”.",
      "Selecione o período que quer importar.",
      "Toque em “Compartilhar extrato” e escolha PDF.",
      "Salve o arquivo no celular e volte aqui para selecioná-lo.",
    ],
  },
  "033": {
    passos: [
      "Abra o app do Santander e vá em “Conta corrente” → “Extrato”.",
      "Escolha o período desejado.",
      "Toque no ícone de compartilhar e escolha PDF.",
      "Salve o arquivo no celular e volte aqui para selecioná-lo.",
    ],
  },
  "001": {
    passos: [
      "Abra o app do Banco do Brasil e toque em “Extrato”.",
      "Escolha o período que quer importar.",
      "Toque em “Salvar/Compartilhar” e escolha PDF.",
      "Salve o arquivo no celular e volte aqui para selecioná-lo.",
    ],
  },
  "104": {
    passos: [
      "Abra o app Caixa e entre em “Saldo e extrato”.",
      "Escolha a conta e o período.",
      "Toque em “Compartilhar” e escolha PDF.",
      "Salve o arquivo no celular e volte aqui para selecioná-lo.",
    ],
  },
  "077": {
    passos: [
      "Abra o app do Inter e toque em “Extrato”, na tela inicial.",
      "Selecione o período desejado.",
      "Toque no ícone de compartilhar e escolha “Extrato em PDF”.",
      "Salve o arquivo no celular e volte aqui para selecioná-lo.",
    ],
  },
  "336": {
    passos: [
      "Abra o app do C6 Bank e vá em “Conta” → “Extrato”.",
      "Escolha o período que quer importar.",
      "Toque em “Exportar” e escolha PDF.",
      "Salve o arquivo no celular e volte aqui para selecioná-lo.",
    ],
  },
  "380": {
    passos: [
      "Abra o app do PicPay e toque em “Extrato”, na tela inicial.",
      "Escolha o período desejado.",
      "Toque em “Exportar extrato” e escolha PDF.",
      "Salve o arquivo no celular e volte aqui para selecioná-lo.",
    ],
  },
};

export function bancoVisual(codigoBanco: string | null | undefined): BancoVisual {
  return (codigoBanco && VISUAL[codigoBanco]) || VISUAL_NEUTRO;
}

export function comoEncontrarExtrato(
  codigoBanco: string | null | undefined,
): ComoEncontrar {
  return (codigoBanco && COMO_ENCONTRAR[codigoBanco]) || PASSOS_GENERICOS;
}
