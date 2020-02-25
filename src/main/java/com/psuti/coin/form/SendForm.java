package com.psuti.coin.form;

import lombok.Data;

@Data
public class SendForm {
    private String publicKey;
    private Float amount;
}
