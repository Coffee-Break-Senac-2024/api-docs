package br.com.api.docs.dto.wallet;


import br.com.api.docs.domain.enums.WalletDocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletResponseDTO {
    private UUID id;
    private String documentName;
    private WalletDocumentType walletDocumentType;
    private UUID userId;
    private String url;
}
