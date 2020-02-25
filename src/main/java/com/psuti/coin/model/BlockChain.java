package com.psuti.coin.model;

import com.psuti.coin.util.ResultHelper;
import lombok.Data;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class BlockChain {

    private static final BlockChain instance = new BlockChain();

    private Logger logger = LogManager.getLogger(BlockChain.class);

    private List<Block> chain = new ArrayList<>();

    private Queue<Transaction> transactionQueue = new ArrayDeque<>();

    private BlockChain() {}

    public static BlockChain getInstance() {
        return instance;
    }

    public Block getLastBlock() {
        if(chain.isEmpty()) {
            createGenesisBlock();
        }
        return chain.get(chain.size()-1);
    }

    private void createGenesisBlock() {
        chain.add(new Block(
                "0",
                ResultHelper.getCurrentTime()
        ));
    }

    public boolean isTransactionOutputFree(Transaction.TransactionOutput txOut) {
        for(Block block : chain) {
            for(Transaction transaction : block.getTransactions()) {
                if(transaction.isTxOutInTxIn(txOut)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isTransactionInputFree(Transaction.TransactionInput txIn) {
        return isTransactionOutputFree(txIn.getUTXO());
    }

    public List<Transaction.TransactionOutput> findFreeTransactionOutputsByPublicKey(PublicKey publicKey) {
        List<Transaction.TransactionOutput> freeTxOuts = new ArrayList<>();
        for(Block block : chain) {
            for(Transaction transaction : block.getTransactions().stream().filter(tx -> tx.isTxRecipientOrSender(publicKey)).collect(Collectors.toList())) {
                freeTxOuts.addAll(findFreeTransactionOutputs(transaction, publicKey));
            }
        }
        return freeTxOuts;
    }

    public List<Transaction.TransactionOutput> findFreeTransactionOutputs(Transaction transaction, PublicKey recipientKey) {
        return transaction.getOutputs().stream().
                filter(txOut -> txOut.getRecipientPublicKey().equals(recipientKey) && isTransactionOutputFree(txOut)).
                collect(Collectors.toList());
    }

    public boolean isValidBlock(Block block) {
        return
                block != null &&
                block.getPreviousHash().equals(getLastBlock().getHash()) &&
                block.getHash().equals(block.calculateHash()) &&
                block.getHash().startsWith(Block.getPrefixString(Block.getPrefix())) &&
                block.isValidTransactions();
    }

    public void addBlock(Block block) {
        if(isValidBlock(block)) {
            logger.log(Level.INFO, "valid block " + block.getHash());
            chain.add(block);
        } else {
            logger.log(Level.ERROR, "Block is not valid");
            throw new RuntimeException("Block is not valid");
        }
    }

    public void addTransaction(Transaction transaction) {
        if(transaction.isValid()) {
            logger.log(Level.INFO, "New valid transaction " + transaction.getId());
            transactionQueue.add(transaction);
        } else {
            logger.log(Level.WARN, "Invalid transaction " + transaction.getId());
        }
    }

    public List<Transaction> findIncomeTransactionByPublicKey(PublicKey publicKey) {
        return chain.stream().filter(Objects::nonNull).map(Block::getTransactions).map(transList -> transList.stream().filter(tx -> tx.getRecipientPublicKey().equals(publicKey)).collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<Transaction> findOutcomeTransactionByPublicKey(PublicKey publicKey) {
        return chain.stream().filter(Objects::nonNull).map(Block::getTransactions).map(transList -> transList.stream().filter(tx -> tx.getSenderPublicKey().equals(publicKey)).collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toList());
    }
}
