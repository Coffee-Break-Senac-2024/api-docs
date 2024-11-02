package br.com.api.docs.domain.entities;

import br.com.api.docs.domain.enums.WalletDocumentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "wallet_tb")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "document_type", nullable = false, unique = true)
    private WalletDocumentType documentType;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "document_file_type", nullable = false)
    private String documentFileType;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "original_hash", nullable = true, unique = true)
    private String hash;

    @Column(name = "rsa_hash", nullable = true, unique = true)
    private String hashRSA;

    @Column(name = "user_id", nullable = false)
    private UUID userId;
}
