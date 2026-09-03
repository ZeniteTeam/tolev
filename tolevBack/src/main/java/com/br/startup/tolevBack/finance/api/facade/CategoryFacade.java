package com.br.startup.tolevBack.finance.api.facade;

import com.br.startup.tolevBack.finance.application.dto.response.CategoryResponse;
import com.br.startup.tolevBack.finance.application.usecase.queries.GetCategoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryFacade {

    private final GetCategoriesService getCategories;

    public List<CategoryResponse> getAll(Long idUsuario) {
        return getCategories.execute(idUsuario);
    }
}
