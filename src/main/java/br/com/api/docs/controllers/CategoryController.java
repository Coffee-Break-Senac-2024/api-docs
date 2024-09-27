package br.com.api.docs.controllers;

import br.com.api.docs.dto.category.CategoryRequest;
import br.com.api.docs.dto.category.CategoryResponseDTO;
import br.com.api.docs.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/category/create")
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequest categoryRequest, Principal principal) {
        String userId = principal.getName();
        return ResponseEntity.ok().body(this.categoryService.createCategory(UUID.fromString(userId), categoryRequest));
    }

}