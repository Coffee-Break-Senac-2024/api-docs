package br.com.api.docs.utils;


import br.com.api.docs.domain.enums.SignatureType;
import br.com.api.docs.repositories.UserDocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserDocUtils {

    private final UserDocRepository userDocRepository;

    public int calculateMaxDocs(SignatureType signatureType) {
        return switch (signatureType) {
            case MONTHLY -> 10;
            case QUARTERLY -> 20;
            case ANNUAL -> 30;
        };
    }

    public boolean canUploadMoreDocs(int maxDocs, int documentCount) {
        return documentCount < maxDocs;
    }

}
