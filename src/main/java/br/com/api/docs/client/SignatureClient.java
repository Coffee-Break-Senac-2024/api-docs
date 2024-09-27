package br.com.api.docs.client;

import br.com.api.docs.dto.signature.UserSignatureResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "signature-client", url = "${signature.service.url}")
public interface SignatureClient {

    @GetMapping("/signature")
    UserSignatureResponse getSignature();

}
