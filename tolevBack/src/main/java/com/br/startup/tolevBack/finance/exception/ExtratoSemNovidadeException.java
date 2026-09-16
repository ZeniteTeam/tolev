package com.br.startup.tolevBack.finance.exception;

/**
 * O extrato foi lido, mas não sobrou nada para gravar.
 *
 * <p>Separada das falhas técnicas porque o desfecho para o usuário é outro:
 * reenviar o mesmo PDF não vai mudar o resultado. Ou o arquivo não era um
 * extrato, ou aquele período já tinha entrado — e a mensagem já vem escrita para
 * a tela dizer qual dos dois foi.
 */
public class ExtratoSemNovidadeException extends RuntimeException {

    public ExtratoSemNovidadeException(String message) {
        super(message);
    }
}
