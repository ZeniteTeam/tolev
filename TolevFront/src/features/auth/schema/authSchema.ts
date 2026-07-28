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
