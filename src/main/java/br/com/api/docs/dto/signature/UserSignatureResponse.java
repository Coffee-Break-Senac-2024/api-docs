package br.com.api.docs.dto.signature;

import br.com.api.docs.domain.enums.SignatureType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSignatureResponse {
    public SignatureType signature;
    public LocalDateTime signedAt;
}
