package com.br.startup.tolevBack.progression.api.facade;

import com.br.startup.tolevBack.progression.application.dto.request.AddValueToDividaRequest;
import com.br.startup.tolevBack.progression.application.dto.request.DividaRequest;
import com.br.startup.tolevBack.progression.application.dto.request.RegisterPaymentRequest;
import com.br.startup.tolevBack.progression.application.dto.response.DividaResponse;
import com.br.startup.tolevBack.progression.application.usecase.commands.Debts.AddNewValueToDividaService;
import com.br.startup.tolevBack.progression.application.usecase.commands.Debts.CreateDividaService;
import com.br.startup.tolevBack.progression.application.usecase.commands.Debts.DeleteDividaService;
import com.br.startup.tolevBack.progression.application.usecase.commands.Debts.RegisterDividaPaymentService;
import com.br.startup.tolevBack.progression.application.usecase.commands.Debts.UpdateDividaService;
import com.br.startup.tolevBack.progression.application.usecase.queries.Debts.GetDividaResponseByIdService;
import com.br.startup.tolevBack.progression.application.usecase.queries.Debts.GetDividasByUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DividaFacade {

    private final GetDividasByUserService getDividasByUser;
    private final GetDividaResponseByIdService getDividaById;
    private final CreateDividaService createDivida;
    private final UpdateDividaService updateDivida;
    private final DeleteDividaService deleteDivida;
    private final AddNewValueToDividaService addNewValueToDivida;
    private final RegisterDividaPaymentService registerDividaPayment;

    public List<DividaResponse> getAll(Long idUsuario) {
        return getDividasByUser.execute(idUsuario);
    }

    public DividaResponse getById(Long id) {
        return getDividaById.execute(id);
    }

    public DividaResponse create(DividaRequest request) {
        return createDivida.execute(request);
    }

    public DividaResponse update(Long id, DividaRequest request) {
        return updateDivida.execute(id, request);
    }

    public void delete(Long id) {
        deleteDivida.execute(id);
    }

    public void addNewValue(AddValueToDividaRequest request) {
        addNewValueToDivida.execute(request);
    }

    public DividaResponse registerPayment(RegisterPaymentRequest request) {
        return registerDividaPayment.execute(request);
    }
}
