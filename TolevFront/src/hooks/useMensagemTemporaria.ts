import { useEffect, useState } from "react";

/** Tempo que um aviso de erro fica na tela antes de sair sozinho. */
export const DURACAO_MENSAGEM_MS = 3000;

/**
 * Deixa uma mensagem visível por alguns segundos e depois a apaga sozinha.
 *
 * Erro de envio não é estado permanente: depois de lido, o texto vermelho só
 * ocupa a tela e faz o próximo toque parecer que falhou de novo. Some sozinho —
 * quem age em cima do erro usa os botões, que continuam onde estavam.
 *
 * @param mensagem o texto a mostrar; `null`/`undefined` esconde na hora
 * @param chave    identifica a origem da mensagem. Serve para reiniciar a
 *                 contagem quando o texto se repete mas o evento é outro (duas
 *                 falhas seguidas com a mesma frase), caso em que comparar só a
 *                 string deixaria o aviso escondido.
 */
export function useMensagemTemporaria(
  mensagem: string | null | undefined,
  chave?: string | number,
  duracaoMs: number = DURACAO_MENSAGEM_MS,
): string | null {
  const [visivel, setVisivel] = useState(true);

  useEffect(() => {
    if (!mensagem) {
      return;
    }
    setVisivel(true);
    const timer = setTimeout(() => setVisivel(false), duracaoMs);
    return () => clearTimeout(timer);
  }, [mensagem, chave, duracaoMs]);

  return mensagem && visivel ? mensagem : null;
}
