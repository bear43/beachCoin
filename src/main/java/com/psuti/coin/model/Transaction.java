package com.psuti.coin.model;

import com.psuti.coin.util.ResultHelper;
import com.psuti.coin.util.SignatureHelper;
import lombok.Data;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class Transaction {
    private String id;//hash
    private PublicKey senderPublicKey;
    private PublicKey recipientPublicKey;
    private float amount;
    private byte[] signature;
    private Set<TransactionInput> inputs = new HashSet<>();
    private Set<TransactionOutput> outputs = new HashSet<>();
    private static long sequence;

    private Logger logger = LogManager.getLogger(Transaction.class);

    @Data
    public static class TransactionInput {
        public String transactionOutputId; //Reference to TransactionOutputs -> transactionId
        public TransactionOutput UTXO; //Contains the Unspent transaction output

        public TransactionInput(String transactionOutputId) {
            this.transactionOutputId = transactionOutputId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TransactionInput that = (TransactionInput) o;
            return Objects.equals(transactionOutputId, that.transactionOutputId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(transactionOutputId);
        }
    }

    @Data
    public static class TransactionOutput {
        public String id;
        public PublicKey recipientPublicKey; //also known as the new owner of these coins.
        public float amount; //the amount of coins they own
        public String parentTransactionId; //the id of the transaction this output was created in

        //Constructor
        public TransactionOutput(PublicKey recipientPublicKey, float amount, String parentTransactionId) {
            this.recipientPublicKey = recipientPublicKey;
            this.amount = amount;
            this.parentTransactionId = parentTransactionId;
            this.id = ResultHelper.applySha256(ResultHelper.getStringFromKey(recipientPublicKey)+Float.toString(amount)+parentTransactionId);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TransactionOutput that = (TransactionOutput) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    public Transaction(PublicKey senderPublicKey, PublicKey recipientPublicKey, float amount) {
        this.senderPublicKey = senderPublicKey;
        this.recipientPublicKey = recipientPublicKey;
        this.amount = amount;
        this.id = calculateHash();
    }

    public String calculateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return ResultHelper.applySha256(
                ResultHelper.getStringFromKey(senderPublicKey) +
                        ResultHelper.getStringFromKey(recipientPublicKey) +
                        amount +
                        sequence
        );
    }

    //Signs all the data we dont wish to be tampered with.
    public void sign(PrivateKey privateKey) {
        String data = ResultHelper.getStringFromKey(senderPublicKey) +
                ResultHelper.getStringFromKey(recipientPublicKey) +
                amount;
        signature = SignatureHelper.applyECDSASig(privateKey,data);
    }
    //Verifies the data we signed hasn't been tampered with
    public boolean verifySignature() {
        String data = ResultHelper.getStringFromKey(senderPublicKey) +
                ResultHelper.getStringFromKey(recipientPublicKey) +
                amount;
        return SignatureHelper.verifyECDSASig(senderPublicKey, data, signature);
    }

    public boolean processTransaction() {
        /*
        1. Get all sender actives
        2. Sum all actives to amount value
        3. (total sender amount value) - (transaction amount)
        4. if (3) < 0 then throw exception that sender has no available actives
        5. if (3) = 0 then need to create transaction outputs:
           first will has amount transfer to recipient
        6. second will has change of sender wallet (if (3) > 0 )
        */
        if(amount <= 0) {
            throw new RuntimeException("Нельзя перевести число меньше или равное нулю");
        }
        if(senderPublicKey.equals(Wallet.systemWallet.getPublicKey())) {
            logger.debug("Processing system transaction: " + id);
            outputs.add(new TransactionOutput(recipientPublicKey, amount, id));
            return true;
        } else {
            if(senderPublicKey.equals(recipientPublicKey)) {
                throw new RuntimeException("Нельзя перевести средства самому себе на счёт");
            }
            BlockChain.getInstance().findFreeTransactionOutputsByPublicKey(senderPublicKey).forEach(output -> {
                TransactionInput ti = new TransactionInput(output.getId());
                ti.setUTXO(output);
                inputs.add(ti);
            });
            float totalSenderActives = getTotalInputSum();
            float activeChange = totalSenderActives - amount;
            if (activeChange < 0) {
                throw new RuntimeException("Недостаточно средств");
            } else {
                logger.debug("Processing transaction: " + id);
                if (activeChange != 0) {
                    outputs.add(new TransactionOutput(senderPublicKey, activeChange, id));
                }
                outputs.add(new TransactionOutput(recipientPublicKey, amount, id));
                return verifyTransactionIO();
            }
        }
    }

    public boolean verifyTransactionIO() {
        return (getTotalInputSum() - getTotalOutputSum()) == 0;
    }

    public static float getTotalInputSum(Collection<TransactionInput> txIns) {
        return txIns.stream().
                filter(input -> input != null && input.getUTXO() != null).
                map(input -> input.getUTXO().amount).
                reduce(Float::sum).
                orElse(0.0f);
    }

    public float getTotalInputSum() {
        return getTotalInputSum(inputs);
    }

    public static float getTotalOutputSum(Collection<TransactionOutput> txOuts) {
        return txOuts.stream().
                filter(Objects::nonNull).
                map(TransactionOutput::getAmount).
                reduce(Float::sum).
                orElse(0.0f);
    }

    public float getTotalOutputSum() {
        return getTotalOutputSum(outputs);
    }

    public boolean isTxOutInTxIn(TransactionOutput txOut) {
        return inputs.stream().
                filter(tx -> tx != null && tx.getUTXO() != null).
                anyMatch(tx -> tx.getUTXO().getId().equals(txOut.getId()));
    }

    public boolean isTxRecipient(PublicKey recipientPublicKey) {
        return this.recipientPublicKey.equals(recipientPublicKey);
    }

    public boolean isTxSender(PublicKey senderPublicKey) {
        return this.getSenderPublicKey().equals(senderPublicKey);
    }

    public boolean isTxRecipientOrSender(PublicKey publicKey) {
        return isTxRecipient(publicKey) || isTxSender(publicKey);
    }

    public boolean isValid() {
        boolean systemWallet = senderPublicKey.equals(Wallet.systemWallet.getPublicKey());
        return (!senderPublicKey.equals(recipientPublicKey) || systemWallet) &&
                verifySignature() &&
                (verifyTransactionIO() || systemWallet) &&
                (isValidOutputs() || systemWallet);
    }

    public boolean isValidOutputs() {
        return outputs.stream().anyMatch(output -> !output.getRecipientPublicKey().equals(senderPublicKey));
    }
}
