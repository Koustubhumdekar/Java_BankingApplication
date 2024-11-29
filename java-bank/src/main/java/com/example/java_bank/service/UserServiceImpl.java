package com.example.java_bank.service;

import com.example.java_bank.dto.*;
import com.example.java_bank.entity.User;
import com.example.java_bank.repository.UserRepository;
import com.example.java_bank.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    /**
     * Saves a new user into database
     **/
    @Override
    public BankResponse createAccount(UserRequest userRequest) {

        if (userRepository.existsByEmail(userRequest.getEmail())) {

            BankResponse response = BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();

            return response;
        }

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .email(userRequest.getEmail())
                .accountBalance(BigDecimal.ZERO)
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .dateOfBirth(userRequest.getDateOfBirth())
                .status("ACTIVE") //setting to default, needs another utils/helper class to make status dynamic
                .build();

        User savedUser = userRepository.save(newUser);

        /**
         * send alert via emails
         **/
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("HEY THERE!!, YOUR ACCOUNT HAS BEEN SUCCESSFULLY CREATED...\n ACCOUNT DETAILS:\n" +
                        "ACCOUNT NAME: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + "\n ACCOUNT NUMBER:" + savedUser.getAccountNumber())
                .build();

        emailService.sendEmailAlert(emailDetails); //alert logic till here


        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                        .build())
                .build();

    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {

        /**
         * to check if account exists
         * checked by using account number**/

        Boolean isAccountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());

        /**
         * if account doesn't exist : return not exist responses **/
        if (!isAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOESNOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DOESNOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        /**
         * To find account using account number **/
        User accountFound = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(accountFound.getAccountBalance())
                        .accountNumber(accountFound.getAccountNumber())
                        .accountName(accountFound.getFirstName() + " " + accountFound.getLastName() + " " + accountFound.getOtherName())
                        .build())
                .build();
    }


    @Override
    public String nameEnquiry(EnquiryRequest request) {

        Boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());

        if (!isAccountExists) {
            return AccountUtils.ACCOUNT_DOESNOT_EXISTS_MESSAGE;
        }

        //if account exists return user details
        User found = userRepository.findByAccountNumber(request.getAccountNumber());

        return found.getFirstName() + " " + found.getLastName() + " " + found.getOtherName();//user details if account number exists
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest creditDebitRequest) {

        /**
         * if account doesn't exist : return not exist responses **/
        Boolean isAccountExists = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());
        if (!isAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOESNOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DOESNOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userCredit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());

        //add onto the existing amount by adding amount to current account balance
        userCredit.setAccountBalance(userCredit.getAccountBalance().add(creditDebitRequest.getAmount()));
        userRepository.save(userCredit);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userCredit.getFirstName() + " " + userCredit.getLastName() + " " + userCredit.getOtherName())
                        .accountBalance(userCredit.getAccountBalance())
                        .accountNumber(creditDebitRequest.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest debitRequest) {

        /**
         * if account doesn't exist : return not exist responses **/
        Boolean isAccountExists = userRepository.existsByAccountNumber(debitRequest.getAccountNumber());
        if (!isAccountExists) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOESNOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DOESNOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userDebit = userRepository.findByAccountNumber(debitRequest.getAccountNumber());
        /**
         * AccountBalance & Amount both are BigDecimal.
         * Convert them to BigInteger for checking balance & debitAmount
         */
        BigInteger balance = userDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = debitRequest.getAmount().toBigInteger();

        /**
         * Check for account balance insufficient,
         * convert balance and debitAmount using intValue() **/
        if (balance.intValue() < debitAmount.intValue()){

            return BankResponse.builder()
                    .responseCode(AccountUtils.BALANCE_INSUFFICIENT_CODE)
                    .responseMessage(AccountUtils.BALANCE_INSUFFICIENT_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        else {
            userDebit.setAccountBalance(userDebit.getAccountBalance().subtract(debitRequest.getAmount()));
            userRepository.save(userDebit);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(debitRequest.getAccountNumber())
                            .accountName(userDebit.getFirstName() + " " + userDebit.getLastName() + " " + userDebit.getOtherName())
                            .accountBalance(userDebit.getAccountBalance())
                            .build())
                    .build();
        }
    }
}
