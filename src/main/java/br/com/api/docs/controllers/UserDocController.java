package br.com.api.docs.controllers;

import br.com.api.docs.dto.userdoc.UserDocDownloadResponseDTO;
import br.com.api.docs.dto.userdoc.UserDocResponseDTO;
import br.com.api.docs.services.UserDocService;
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
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserDocController {

    private final UserDocService userDocService;

    @PostMapping("/{categoryId}/create")
    public ResponseEntity<UserDocResponseDTO> createDocument(@PathVariable("categoryId") UUID categoryId,
                                                             @RequestParam("file") MultipartFile file,
                                                             @RequestParam("documentName") String documentName,
                                                             @RequestParam("description") String description,
                                                             Principal principal) {
        String userId = principal.getName();

        return ResponseEntity.ok().body(this.userDocService.uploadFile(file, categoryId, UUID.fromString(userId), documentName, description));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("id") UUID id) {
        UserDocDownloadResponseDTO download = this.userDocService.downloadFile(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(download.getDocumentType()));
        headers.setContentDispositionFormData("attachment", download.getDocumentName());

        return new ResponseEntity<>(download.getContent(), headers, HttpStatus.OK);
    }

}
