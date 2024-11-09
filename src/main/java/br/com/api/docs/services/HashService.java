package br.com.api.docs.services;

import br.com.api.docs.dto.hash.HashResponseDTO;
import br.com.api.docs.repositories.UserDocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.*;
import java.util.Base64;

@Service
public class HashService {

    public HashResponseDTO generateHash(byte[] input) throws NoSuchAlgorithmException {
        String hash = generateHashWithAlgoritm("SHA-256", input);

        KeyPair keyPair = generateKeyPair();

        String hashRsa = signHashWithRsaPrivateKey(hash, keyPair.getPrivate());

        String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

        return HashResponseDTO.builder()
                .hash(hash)
                .hashRsa(hashRsa)
                .publicKey(publicKeyBase64)
                .build();
    }

    private String generateHashWithAlgoritm(String algorithm, byte[] input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            byte[] hashBytes = messageDigest.digest(input);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen =  KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }
    private String signHashWithRsaPrivateKey(String hash, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(hash.getBytes());

            byte[] signedBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro tentando assinar hash");
        }
    }
}
