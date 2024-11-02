package br.com.api.docs.repositories;

import br.com.api.docs.domain.entities.Wallet;
import br.com.api.docs.domain.enums.WalletDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    Optional<Wallet> findByUserIdAndDocumentType(UUID userId, WalletDocumentType walletDocumentType);
    List<Wallet> findAllByUserId(UUID userId);
}
