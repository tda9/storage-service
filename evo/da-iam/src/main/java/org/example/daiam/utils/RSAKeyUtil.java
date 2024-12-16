package org.example.daiam.utils;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

import static org.springframework.security.config.Elements.JWT;

@Component
public class RSAKeyUtil {
    public PrivateKey getPrivateKey() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("keys/private.pem")) {
            String privateKeyPEM = null;
            if (inputStream != null) {
                privateKeyPEM = new String(inputStream.readAllBytes())
                        .replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")
                        .replaceAll("\\s", "");
            }
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public PublicKey getPublicKey() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("keys/public.pem")) {
            String publicKeyPEM = null;
            if (inputStream != null) {
                publicKeyPEM = new String(inputStream.readAllBytes())
                        .replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")
                        .replaceAll("\\s", "");
            }
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyPEM);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private final KeyPair keyPair;

    public RSAKeyUtil() {
        this.keyPair = generateKeyPair();
    }

    private KeyPair generateKeyPair() {
        PublicKey publicKey = getPublicKey();
        PrivateKey privateKey = getPrivateKey();
        return new KeyPair(publicKey, privateKey);
    }

    public JWKSet jwkSet() {
        RSAKey.Builder builder = new RSAKey.Builder(
                (RSAPublicKey) this.keyPair.getPublic())
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID(UUID.randomUUID().toString());
        return new JWKSet(builder.build());
    }
}
