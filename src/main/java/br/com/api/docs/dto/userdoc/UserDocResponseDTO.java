package br.com.api.docs.dto.userdoc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDocResponseDTO {
    private UUID id;
    private UUID userId;
    private UUID categoryId;
    private String documentName;
    private String documentType;
    private String fileUrl;
    private String accessUrl;
}
