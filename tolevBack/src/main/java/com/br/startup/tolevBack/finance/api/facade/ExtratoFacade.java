package com.br.startup.tolevBack.finance.api.facade;

import com.br.startup.tolevBack.finance.application.dto.response.BancoUsuarioResponse;
import com.br.startup.tolevBack.finance.application.dto.response.ExtratoImportacaoResponse;
import com.br.startup.tolevBack.finance.application.usecase.commands.ConfirmExtratoImportService;
import com.br.startup.tolevBack.finance.application.usecase.commands.ImportExtratoService;
import com.br.startup.tolevBack.finance.application.usecase.commands.UndoExtratoImportService;
import com.br.startup.tolevBack.finance.application.usecase.queries.GetExtratoImportsService;
import com.br.startup.tolevBack.finance.application.usecase.queries.GetUserBanksService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExtratoFacade {

    private final ImportExtratoService importExtrato;
    private final UndoExtratoImportService undoExtratoImport;
    private final ConfirmExtratoImportService confirmExtratoImport;
    private final GetExtratoImportsService getExtratoImports;
    private final GetUserBanksService getUserBanks;

    public ExtratoImportacaoResponse importar(Long idUsuario, Long idBanco, String nomeArquivo, byte[] pdf) {
        return importExtrato.execute(idUsuario, idBanco, nomeArquivo, pdf);
    }

    public List<ExtratoImportacaoResponse> listar(Long idUsuario) {
        return getExtratoImports.execute(idUsuario);
    }

    public ExtratoImportacaoResponse porId(Long idUsuario, Long idImportacao) {
        return getExtratoImports.byId(idUsuario, idImportacao);
    }

    public ExtratoImportacaoResponse confirmar(Long idUsuario, Long idImportacao) {
        return confirmExtratoImport.execute(idUsuario, idImportacao);
    }

    public void desfazer(Long idUsuario, Long idImportacao) {
        undoExtratoImport.execute(idUsuario, idImportacao);
    }

    public List<BancoUsuarioResponse> bancosDoUsuario(Long idUsuario) {
        return getUserBanks.execute(idUsuario);
    }
}
