package br.com.api.docs.controllers;

import br.com.api.docs.domain.enums.WalletDocumentType;
import br.com.api.docs.dto.hash.WalletHashResponseDTO;
import br.com.api.docs.dto.wallet.WalletDownloadResponseDTO;
import br.com.api.docs.dto.wallet.WalletListResponseDTO;
import br.com.api.docs.dto.wallet.WalletResponseDTO;
import br.com.api.docs.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/create")
    public ResponseEntity<WalletResponseDTO> create(@RequestParam("file") MultipartFile file,
                                                    @RequestParam("documentName") String documentName,
                                                    @RequestParam("walletDocumentType") WalletDocumentType walletDocumentType,
                                                    Principal principal) {
        String userId = principal.getName();
        return ResponseEntity.ok().body(walletService.saveDocumentToWallet(file, UUID.fromString(userId), documentName, walletDocumentType));
    }

    @GetMapping()
    public ResponseEntity<WalletListResponseDTO> getWallet(Principal principal) {
        String userId = principal.getName();
        return ResponseEntity.ok(walletService.getWallet(UUID.fromString(userId)));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("id") UUID id) {
        WalletDownloadResponseDTO download = this.walletService.downloadDocumentFromWallet(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(download.getDocumentType()));
        headers.setContentDispositionFormData("attachment", download.getDocumentName());

        return new ResponseEntity<>(download.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}/verify")
    public ResponseEntity<WalletHashResponseDTO> verifyFile(@PathVariable("id") UUID id) {
        return ResponseEntity.ok().body(walletService.getWalletToValidation(id));
    }

}
