package com.br.startup.tolevBack.finance.api.controller;

import com.br.startup.tolevBack.finance.api.facade.ExtratoFacade;
import com.br.startup.tolevBack.finance.application.dto.response.BancoUsuarioResponse;
import com.br.startup.tolevBack.finance.application.dto.response.ExtratoImportacaoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

/**
 * Importação de extrato bancário em PDF.
 *
 * <p>Fica sob {@code /transactions} porque o resultado do upload são transações
 * do usuário — a leitura pelo Gemini é meio, não fim.
 *
 * <p>O upload é aceito, não executado: o POST responde {@code 202} assim que
 * registra o pedido, e o resultado aparece nos GETs quando a leitura terminar.
 * Ler um extrato mensal leva dezenas de segundos, e prender a requisição por
 * todo esse tempo deixaria o app parado numa tela de espera que ele não pode
 * abandonar.
 */
@RestController
@RequestMapping("/transactions/extrato")
@RequiredArgsConstructor
public class ExtratoController {

    private final ExtratoFacade extratoFacade;

    /**
     * Registra o upload e devolve na hora, com a importação em
     * {@code PROCESSANDO}.
     *
     * <p>Multipart, não JSON com base64: o PDF vai como binário puro, sem os 33%
     * de inchaço do base64 nem o custo de decodificar antes de saber se o
     * arquivo presta.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExtratoImportacaoResponse> importar(
            @RequestParam Long idUsuario,
            @RequestParam Long idBanco,
            @RequestPart("arquivo") MultipartFile arquivo) {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Envie o PDF do extrato.");
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(extratoFacade.importar(
                        idUsuario, idBanco, arquivo.getOriginalFilename(), bytes(arquivo)));
    }

    /** Histórico de importações do usuário, mais recentes primeiro. */
    @GetMapping
    public ResponseEntity<List<ExtratoImportacaoResponse>> listar(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(extratoFacade.listar(idUsuario));
    }

    /** O estado de uma importação — é isto que a tela de espera fica relendo. */
    @GetMapping("/{idImportacao}")
    public ResponseEntity<ExtratoImportacaoResponse> porId(
            @PathVariable Long idImportacao, @RequestParam Long idUsuario) {
        return ResponseEntity.ok(extratoFacade.porId(idUsuario, idImportacao));
    }

    /**
     * O "ok, atualizar" do usuário. Some com o aviso de extrato pronto sem
     * depender de o app ter ficado aberto durante o processamento.
     */
    @PostMapping("/{idImportacao}/confirmacao")
    public ResponseEntity<ExtratoImportacaoResponse> confirmar(
            @PathVariable Long idImportacao, @RequestParam Long idUsuario) {
        return ResponseEntity.ok(extratoFacade.confirmar(idUsuario, idImportacao));
    }

    /** Apaga as transações de uma importação e libera o período para novo upload. */
    @DeleteMapping("/{idImportacao}")
    public ResponseEntity<Void> desfazer(
            @PathVariable Long idImportacao, @RequestParam Long idUsuario) {
        extratoFacade.desfazer(idUsuario, idImportacao);
        return ResponseEntity.noContent().build();
    }

    /**
     * Os bancos deste usuário — os que já trouxeram transação, com período
     * coberto e totais. Diferente de {@code GET /banks}, que é o catálogo de
     * onde escolher na hora do upload.
     */
    @GetMapping("/bancos")
    public ResponseEntity<List<BancoUsuarioResponse>> bancos(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(extratoFacade.bancosDoUsuario(idUsuario));
    }

    /**
     * Falha de leitura do upload não é erro do usuário nem regra de negócio — a
     * conexão caiu no meio. Vira 500 pelo handler global, que é o certo aqui.
     */
    private byte[] bytes(MultipartFile arquivo) {
        try {
            return arquivo.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível ler o arquivo enviado.", e);
        }
    }
}
