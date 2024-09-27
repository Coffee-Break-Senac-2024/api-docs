package br.com.api.docs.services;

import br.com.api.docs.client.SignatureClient;
import br.com.api.docs.domain.entities.UserDoc;
import br.com.api.docs.domain.enums.SignatureType;
import br.com.api.docs.dto.signature.UserSignatureResponse;
import br.com.api.docs.dto.userdoc.UserDocDownloadResponseDTO;
import br.com.api.docs.dto.userdoc.UserDocResponseDTO;
import br.com.api.docs.exceptions.DocumentsUploadException;
import br.com.api.docs.exceptions.InputException;
import br.com.api.docs.exceptions.NotFoundException;
import br.com.api.docs.mapper.UserDocMapper;
import br.com.api.docs.repositories.UserDocRepository;
import br.com.api.docs.utils.UserDocUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
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

    private final SignatureClient signatureClient;

    private final UserDocMapper userDocMapper;

    private final UserDocUtils userDocUtils;

    public UserDocResponseDTO uploadFile(MultipartFile file, UUID categoryId, UUID userId, String documentName, String description) {
        SignatureType signatureType = getSignatureType();

        int maxDocs = userDocUtils.calculateMaxDocs(signatureType);

        if (userDocUtils.canUploadMoreDocs(maxDocs, userId)) {
            Optional<UserDoc> document = this.userDocRepository.findByDocumentNameAndUserIdAndCategoryId(documentName, userId, categoryId);

            if (document.isEmpty()) {
                UserDoc userDoc = new UserDoc();
                userDoc.setUserId(userId);
                userDoc.setCategoryId(categoryId);
                userDoc.setDocumentName(documentName);
                userDoc.setDescription(description);
                userDoc.setDocumentType(file.getContentType());

                String fileName = userId + "/" + file.getOriginalFilename();

                userDoc.setFileUrl(fileName);

                String url = uploadFileToS3(file, fileName);

                UserDoc saved = this.userDocRepository.save(userDoc);

                return userDocMapper.mapEntityToUserDocResponse(saved, url);
            }

            throw new InputException("Ja existe documento com esse nome");
        }
        throw new DocumentsUploadException("Limite de documentos atingido. Remova ou melhore seu plano!");
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

    private SignatureType getSignatureType() {
        UserSignatureResponse signature = signatureClient.getSignature();
        System.out.println(signature + ", signature");
        return signature.getSignature();
    }

    public UserDocDownloadResponseDTO downloadFile(UUID id) {
        UserDoc userDoc = this.userDocRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Documento não encontrado"));

        S3Object s3Object = s3Client.getObject(bucketName, userDoc.getFileUrl());

        try (S3ObjectInputStream inputStream = s3Client.getObject(bucketName, userDoc.getFileUrl()).getObjectContent()) {
            byte[] content = IOUtils.toByteArray(inputStream);

            return userDocMapper.mapToUserDocDownloadResponse(content, userDoc.getDocumentName(), userDoc.getDocumentType());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao baixar o arquivo do S3", e);
        }
    }
}
