package br.com.api.docs.dto.hash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletHashResponseDTO {
    private String documentName;
    private String hash;
    private String hashRsa;
    private String publicKey;
}
