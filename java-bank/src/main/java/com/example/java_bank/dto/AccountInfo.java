package com.example.java_bank.dto;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfo {

    private String accountName;
    private BigDecimal accountBalance;
    private String accountNumber;

}
