import type { HelpContent } from "../../../components";

export type StepCopy = {
  title: string;
  subtitle: string;
  help: HelpContent;
};

/**
 * Copy for each step of "Adicionar transação". Diferente da dívida, aqui nada
 * mora num contrato: a pessoa acabou de gastar e lembra de tudo. Por isso são
 * 4 passos curtos e o texto de ajuda explica *por que* pedimos cada coisa, não
 * onde encontrá-la.
 */
export const STEPS: StepCopy[] = [
  {
    title: "O que aconteceu com seu dinheiro?",
    subtitle:
      "Entrou ou saiu, e quanto. Esses dois são os únicos campos que você precisa saber de cabeça — o resto a gente pergunta depois.",
    help: {
      label: "Por que isso importa?",
      title: "Receita ou despesa",
      body:
        "É a divisão mais básica do seu mês: o que entra sustenta o que sai. Lançar os dois é o que faz a análise saber quanto sobra de verdade.",
      ondeEncontrar: [
        "Receita é dinheiro que chegou até você: salário, freela, venda, presente, reembolso.",
        "Despesa é dinheiro que saiu: compra, conta, assinatura, transporte.",
        "Pagou uma parcela de dívida? Registre em Dívidas, na tela da própria dívida — assim ela abate o saldo devedor.",
      ],
      footer: "Errou o valor? Nenhum lançamento é definitivo: dá pra ajustar depois.",
    },
  },
  {
    title: "Em que isso se encaixa?",
    subtitle:
      "Escolha a categoria. É ela que agrupa seus gastos e revela para onde seu dinheiro está indo de verdade todo mês.",
    help: {
      label: "E se nenhuma servir?",
      title: "Categorias",
      body:
        "As categorias vêm prontas para você não ter que inventar um sistema do zero. Elas são o eixo de toda a análise de consumo.",
      ondeEncontrar: [
        "Escolha pelo motivo do gasto, não pelo lugar: um lanche no posto é Alimentação, não Transporte.",
        "Na dúvida entre duas, escolha a que você usaria de novo numa compra parecida — consistência vale mais que precisão.",
        "Não achou nenhuma que sirva? Use “Outros” agora e crie a sua depois em Finanças › Suas categorias.",
      ],
      footer: "Categorizar bem é o que transforma uma lista de gastos numa análise útil.",
    },
  },
  {
    title: "Onde e quando foi?",
    subtitle:
      "O lugar e a data. Com eles a análise consegue ver seus padrões — onde você mais gasta e em que momentos do mês.",
    help: {
      label: "Preciso preencher tudo?",
      title: "Local, data e forma de pagamento",
      body:
        "Só a data é obrigatória, e ela já vem preenchida com hoje. O resto deixa a análise mais rica, mas não trava seu lançamento.",
      ondeEncontrar: [
        "Estabelecimento: o nome do lugar, como você o chamaria. Escrever igual nas próximas vezes agrupa tudo no mesmo lugar.",
        "Data: o dia em que você gastou, não o dia em que a fatura vence.",
        "Forma de pagamento: no crédito, o dinheiro sai da sua conta bem depois — é isso que a projeção de fluxo precisa saber.",
      ],
      footer: "Deixou o estabelecimento em branco? Sem problema, a transação entra do mesmo jeito.",
    },
  },
  {
    title: "De onde saiu esse dinheiro?",
    subtitle:
      "A conta que bancou o gasto e, se for uma compra parcelada, em quantas vezes ela ficou.",
    help: {
      label: "Não tenho conta cadastrada",
      title: "Conta e parcelamento",
      body:
        "Escolher “Dinheiro / carteira” é uma resposta completa, não um atalho. O app ainda não conecta bancos, então é o caso mais comum.",
      ondeEncontrar: [
        "Ao ligar a transação a uma conta, o saldo dela se move junto — é assim que a tela de saldo continua batendo com a realidade.",
        "Dinheiro / carteira registra o gasto sem mexer em saldo nenhum.",
        "Parcelado: informe o total de vezes e qual parcela é essa. Se você acabou de comprar em 10x, é a parcela 1 de 10.",
      ],
      footer: "Compra parcelada no crédito? Marcar aqui é o que deixa a análise enxergar o compromisso futuro.",
    },
  },
];

export const TOTAL_STEPS = STEPS.length;
