import api from "../axios";

export interface AddValueToDividaPayload {
  id: number;
  value: number;
}

/** Registers a payment against a debt. POST /dividas/add */
export async function addValueToDivida(payload: AddValueToDividaPayload): Promise<void> {
  await api.post("/dividas/add", payload);
}
