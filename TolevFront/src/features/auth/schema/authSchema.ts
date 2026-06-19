import { z } from "zod";

export const loginFormSchema = z.object({
  email: z
    .string()
    .trim()
    .min(1, "Informe seu email")
    .email("Email inválido"),
  senha: z.string().min(1, "Informe sua senha"),
});

export type LoginFormValues = z.infer<typeof loginFormSchema>;

export const registerFormSchema = z
  .object({
    nome: z.string().trim().max(255, "Nome muito longo").optional(),
    nomeUsuario: z
      .string()
      .trim()
      .min(1, "Escolha um nome de usuário")
      .max(100, "Nome de usuário muito longo"),
    email: z
      .string()
      .trim()
      .min(1, "Informe seu email")
      .email("Email inválido"),
    senha: z.string().min(8, "A senha deve ter no mínimo 8 caracteres"),
    confirmarSenha: z.string().min(1, "Confirme sua senha"),
  })
  .refine((data) => data.senha === data.confirmarSenha, {
    message: "As senhas não coincidem",
    path: ["confirmarSenha"],
  });

export type RegisterFormValues = z.infer<typeof registerFormSchema>;
