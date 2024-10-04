package br.com.api.docs.client;

import br.com.api.docs.config.ClientConfiguration;
import br.com.api.docs.dto.signature.UserSignatureResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@FeignClient(name = "signature-client", url = "${signature.service.url}", configuration = ClientConfiguration.class)
public interface SignatureClient {

    @GetMapping("/signature")
    UserSignatureResponse getSignature();

   @PatchMapping("/signature/update-count")
   CompletableFuture<Void> updateDocumentCount(@RequestHeader("documentCount") int documentCount);
}
