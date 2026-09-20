package com.br.startup.tolevBack.finance.api.facade;

import com.br.startup.tolevBack.finance.application.dto.request.GastoFixoRequest;
import com.br.startup.tolevBack.finance.application.dto.response.GastoFixoResponse;
import com.br.startup.tolevBack.finance.application.usecase.commands.ConfirmarGastosFixosService;
import com.br.startup.tolevBack.finance.application.usecase.commands.CreateGastoFixoService;
import com.br.startup.tolevBack.finance.application.usecase.commands.DeleteGastoFixoService;
import com.br.startup.tolevBack.finance.application.usecase.commands.UpdateGastoFixoService;
import com.br.startup.tolevBack.finance.application.usecase.queries.GetGastosFixosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GastoFixoFacade {

    private final GetGastosFixosService getGastosFixos;
    private final CreateGastoFixoService createGastoFixo;
    private final UpdateGastoFixoService updateGastoFixo;
    private final DeleteGastoFixoService deleteGastoFixo;
    private final ConfirmarGastosFixosService confirmarGastosFixos;

    public List<GastoFixoResponse> getAll(Long idUsuario) {
        return getGastosFixos.execute(idUsuario);
    }

    public GastoFixoResponse create(GastoFixoRequest request) {
        return createGastoFixo.execute(request);
    }

    public GastoFixoResponse update(Long id, Long idUsuario, GastoFixoRequest request) {
        return updateGastoFixo.execute(id, idUsuario, request);
    }

    public void delete(Long id, Long idUsuario) {
        deleteGastoFixo.execute(id, idUsuario);
    }

    public int confirmar(Long idUsuario) {
        return confirmarGastosFixos.execute(idUsuario);
    }
}
