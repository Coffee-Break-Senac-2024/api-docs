package br.com.api.docs.services;

import br.com.api.docs.domain.entities.Wallet;
import br.com.api.docs.domain.enums.WalletDocumentType;
import br.com.api.docs.dto.hash.HashResponseDTO;
import br.com.api.docs.dto.hash.WalletHashResponseDTO;
import br.com.api.docs.dto.wallet.WalletDownloadResponseDTO;
import br.com.api.docs.dto.wallet.WalletListResponseDTO;
import br.com.api.docs.dto.wallet.WalletResponseDTO;
import br.com.api.docs.exceptions.DocumentDownloadException;
import br.com.api.docs.exceptions.DocumentsUploadException;
import br.com.api.docs.exceptions.InputException;
import br.com.api.docs.exceptions.NotFoundException;
import br.com.api.docs.repositories.WalletRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    @Value("${application.bucket.name}")
    private String bucketName;

    private final WalletRepository walletRepository;

    private final UserDocService userDocService;

    private final HashService hashService;

    private final AmazonS3 s3Client;

    @Transactional(rollbackFor = Exception.class)
    public WalletResponseDTO saveDocumentToWallet(MultipartFile file, UUID userId, String documentName, WalletDocumentType walletType) {
        Optional<Wallet> walletFound = this.walletRepository.findByUserIdAndDocumentType(userId, walletType);

        if (walletFound.isPresent()) {
            throw new InputException("Você já possui "+walletType.name() + " cadastrado.");
        }

        HashResponseDTO hashResponseDTO = null;

        try {
            byte[] fileBytes = file.getBytes();

            hashResponseDTO = hashService.generateHash(fileBytes);
        } catch (IOException e) {
            throw new DocumentsUploadException("Erro ao ler o arquivo para o hash.");
        } catch (NoSuchAlgorithmException e) {
            throw new DocumentsUploadException("Erro ao criar hash");
        }

        Wallet wallet = new Wallet();
        wallet.setDocumentType(walletType);
        wallet.setDocumentName(documentName);
        wallet.setDocumentFileType(file.getContentType());
        wallet.setUserId(userId);
        wallet.setHash(hashResponseDTO.getHash());
        wallet.setHashRSA(hashResponseDTO.getHashRsa());
        wallet.setPublicKey(hashResponseDTO.getPublicKey());

        String fileUrl = "wallet/" +userId + "/" + file.getOriginalFilename();

        wallet.setFileUrl(fileUrl);

        String urlToS3 = this.userDocService.uploadFileToS3(file, fileUrl);

        Wallet save = this.walletRepository.save(wallet);

        return WalletResponseDTO.builder()
                .documentName(save.getDocumentName())
                .walletDocumentType(save.getDocumentType())
                .url(urlToS3)
                .userId(userId)
                .build();
    }

    public WalletListResponseDTO getWallet(UUID userId) {
        List<Wallet> walletDocuments = this.walletRepository.findAllByUserId(userId);

        if (walletDocuments.isEmpty()) {
            throw new NotFoundException("Não possui nenhum documento cadastrado na wallet");
        }

        List<WalletResponseDTO> wallet = walletDocuments.stream().map((item) -> WalletResponseDTO.builder()
                .id(item.getId())
                .walletDocumentType(item.getDocumentType())
                .documentName(item.getDocumentName())
                .userId(item.getUserId())
                .url(item.getFileUrl())
                .build()).toList();

        return WalletListResponseDTO
                .builder()
                .wallet(wallet)
                .build();
    }

    public WalletDownloadResponseDTO downloadDocumentFromWallet(UUID id) {
        Wallet wallet = this.walletRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Documento da carteira não encontrado."));

        try (S3ObjectInputStream inputStream = s3Client.getObject(bucketName, wallet.getFileUrl()).getObjectContent()) {
            byte[] content = IOUtils.toByteArray(inputStream);

           return WalletDownloadResponseDTO
                   .builder()
                   .content(content)
                   .documentName(wallet.getDocumentName())
                   .documentType(wallet.getDocumentFileType())
                   .build();
        } catch (IOException e) {
            throw new DocumentDownloadException("Erro ao baixar o arquivo do S3");
        }
    }

    public WalletHashResponseDTO getWalletToValidation(UUID id) {
        Wallet walletDocument = this.walletRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Documento nao encontrado para validacao"));

        return WalletHashResponseDTO.builder()
                .documentName(walletDocument.getDocumentName())
                .hashRsa(walletDocument.getHashRSA())
                .publicKey(walletDocument.getPublicKey())
                .build();
    }
}
