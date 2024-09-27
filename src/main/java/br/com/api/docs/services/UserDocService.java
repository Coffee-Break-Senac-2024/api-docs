package br.com.api.docs.services;

import br.com.api.docs.domain.entities.UserDoc;
import br.com.api.docs.dto.userdoc.UserDocResponseDTO;
import br.com.api.docs.exceptions.InputException;
import br.com.api.docs.repositories.UserDocRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDocService {

    @Value("${application.bucket.name}")
    private String bucketName;

    private final AmazonS3 s3Client;

    private final UserDocRepository userDocRepository;

    public UserDocResponseDTO uploadFile(MultipartFile file, UUID categoryId, UUID userId, String documentName, String description) {
        Optional<UserDoc> document = this.userDocRepository.findByDocumentNameAndUserIdAndCategoryId(documentName, userId, categoryId);

        if (document.isEmpty()) {
            UserDoc userDoc = new UserDoc();
            userDoc.setUserId(userId);
            userDoc.setCategoryId(categoryId);
            userDoc.setDocumentName(documentName);
            userDoc.setDescription(description);
            userDoc.setDocumentType(file.getContentType());

            String fileName = userId + "/" +file.getOriginalFilename();

            userDoc.setFileUrl(fileName);

            String url = uploadFileToS3(file, fileName);

            UserDoc saved = this.userDocRepository.save(userDoc);

            return UserDocResponseDTO.builder()
                    .id(saved.getId())
                    .userId(saved.getUserId())
                    .categoryId(saved.getCategoryId())
                    .documentName(saved.getDocumentName())
                    .documentType(saved.getDocumentType())
                    .fileUrl(saved.getFileUrl())
                    .accessUrl(url)
                    .build();
        }

        throw new InputException("Ja existe documento com esse nome");
    }

    private String uploadFileToS3(MultipartFile file, String fileName) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

       return s3Client.getUrl(bucketName, fileName).toString();
    }
}
