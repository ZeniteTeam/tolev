package com.br.startup.tolevBack.finance.api.controller;

import com.br.startup.tolevBack.finance.api.facade.CategoryFacade;
import com.br.startup.tolevBack.finance.application.dto.request.CategoryRequest;
import com.br.startup.tolevBack.finance.application.dto.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * O catálogo de categorias. A leitura devolve sistema + usuário juntos; a
 * escrita só alcança as do usuário — as do sistema são as mesmas para todo mundo.
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryFacade categoryFacade;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories(@RequestParam Long idUsuario) {
        return ResponseEntity.ok(categoryFacade.getAll(idUsuario));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryFacade.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryFacade.update(id, request));
    }

    /**
     * Tira a categoria do catálogo. As transações já classificadas nela ficam
     * como estão — ver {@code DeleteUserCategoryService}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id,
            @RequestParam(required = false) Long idUsuario) {
        categoryFacade.delete(id, idUsuario);
        return ResponseEntity.noContent().build();
    }
}
