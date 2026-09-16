package com.br.startup.tolevBack.finance.api.facade;

import com.br.startup.tolevBack.finance.application.dto.request.CategoryRequest;
import com.br.startup.tolevBack.finance.application.dto.response.CategoryResponse;
import com.br.startup.tolevBack.finance.application.usecase.commands.CreateUserCategoryService;
import com.br.startup.tolevBack.finance.application.usecase.commands.DeleteUserCategoryService;
import com.br.startup.tolevBack.finance.application.usecase.commands.UpdateUserCategoryService;
import com.br.startup.tolevBack.finance.application.usecase.queries.GetCategoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryFacade {

    private final GetCategoriesService getCategories;
    private final CreateUserCategoryService createUserCategory;
    private final UpdateUserCategoryService updateUserCategory;
    private final DeleteUserCategoryService deleteUserCategory;

    public List<CategoryResponse> getAll(Long idUsuario) {
        return getCategories.execute(idUsuario);
    }

    public CategoryResponse create(CategoryRequest request) {
        return createUserCategory.execute(request);
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        return updateUserCategory.execute(id, request);
    }

    public void delete(Long id, Long idUsuario) {
        deleteUserCategory.execute(id, idUsuario);
    }
}
