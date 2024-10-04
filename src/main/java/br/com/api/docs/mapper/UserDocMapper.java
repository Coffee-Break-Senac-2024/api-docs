package br.com.api.docs.mapper;

import br.com.api.docs.domain.entities.UserDoc;
import br.com.api.docs.dto.userdoc.UserDocDownloadResponseDTO;
import br.com.api.docs.dto.userdoc.UserDocResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UserDocMapper {

    public UserDocResponseDTO mapEntityToUserDocResponse(UserDoc userDoc, String accessUrl) {
        return UserDocResponseDTO.builder()
                .id(userDoc.getId())
                .userId(userDoc.getUserId())
                .categoryId(userDoc.getCategoryId())
                .documentName(userDoc.getDocumentName())
                .description(userDoc.getDescription())
                .documentType(userDoc.getDocumentType())
                .fileUrl(userDoc.getFileUrl())
                .accessUrl(accessUrl)
                .build();
    }

    public UserDocDownloadResponseDTO mapToUserDocDownloadResponse(byte[] content, String documentName, String documentType) {
        return UserDocDownloadResponseDTO.builder()
                .content(content)
                .documentName(documentName)
                .documentType(documentType)
                .build();
    }
}
