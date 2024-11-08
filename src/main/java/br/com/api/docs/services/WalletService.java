package br.com.api.docs.services;

import br.com.api.docs.domain.entities.Wallet;
import br.com.api.docs.domain.enums.WalletDocumentType;
import br.com.api.docs.dto.wallet.WalletDownloadResponseDTO;
import br.com.api.docs.dto.wallet.WalletListResponseDTO;
import br.com.api.docs.dto.wallet.WalletResponseDTO;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    private final AmazonS3 s3Client;

    public WalletResponseDTO saveDocumentToWallet(MultipartFile file, UUID userId, String documentName, WalletDocumentType walletType) {
        Optional<Wallet> walletFound = this.walletRepository.findByUserIdAndDocumentType(userId, walletType);

        if (walletFound.isPresent()) {
            throw new InputException("Você já possui "+walletType.name() + " cadastrado.");
        }

        Wallet wallet = new Wallet();
        wallet.setDocumentType(walletType);
        wallet.setDocumentName(documentName);
        wallet.setDocumentFileType(file.getContentType());
        wallet.setHash(UUID.randomUUID().toString());
        wallet.setHashRSA(UUID.randomUUID().toString());
        wallet.setUserId(userId);

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
            throw new RuntimeException("Erro ao baixar o arquivo do S3", e);
        }
    }
}
