package br.com.api.docs.services;

import br.com.api.docs.client.SignatureClient;
import br.com.api.docs.domain.entities.UserDoc;
import br.com.api.docs.dto.signature.UserSignatureResponse;
import br.com.api.docs.dto.userdoc.ListUserDocResponseDTO;
import br.com.api.docs.dto.userdoc.UserDocDownloadResponseDTO;
import br.com.api.docs.dto.userdoc.UserDocResponseDTO;
import br.com.api.docs.exceptions.DocumentsUploadException;
import br.com.api.docs.exceptions.InputException;
import br.com.api.docs.exceptions.NotFoundException;
import br.com.api.docs.mapper.UserDocMapper;
import br.com.api.docs.repositories.UserDocRepository;
import br.com.api.docs.utils.UserDocUtils;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
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

    @Transactional(rollbackFor = Exception.class)
    public UserDocResponseDTO uploadFile(MultipartFile file, UUID categoryId, UUID userId, String documentName, String description) {
        UserSignatureResponse userSignatureResponse = getSignatureType();

        int maxDocs = userDocUtils.calculateMaxDocs(userSignatureResponse.getSignature());

        if (userDocUtils.canUploadMoreDocs(maxDocs, userSignatureResponse.getDocumentCount())) {
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

                int newDocumentCount = userSignatureResponse.getDocumentCount() + 1;
                this.signatureClient.updateDocumentCount(newDocumentCount);

                return userDocMapper.mapEntityToUserDocResponse(saved, url);
            }

            throw new InputException("Ja existe documento com esse nome");
        }
        throw new DocumentsUploadException("Limite de documentos atingido. Remova ou melhore seu plano!");
    }

    public String uploadFileToS3(MultipartFile file, String fileName) {
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

    private UserSignatureResponse getSignatureType() {
        UserSignatureResponse signature = signatureClient.getSignature();
        System.out.println(signature + ", signature");
        return signature;
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

    public ListUserDocResponseDTO getDocumentsByCategoryId(UUID userId, UUID categoryId) {
        List<UserDoc> documents = this.userDocRepository.findAllByUserIdAndCategoryId(userId, categoryId);

        if (documents.isEmpty()) {
            throw new NotFoundException("Nenhum documento cadastrado nessa categoria.");
        }

        List<UserDocResponseDTO> documentsResponse = documents.stream().map((document) -> UserDocResponseDTO.builder()
                .id(document.getId())
                .documentName(document.getDocumentName())
                .description(document.getDescription())
                .documentType(document.getDocumentType())
                .build()).toList();

        return ListUserDocResponseDTO.builder()
                .documents(documentsResponse)
                .build();
    }

    public void deleteFile(UUID id, UUID userId) {
        UserDoc userDoc = this.userDocRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Documento não encontrado."));

        try {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, userDoc.getFileUrl()));
        } catch (Exception e) {
            throw new SdkClientException("Não foi possivel deletar o arquivo");
        }

        this.userDocRepository.deleteById(id);
    }
}
