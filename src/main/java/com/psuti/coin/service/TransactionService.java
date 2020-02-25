package com.psuti.coin.service;

import com.psuti.coin.dto.TransactionDTO;
import com.psuti.coin.model.Block;
import com.psuti.coin.model.BlockChain;
import com.psuti.coin.model.Transaction;
import com.psuti.coin.model.Wallet;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    public List<TransactionDTO> getIncomeTransactions(PublicKey publicKey) {
        return BlockChain.getInstance().findIncomeTransactionByPublicKey(publicKey).stream().map(TransactionDTO::new).collect(Collectors.toList());
    }

    public List<TransactionDTO> getOutcomeTransactions(PublicKey publicKey) {
        return BlockChain.getInstance().findOutcomeTransactionByPublicKey(publicKey).stream().map(TransactionDTO::new).collect(Collectors.toList());
    }
}
