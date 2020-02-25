package com.psuti.coin.model;


import com.psuti.coin.util.ResultHelper;
import lombok.Data;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Data
public class Block {
    private String hash;
    private String previousHash;
    private List<Transaction> transactions = new ArrayList<>();
    private long timeStamp;
    private String merkleRoot;
    private int nonce;
    private static int prefix = 3;

    private Logger logger = LogManager.getLogger(Block.class);

    public Block(String previousHash, long timeStamp) {
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String dataToHash = previousHash
                + timeStamp
                + nonce
                + transactions
                + merkleRoot;
        MessageDigest digest = null;
        byte[] bytes = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes(UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            logger.log(Level.ERROR, ex.getMessage());
        }
        if(bytes == null || bytes.length == 0) {
            RuntimeException ex = new RuntimeException("There is no bytes after block hashing");
            logger.log(Level.ERROR, ex);
            throw ex;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(byte incomingByte : bytes) {
            stringBuilder.append(String.format("%02x", incomingByte));
        }
        return stringBuilder.toString();
    }

    public static int getPrefix() {
        return prefix;
    }

    public static String getPrefixString(int prefix) {
        return new String(new char[prefix]).replace('\0', '0');
    }

    public String mineBlock(int prefix) {
        merkleRoot = ResultHelper.getMerkleRoot(transactions);
        String prefixString = getPrefixString(prefix);
        while (!hash.substring(0, prefix).equals(prefixString)) {
            nonce++;
            hash = calculateHash();
        }
        return hash;
    }

    public String mineBlock() {
        return mineBlock(prefix);
    }


    public boolean addTransaction(Transaction transaction) {
        //process transaction and check if valid, unless block is genesis block then ignore.
        if(transaction == null) return false;
/*        if((!previousHash.equals("0"))) {
            if(!transaction.processTransaction()) {
                logger.debug("Transaction failed to process. Discarded.");
                return false;
            }
        }*/
        if(!transaction.processTransaction()) {
            logger.debug("Transaction " + transaction.getId() +" failed to process. Discarded.");
            return false;
        }
        transactions.add(transaction);
        logger.debug("Transaction " + transaction.getId() + " Successfully added to Block");
        return true;
    }

    public boolean isValidTransactions() {
        for(Transaction transaction : transactions) {
            if(!transaction.isValid()) {
                return false;
            }
            for(Transaction.TransactionInput txIn : transaction.getInputs()) {
                if(!BlockChain.getInstance().isTransactionInputFree(txIn)) {
                    return false;
                }
            }
        }
        return true;
    }
}
