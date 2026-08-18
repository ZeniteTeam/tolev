import type { HelpContent } from "../../../components";

export type StepCopy = {
  title: string;
  subtitle: string;
  help: HelpContent;
};

/**
 * Copy for each step of "Adicionar dívida". The title speaks to how the person
 * is probably feeling, the subtitle says what we're going to do with the
 * number, and the help sheet says exactly where to find it — no one decorates
 * a debt form from memory.
 */
export const STEPS: StepCopy[] = [
  {
    title: "Vamos dar nome a essa dívida",
    subtitle:
      "Colocar no papel é o passo mais difícil — e você já está aqui. Comece pelo básico: como você chama essa dívida e de onde ela veio.",
    help: {
      label: "Onde encontro isso?",
      title: "Nome, tipo e banco",
      body:
        "Aqui não tem certo nem errado: o nome é só pra você reconhecer a dívida na lista. O tipo e o banco são o que nos deixam calcular e comparar direito.",
      ondeEncontrar: [
        "O tipo aparece no topo do contrato ou da fatura: cartão de crédito, empréstimo pessoal, financiamento, cheque especial ou carnê.",
        "O banco é quem cobra de você — o nome que aparece no boleto, no app ou no débito da conta.",
        "O peso emocional é seu: 5 chamas é aquela dívida que tira seu sono, 1 é a que quase não incomoda.",
      ],
      footer: "O peso emocional é o que faz o método Tsunami atacar primeiro o que mais pesa em você.",
    },
  },
  {
    title: "Quanto e em quantas vezes?",
    subtitle:
      "Agora os números do contrato. É com eles que montamos a sua tabela de parcelas e a data em que essa dívida acaba.",
    help: {
      label: "Onde encontro isso?",
      title: "Valor, parcelas e datas",
      body:
        "Todos esses dados estão no contrato ou no carnê que você recebeu quando fechou a dívida. No app do banco, procure por “Detalhes do contrato” ou “Meus empréstimos”.",
      ondeEncontrar: [
        "Valor: procure por “valor total financiado”, “valor da operação” ou “valor liberado”. É o valor da dívida, não o da parcela.",
        "Nº de parcelas: aparece como “prazo” ou “nº de prestações” — o total, incluindo as que você já pagou.",
        "Data de liberação: o dia em que o dinheiro caiu na conta ou em que a compra foi feita.",
        "1º vencimento: a data da primeira parcela. No carnê, é o primeiro boleto da sequência.",
      ],
      footer: "Não achou uma das datas? Coloque a mais provável — dá pra corrigir depois sem perder nada.",
    },
  },
  {
    title: "E se atrasar, quanto custa?",
    subtitle:
      "Ninguém gosta de olhar pra essa parte, mas é ela que revela o tamanho real da dívida. Saber os encargos é o que te dá vantagem.",
    help: {
      label: "Onde encontro isso?",
      title: "Multa, juros e mora",
      body:
        "Esses três números ficam na cláusula de encargos do contrato, normalmente perto do fim, e também no rodapé do boleto — em letra pequena, mas estão lá.",
      ondeEncontrar: [
        "Multa por atraso: cobrada uma única vez sobre a parcela atrasada. No crédito ao consumidor, a lei limita a 2%.",
        "Juros mensal: a taxa do contrato, escrita como “% a.m.” (ao mês). Se o seu contrato só mostra “% a.a.”, divida por 12 para uma aproximação.",
        "Juros de mora: a taxa que corre por dia de atraso, quase sempre 1% ao mês.",
        "No app do banco, procure por “Custo Efetivo Total (CET)” — a tela de encargos costuma ficar ao lado.",
      ],
      footer: "Não achou a multa ou a mora? Deixe em branco: usamos apenas o juros mensal no cálculo das parcelas.",
    },
  },
  {
    title: "Suas parcelas são iguais ou vão diminuindo?",
    subtitle:
      "Essa é a diferença entre os dois sistemas de amortização mais usados no Brasil. Olhe seu carnê: a resposta está no valor das próximas parcelas.",
    help: {
      label: "Como descubro isso?",
      title: "PRICE ou SAC?",
      body:
        "Os dois pagam a mesma dívida, mas distribuem os juros de formas diferentes. O jeito mais rápido de saber é comparar a primeira parcela com a última.",
      ondeEncontrar: [
        "Se todas as parcelas têm o mesmo valor do começo ao fim, é PRICE. É o padrão de cartão, crediário, carnê e empréstimo pessoal.",
        "Se a parcela começa mais alta e vai caindo todo mês, é SAC. Aparece bastante em financiamento imobiliário e de veículos.",
        "No contrato, procure por “sistema de amortização” — costuma vir escrito com todas as letras.",
        "Na dúvida, escolha PRICE: é o mais comum em dívidas do dia a dia.",
      ],
      footer: "Escolheu errado? É só editar a dívida depois que a tabela é recalculada.",
    },
  },
  {
    title: "Por último: como os juros crescem",
    subtitle:
      "É o que separa uma dívida que incomoda de uma que vira bola de neve. Escolha e a gente monta a projeção real.",
    help: {
      label: "Como descubro isso?",
      title: "Juros simples ou compostos?",
      body:
        "A diferença é sobre o que a taxa incide: sobre o valor que você pegou emprestado, ou sobre o saldo que ainda deve — juros rendendo em cima de juros.",
      ondeEncontrar: [
        "Simples: a taxa incide sempre sobre o valor original. Comum em carnês de loja e acordos informais, onde os juros já vêm somados no total.",
        "Composto: a taxa incide sobre o saldo devedor todo mês. É o padrão de bancos e financeiras — cartão, cheque especial, empréstimo e financiamento.",
        "No contrato, “capitalização mensal” ou “juros capitalizados” significa composto.",
        "Na dúvida, escolha composto: é o que quase todo crédito bancário usa.",
      ],
      footer: "Escolher composto por engano só deixa a projeção mais conservadora — nunca otimista demais.",
    },
  },
];

export const TOTAL_STEPS = STEPS.length;
