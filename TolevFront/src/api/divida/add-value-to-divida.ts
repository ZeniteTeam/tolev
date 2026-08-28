import api from "../axios";

export interface AddValueToDividaPayload {
  id: number;
  value: number;
}

export async function addValueToDivida(payload: AddValueToDividaPayload): Promise<void> {
  await api.post("/dividas/add", payload);
}
