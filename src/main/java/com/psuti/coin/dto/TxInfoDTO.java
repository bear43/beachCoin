package com.psuti.coin.dto;

import com.psuti.coin.model.Transaction;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TxInfoDTO {
    private List<TransactionDTO> income = new ArrayList<>();
    private List<TransactionDTO> outcome = new ArrayList<>();

    public TxInfoDTO() {

    }

    public TxInfoDTO(List<TransactionDTO> income, List<TransactionDTO> outcome) {
        this.income.addAll(income);
        this.outcome.addAll(outcome);
    }

}
