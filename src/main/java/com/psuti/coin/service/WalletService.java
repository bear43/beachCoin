package com.psuti.coin.service;

import com.psuti.coin.dto.TransactionDTO;
import com.psuti.coin.dto.TxInfoDTO;
import com.psuti.coin.form.SendForm;
import com.psuti.coin.model.Wallet;
import com.psuti.coin.util.ResultHelper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.List;

@Service
@SessionScope
@Data
public class WalletService {

    private Wallet wallet = new Wallet();

    @Autowired
    private TransactionService transactionService;

    public String sendTo(SendForm form) {
        Wallet recipient = new Wallet(null, ResultHelper.getKeyFromString(form.getPublicKey()));
        wallet.sendTo(recipient, form.getAmount());
        return "Транзакция поставлена в очередь";
    }

    public String sendFromSystem(Float amount) {
        Wallet.systemWallet.sendTo(wallet, amount);
        return "Транзакция поставлена в очередь, ожидайте пополнения баланса";
    }

    public TxInfoDTO getAllTransaction() {
        return new TxInfoDTO(
                transactionService.getIncomeTransactions(wallet.getPublicKey()),
                transactionService.getOutcomeTransactions(wallet.getPublicKey())
        );
    }
}
