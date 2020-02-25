package com.psuti.coin.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import sun.security.ec.ECKeyPairGenerator;
import sun.security.ec.ECPrivateKeyImpl;
import sun.security.ec.ECPublicKeyImpl;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyHelper {

    private static Logger logger = LogManager.getLogger(KeyHelper.class);

    private static KeyPairGenerator generator;

    static {
        Security.addProvider(new BouncyCastleProvider());
        try {
            generator = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            // Initialize the key generator and generate a KeyPair
            generator.initialize(ecSpec, random);   //256 bytes provides an acceptable security level
            KeyPair keyPair = generator.generateKeyPair();
        } catch (Exception ex) {
            logger.log(Level.ERROR, ex);
            throw new RuntimeException(ex);
        }
    }

    public static KeyPair newPair() {
        return generator.generateKeyPair();
    }

    public static KeyPair newPair(byte[] privateKey, byte[] publicKey) {
        try {
            ECPrivateKeyImpl _privateKey = new ECPrivateKeyImpl(privateKey);
            ECPublicKeyImpl _publicKey = new ECPublicKeyImpl(publicKey);
            return new KeyPair(_publicKey, _privateKey);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
