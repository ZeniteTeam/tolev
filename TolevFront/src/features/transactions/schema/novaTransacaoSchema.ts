import { z } from "zod";
import { METODO_PAGAMENTO, ORIGEM_CATEGORIA, TIPO_TRANSACAO } from "../../../types/transacao";
import { isValidBrDate } from "../../../util/date";
import { digitsToDecimal } from "../../../util/masks";

/**
 * Como em "nova dívida", os campos de dinheiro guardam só os dígitos digitados
 * (ver `util/masks`) e a data guarda o texto mascarado DD/MM/AAAA. A conversão
 * para número/ISO acontece só no submit.
 */
export const novaTransacaoSchema = z
  .object({
    tipo: z.enum(TIPO_TRANSACAO, { message: "Escolha se entrou ou saiu dinheiro" }),
    valor: z.string().refine((v) => digitsToDecimal(v) > 0, "Informe o valor da transação"),

    categoriaId: z.number().int().positive("Escolha uma categoria"),
    categoriaOrigem: z.enum(ORIGEM_CATEGORIA),

    estabelecimento: z.string().max(255, "Nome muito longo"),
    descricao: z.string().max(500, "Descrição muito longa"),
    data: z
      .string()
      .min(1, "Informe a data da transação")
      .refine(isValidBrDate, "Data inválida — use DD/MM/AAAA"),
    metodoPagamento: z.enum(METODO_PAGAMENTO, { message: "Escolha a forma de pagamento" }),

    /** null = dinheiro / carteira, sem conta conectada. */
    contaId: z.number().int().positive().nullable(),
    parcelado: z.boolean(),
    totalParcelas: z.string(),
    numeroParcela: z.string(),
  })
  .superRefine((v, ctx) => {
    if (!v.parcelado) return;

    const total = Number(v.totalParcelas);
    const numero = Number(v.numeroParcela);

    if (!(total >= 2)) {
      ctx.addIssue({
        code: "custom",
        path: ["totalParcelas"],
        message: "Uma compra parcelada tem pelo menos 2 parcelas",
      });
    }
    if (total > 480) {
      ctx.addIssue({
        code: "custom",
        path: ["totalParcelas"],
        message: "No máximo 480 parcelas",
      });
    }
    if (!(numero >= 1) || (total >= 2 && numero > total)) {
      ctx.addIssue({
        code: "custom",
        path: ["numeroParcela"],
        message: "Informe qual das parcelas é essa",
      });
    }
  });

export type NovaTransacaoValues = z.infer<typeof novaTransacaoSchema>;

/** Fields validated when leaving each step (index = step - 1). */
export const STEP_FIELDS: (keyof NovaTransacaoValues)[][] = [
  ["tipo", "valor"],
  ["categoriaId", "categoriaOrigem"],
  ["estabelecimento", "descricao", "data", "metodoPagamento"],
  ["contaId", "parcelado", "totalParcelas", "numeroParcela"],
];
