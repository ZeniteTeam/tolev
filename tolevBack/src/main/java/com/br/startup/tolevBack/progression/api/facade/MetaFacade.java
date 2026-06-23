package com.br.startup.tolevBack.progression.api.facade;

import com.br.startup.tolevBack.progression.application.dto.request.AddValueToMetaRequest;
import com.br.startup.tolevBack.progression.application.dto.request.MetaRequest;
import com.br.startup.tolevBack.progression.application.dto.response.MetaResponse;
import com.br.startup.tolevBack.progression.application.usecase.commands.Goals.AddNewValueToMetaService;
import com.br.startup.tolevBack.progression.application.usecase.commands.Goals.CreateMetaService;
import com.br.startup.tolevBack.progression.application.usecase.commands.Goals.DeleteMetaService;
import com.br.startup.tolevBack.progression.application.usecase.commands.Goals.UpdateMetaService;
import com.br.startup.tolevBack.progression.application.usecase.queries.Goals.GetMetaByIdService;
import com.br.startup.tolevBack.progression.application.usecase.queries.Goals.GetMetasByUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MetaFacade {

    private final GetMetasByUserService getMetasByUser;
    private final GetMetaByIdService getMetaById;
    private final CreateMetaService createMeta;
    private final UpdateMetaService updateMeta;
    private final DeleteMetaService deleteMeta;
    private final AddNewValueToMetaService addNewValueToMeta;

    public List<MetaResponse> getAll(Long idUsuario) {
        return getMetasByUser.execute(idUsuario);
    }

    public MetaResponse getById(Long id) {
        return getMetaById.execute(id);
    }

    public MetaResponse create(MetaRequest request) {
        return createMeta.execute(request);
    }

    public MetaResponse update(Long id, MetaRequest request) {
        return updateMeta.execute(id, request);
    }

    public void delete(Long id) {
        deleteMeta.execute(id);
    }

    public void addNewValue(AddValueToMetaRequest id) {
        addNewValueToMeta.execute(id);
    }
}
