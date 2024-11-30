package com.example.java_bank.service;

import com.example.java_bank.dto.BankResponse;
import com.example.java_bank.dto.EmailDetails;
import com.example.java_bank.dto.TransferRequest;
import com.example.java_bank.entity.User;
import com.example.java_bank.repository.UserRepository;
import com.example.java_bank.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferServiceImpl implements TransferService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;


    /** Transfer implementation */
    @Override
    public BankResponse transfer(TransferRequest transferRequest) {

        //Check existance of both account in this
        //Boolean isSourceAccountExists = userRepository.existsByAccountNumber(transferRequest.getSourceAccountNumber());
        Boolean isDestinationAccountExists = userRepository.existsByAccountNumber(transferRequest.getDestinationAccountNumber());

        if (!isDestinationAccountExists) { // return Account doesn't exist here
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOESNOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DOESNOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User sourceAccountUser = userRepository.findByAccountNumber(transferRequest.getSourceAccountNumber());

        /**
         * Uncomment while debugging to check if amount is null
         * **/
        /*if (transferRequest.getAmount() == null) {
            return BankResponse.builder()
                    .responseCode("INVALID AMOUNT CODE")
                    .responseMessage("Amount is required")
                    .accountInfo(null)
                    .build();
        }*/

        if(transferRequest.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.BALANCE_INSUFFICIENT_CODE)
                    .responseMessage(AccountUtils.BALANCE_INSUFFICIENT_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(transferRequest.getAmount()));
        String sourceAccountUsername = sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName();
        userRepository.save(sourceAccountUser);

        /**
         * alret for debit in account**/
        EmailDetails debitAlert = EmailDetails.builder()
                .recipient(sourceAccountUser.getEmail())
                .subject("ACCOUNT DEBIT NOTIFICATION")
                .messageBody("HEY THERE!!, YOUR ACCOUNT HAS BEEN DEBITED BY AMOUNT: \n" + transferRequest.getAmount() + "\n" + "YOUR CURRENT BALANCE IS: " + sourceAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(debitAlert);

        /** destination account **/
        User destinationAccount = userRepository.findByAccountNumber(transferRequest.getDestinationAccountNumber());
        destinationAccount.setAccountBalance(destinationAccount.getAccountBalance().add(transferRequest.getAmount()));
        //String recipientAccountUsername = destinationAccount.getFirstName() + " " + destinationAccount.getLastName();
        userRepository.save(destinationAccount);

                /**
                 * alret for debit in account **/
        EmailDetails creditAlert = EmailDetails.builder()
                .recipient(sourceAccountUser.getEmail())
                .subject("ACCOUNT CREDIT NOTIFICATION")
                .messageBody("HEY THERE!!, YOUR ACCOUNT HAS BEEN CREDITED BY AMOUNT: \n" + transferRequest.getAmount() + "AMOUNT IS SENT TO YOU FROM " + sourceAccountUsername + " " + "YOUR CURRENT BALANCE IS: " + sourceAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(debitAlert);
        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESS_MESSAGE)
                .accountInfo(null)
                .build();
    }
}