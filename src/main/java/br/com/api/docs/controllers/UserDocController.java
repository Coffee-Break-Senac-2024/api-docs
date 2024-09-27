package br.com.api.docs.controllers;

import br.com.api.docs.dto.userdoc.UserDocResponseDTO;
import br.com.api.docs.services.UserDocService;
import lombok.RequiredArgsConstructor;
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

}
