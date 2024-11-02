package br.com.api.docs.dto.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WalletDownloadResponseDTO {
    private byte[] content;
    private String documentName;
    private String documentType;
}
