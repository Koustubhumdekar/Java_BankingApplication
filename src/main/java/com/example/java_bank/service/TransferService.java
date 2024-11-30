package com.example.java_bank.service;

import com.example.java_bank.dto.BankResponse;
import com.example.java_bank.dto.TransferRequest;

public interface TransferService {

    BankResponse transfer(TransferRequest transferRequest);
}
