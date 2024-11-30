package com.example.java_bank.controller;

import com.example.java_bank.dto.BankResponse;
import com.example.java_bank.dto.TransferRequest;
import com.example.java_bank.service.TransferService;
import com.example.java_bank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    @Autowired
    TransferService transferService;

    @PostMapping("/transfer")
    public BankResponse transfer(@RequestBody TransferRequest transferRequest){

        return transferService.transfer(transferRequest);
    }

}
