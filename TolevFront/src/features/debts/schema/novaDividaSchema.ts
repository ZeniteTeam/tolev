import { z } from "zod";
import { REGIME_JUROS, SISTEMA_AMORTIZACAO, TIPO_DIVIDA } from "../../../types/divida";
import { isValidBrDate } from "../../../util/date";
import { digitsToDecimal } from "../../../util/masks";

/**
 * Money and percentage fields hold the digits the user typed (see
 * `util/masks`), so "12050" is R$ 120,50 / 120,50%. Dates hold the masked
 * DD/MM/AAAA text. Everything is converted to numbers/ISO only at submit.
 */
const dataOpcional = z
  .string()
  .refine((v) => v === "" || isValidBrDate(v), "Data inválida — use DD/MM/AAAA");

export const novaDividaSchema = z.object({
  nome: z.string().trim().min(1, "Dê um nome para essa dívida").max(255, "Nome muito longo"),
  tipo: z.enum(TIPO_DIVIDA, { message: "Escolha o tipo da dívida" }),
  banco: z
    .string()
    .min(1, "Escolha o banco ou a loja que cobra essa dívida")
    .refine((v) => v !== "all", "Escolha o banco ou a loja que cobra essa dívida"),
  pesoEmocional: z.number().min(1).max(5),

  valor: z.string().refine((v) => digitsToDecimal(v) > 0, "Informe o valor total da dívida"),
  parcelas: z
    .string()
    .refine((v) => Number(v) >= 1, "Informe em quantas parcelas ela será paga")
    .refine((v) => Number(v) <= 480, "No máximo 480 parcelas"),
  dataLiberacao: dataOpcional,
  dataPrimeiroVencimento: z
    .string()
    .min(1, "Informe o vencimento da primeira parcela")
    .refine(isValidBrDate, "Data inválida — use DD/MM/AAAA"),

  // Encargos são opcionais: quem não achar no contrato segue sem travar.
  multaAtraso: z.string(),
  jurosMensal: z.string(),
  jurosMora: z.string(),

  sistemaAmortizacao: z.enum(SISTEMA_AMORTIZACAO, { message: "Escolha um dos dois" }),
  regimeJuros: z.enum(REGIME_JUROS, { message: "Escolha um dos dois" }),
});

export type NovaDividaValues = z.infer<typeof novaDividaSchema>;

/** Fields validated when leaving each step (index = step - 1). */
export const STEP_FIELDS: (keyof NovaDividaValues)[][] = [
  ["nome", "tipo", "banco", "pesoEmocional"],
  ["valor", "parcelas", "dataLiberacao", "dataPrimeiroVencimento"],
  ["multaAtraso", "jurosMensal", "jurosMora"],
  ["sistemaAmortizacao"],
  ["regimeJuros"],
];
