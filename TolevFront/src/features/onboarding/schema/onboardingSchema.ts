import { z } from "zod";
import {
  OBJETIVO_PRINCIPAL,
  SITUACAO_FINANCEIRA,
  TIPO_EMPREGO,
} from "../../../types/auth";

const OPCAO_OBRIGATORIA = "Escolha uma opção para continuar";

export const onboardingSchema = z
  .object({
    nome: z
      .string()
      .trim()
      .min(1, "Como podemos te chamar?")
      .max(255, "Nome muito longo"),
    objetivoPrincipal: z.enum(OBJETIVO_PRINCIPAL, { message: OPCAO_OBRIGATORIA }),
    situacaoFinanceira: z.enum(SITUACAO_FINANCEIRA, { message: OPCAO_OBRIGATORIA }),
    ocupacao: z.enum(TIPO_EMPREGO, { message: OPCAO_OBRIGATORIA }),
    // Dígitos da renda mensal em reais (ex.: "3500"). "0" é válido (sem renda).
    rendaMensal: z.string().min(1, "Informe sua renda mensal"),
    nomeUsuario: z
      .string()
      .trim()
      .min(1, "Escolha um nome de usuário")
      .max(100, "Nome de usuário muito longo"),
    email: z.string().trim().min(1, "Informe seu email").email("Email inválido"),
    senha: z.string().min(8, "A senha deve ter no mínimo 8 caracteres"),
    confirmarSenha: z.string().min(1, "Confirme sua senha"),
  })
  .refine((data) => data.senha === data.confirmarSenha, {
    message: "As senhas não coincidem",
    path: ["confirmarSenha"],
  });

export type OnboardingValues = z.infer<typeof onboardingSchema>;
