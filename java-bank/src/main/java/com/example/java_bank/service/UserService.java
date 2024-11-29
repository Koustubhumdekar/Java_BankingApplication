package com.example.java_bank.service;

import com.example.java_bank.dto.BankResponse;
import com.example.java_bank.dto.CreditDebitRequest;
import com.example.java_bank.dto.EnquiryRequest;
import com.example.java_bank.dto.UserRequest;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
    String nameEnquiry(EnquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest creditDebitRequest);
    BankResponse debitAccount(CreditDebitRequest debitRequest);
}
