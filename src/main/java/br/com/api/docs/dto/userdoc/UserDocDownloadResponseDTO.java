package br.com.api.docs.dto.userdoc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDocDownloadResponseDTO {
    private byte[] content;
    private String documentName;
    private String documentType;
}
