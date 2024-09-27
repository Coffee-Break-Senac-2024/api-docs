package br.com.api.docs.repositories;

import br.com.api.docs.domain.entities.UserDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDocRepository extends JpaRepository<UserDoc, UUID> {
    Optional<UserDoc> findByDocumentNameAndUserIdAndCategoryId(String documentName, UUID userId, UUID categoryId);
}
