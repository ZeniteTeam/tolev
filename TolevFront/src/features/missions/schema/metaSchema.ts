import { z } from "zod";
import { CATEGORIA_META } from "../../../types/meta";
import { parseCurrencyToNumber } from "../../../util/currency";
import { isValidBrDate } from "../../../util/date";

export const metaFormSchema = z.object({
  nomeMeta: z
    .string()
    .trim()
    .min(1, "Dê um nome para sua meta")
    .max(255, "Nome muito longo"),
  categoria: z.enum(CATEGORIA_META),
  motivacaoMeta: z
    .string()
    .trim()
    .max(500, "Máximo de 500 caracteres")
    .optional(),
  valorMeta: z
    .string()
    .min(1, "Informe o valor total")
    .refine((v) => parseCurrencyToNumber(v) > 0, "Valor inválido"),
  dataLimite: z
    .date({error: "Insira uma data valida"})
    .refine(
      (v) => isValidBrDate(v.toLocaleDateString("pt-BR")), 
      "Use o formato DD/MM/AAAA"
    )
    .min(new Date(), "A data deve ser futura"),
  valorDedicado: z
    .string()
    .min(1, "Informe o valor total")
    .refine((v) => parseCurrencyToNumber(v) > 0, "Valor inválido"),
  recompensa: z.string().trim().max(255, "Texto muito longo").optional(),
  commitment: z.number().int().min(1).max(5),
});

export type MetaFormValues = z.infer<typeof metaFormSchema>;
