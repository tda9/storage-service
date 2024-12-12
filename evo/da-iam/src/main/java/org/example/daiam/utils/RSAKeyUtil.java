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
    private static final String PRIVATE_KEY_PATH = "src/main/resources/keys/private.pem";
    private static final String PUBLIC_KEY_PATH = "src/main/resources/keys/public.pem";

    public PrivateKey getPrivateKey() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("keys/private.pem");
        String privateKeyPEM = null;
        KeyFactory keyFactory = null;
        PKCS8EncodedKeySpec spec = null;
        try {
            //privateKeyPEM = new String(Files.readAllBytes(Paths.get(PRIVATE_KEY_PATH)))
            privateKeyPEM = new String(inputStream.readAllBytes())
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);
            spec = new PKCS8EncodedKeySpec(keyBytes);
            keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (IOException |
                 NoSuchAlgorithmException |
                 InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public PublicKey getPublicKey() throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("keys/public.pem");

        //String publicKeyPEM = new String(Files.readAllBytes(Paths.get(PUBLIC_KEY_PATH)))
        String publicKeyPEM = new String(inputStream.readAllBytes())
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }
    private final KeyPair keyPair;
    public RSAKeyUtil() {
        try {
            this.keyPair = generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing KeyUtil", e);
        }
    }
    private KeyPair generateKeyPair() throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = getPublicKey();
        PrivateKey privateKey = getPrivateKey();
        return new KeyPair(publicKey, privateKey);
    }
    public JWKSet jwkSet() {
        if (keyPair == null) {
            throw new IllegalStateException("KeyPair not initialized");
        }

        RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) this.keyPair.getPublic())
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID(UUID.randomUUID().toString());
        return new JWKSet(builder.build());
    }
}
