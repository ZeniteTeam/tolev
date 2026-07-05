import { create } from "zustand";
import type { MetodoId } from "../constants/metodos";

interface PlanState {
  /** Currently applied debt-repayment method. */
  metodoAplicado: MetodoId;
  setMetodo: (id: MetodoId) => void;
}

/** Client-side state for the active planning method (backend sync later). */
export const usePlanStore = create<PlanState>((set) => ({
  metodoAplicado: "avalanche",
  setMetodo: (id) => set({ metodoAplicado: id }),
}));
