/**
 * Auth domain types, mirroring the backend `users` module:
 *  - RegisterRequest / LoginRequest        (users.application.dto.request)
 *  - AuthResponse / UsuarioResponse        (users.application.dto.response)
 */

/** POST /auth/register. */
export interface RegisterRequest {
  nome?: string | null;
  genero?: string | null;
  dataNascimento?: string | null; // ISO date "yyyy-MM-dd"
  nomeUsuario: string;
  email: string;
  senha: string;
}

/** POST /auth/login. */
export interface LoginRequest {
  email: string;
  senha: string;
}

/** Authenticated user, as returned inside AuthResponse. */
export interface UsuarioResponse {
  id: number;
  nome: string | null;
  genero: string | null;
  dataNascimento: string | null;
  nomeUsuario: string;
  email: string;
}

/** /auth endpoints. */
export interface AuthResponse {
  token: string;
  tipo: string; // "Bearer"
  expiraEmMs: number;
  usuario: UsuarioResponse;
}
