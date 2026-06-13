package com.br.startup.tolevBack.progression.api.facade;

import com.br.startup.tolevBack.progression.application.dto.request.MetaRequest;
import com.br.startup.tolevBack.progression.application.dto.response.MetaResponse;
import com.br.startup.tolevBack.progression.application.usecase.commands.CreateMetaService;
import com.br.startup.tolevBack.progression.application.usecase.commands.DeleteMetaService;
import com.br.startup.tolevBack.progression.application.usecase.commands.UpdateMetaService;
import com.br.startup.tolevBack.progression.application.usecase.queries.GetMetaByIdService;
import com.br.startup.tolevBack.progression.application.usecase.queries.GetMetasByUserService;
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
}
