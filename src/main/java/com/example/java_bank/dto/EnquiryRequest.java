package com.example.java_bank.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnquiryRequest {
    private String accountNumber;
}
