package com.psuti.coin.dto;

import com.psuti.coin.model.Transaction;
import com.psuti.coin.model.Wallet;
import com.psuti.coin.util.ResultHelper;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class TransactionDTO {
    private String id;
    private String sender;
    private String recipient;
    private Float amount;
    private List<String> inputs;
    private List<String> outputs;

    public TransactionDTO() {
    }

    public TransactionDTO(Transaction transaction) {
        if(transaction != null) {
            id = transaction.getId();
            sender = Wallet.systemWallet.getPublicKey().equals(transaction.getSenderPublicKey()) ? "Система" : ResultHelper.getStringFromKey(transaction.getSenderPublicKey());
            recipient = ResultHelper.getStringFromKey(transaction.getRecipientPublicKey());
            amount = transaction.getAmount();
            inputs = transaction.getInputs().stream().filter(Objects::nonNull).map(in -> String.format("ID: %s, Средства: %s", in.UTXO.getId(), in.UTXO.getAmount())).collect(Collectors.toList());
            outputs = transaction.getOutputs().stream().filter(Objects::nonNull).map(out -> String.format("ID: %s, Средства: %s", out.getId(), out.getAmount())).collect(Collectors.toList());
        }
    }
}
