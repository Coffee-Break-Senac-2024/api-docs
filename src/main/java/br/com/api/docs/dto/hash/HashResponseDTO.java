package br.com.api.docs.dto.hash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.PublicKey;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HashResponseDTO {
    private String hash;
    private String hashRsa;
    private String publicKey;
}
