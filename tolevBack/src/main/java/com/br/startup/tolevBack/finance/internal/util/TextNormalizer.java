package com.br.startup.tolevBack.finance.internal.util;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Normalização de texto livre digitado pelo usuário. Serve pra duas coisas que
 * dependem de comparar strings: não duplicar o mesmo estabelecimento escrito de
 * jeitos diferentes ("Padaria do João" / "padaria do joao") e agrupar
 * transações por descrição na análise de consumo.
 */
public final class TextNormalizer {

    private static final java.util.regex.Pattern ACENTOS =
            java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final java.util.regex.Pattern ESPACOS =
            java.util.regex.Pattern.compile("\\s+");

    private TextNormalizer() {
    }

    /**
     * Minúsculas, sem acento e sem espaço repetido. Devolve {@code null} para
     * entrada nula ou em branco, pra não gravar string vazia no banco.
     */
    public static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String semAcento = ACENTOS.matcher(Normalizer.normalize(value, Normalizer.Form.NFD)).replaceAll("");
        String limpo = ESPACOS.matcher(semAcento).replaceAll(" ").trim().toLowerCase(Locale.ROOT);
        return limpo.isEmpty() ? null : limpo;
    }
}
