package br.com.api.docs.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        String token = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();

        if (token != null) {
            requestTemplate.header("Authorization", "Bearer "+token);
        }
    }
}
