package com.psuti.coin.model;

import com.psuti.coin.util.KeyHelper;
import com.psuti.coin.util.ResultHelper;
import lombok.Data;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
public class Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public static Wallet systemWallet = new Wallet();

    public Wallet(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public Wallet(KeyPair keyPair) {
        this(keyPair.getPrivate(), keyPair.getPublic());
    }

    public Wallet() {
        this(KeyHelper.newPair());
    }

    private Logger logger = LogManager.getLogger(Wallet.class);

    public float getBalance() {
        return Transaction.getTotalOutputSum(BlockChain.getInstance().findFreeTransactionOutputsByPublicKey(publicKey));
    }

    public Transaction sendTo(Wallet recipient, float amount) {
        Transaction tx = new Transaction(publicKey, recipient.getPublicKey(), amount);
        tx.sign(privateKey);
        logger.log(Level.INFO, "New tx " + tx.getId());
        if(tx.processTransaction()) {
            BlockChain.getInstance().getTransactionQueue().add(tx);
        }
        return tx;
    }

    public String getPublicKeyStringEncoded() {
        return ResultHelper.getStringFromKey(publicKey);
    }
}
