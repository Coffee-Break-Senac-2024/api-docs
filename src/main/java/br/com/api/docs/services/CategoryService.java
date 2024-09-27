package br.com.api.docs.services;

import br.com.api.docs.domain.entities.Category;
import br.com.api.docs.dto.category.CategoryListResponseDTO;
import br.com.api.docs.dto.category.CategoryRequest;
import br.com.api.docs.dto.category.CategoryResponseDTO;
import br.com.api.docs.exceptions.InputException;
import br.com.api.docs.exceptions.NotFoundException;
import br.com.api.docs.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponseDTO createCategory(UUID userId, CategoryRequest categoryRequest) {
        Optional<Category> categoryFound = categoryRepository.findByName(categoryRequest.getName());

        if (categoryFound.isEmpty()) {
            Category newCategory = new Category();
            newCategory.setName(categoryRequest.getName());
            newCategory.setDescription(categoryRequest.getDescription());
            newCategory.setUserId(userId);


            Category category = this.categoryRepository.save(newCategory);

            return CategoryResponseDTO.builder()
                    .name(category.getName())
                    .description(category.getDescription())
                    .id(category.getId())
                    .build();
        }

        throw new InputException("Categoria já foi cadastrada");
    }

    public CategoryListResponseDTO getCategories(UUID userId) {
        List<Category> categories = this.categoryRepository.findAllByUserId(userId);

        if (categories.isEmpty()) {
            throw new NotFoundException("Nenhuma categoria encontrada por este usuario");
        }

        List<CategoryResponseDTO> categoriesResponse = categories.stream().map((category) -> {
            CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();

            categoryResponseDTO.setId(category.getId());
            categoryResponseDTO.setName(category.getName());

            return categoryResponseDTO;
        }).toList();

        return CategoryListResponseDTO.builder()
                .categories(categoriesResponse)
                .build();
    }

}
