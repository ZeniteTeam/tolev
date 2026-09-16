import type { BancoUsuarioResponse } from "../../types/extrato";
import api from "../axios";

/** GET /transactions/extrato/bancos — os bancos que já trouxeram transação. */
export async function getBancosUsuario(
  idUsuario: number,
): Promise<BancoUsuarioResponse[]> {
  const response = await api.get<BancoUsuarioResponse[]>(
    "/transactions/extrato/bancos",
    { params: { idUsuario } },
  );
  return response.data;
}
