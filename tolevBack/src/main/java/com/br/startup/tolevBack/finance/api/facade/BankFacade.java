package com.br.startup.tolevBack.finance.api.facade;

import com.br.startup.tolevBack.finance.application.dto.response.BankResponse;
import com.br.startup.tolevBack.finance.application.usecase.queries.GetBanksService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankFacade {

    private final GetBanksService getBanks;

    public List<BankResponse> getAll() {
        return getBanks.execute();
    }
}
