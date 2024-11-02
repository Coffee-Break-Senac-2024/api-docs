package br.com.api.docs.services;

import br.com.api.docs.repositories.UserDocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class HashService {

    private final UserDocRepository userDocRepository;

    public void generateHash(byte[] input) {
        String hash = generateHashWithAlgoritm("SHA-256", input);

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

//    private String modifyHash(String hash) {
//
//    }

}
