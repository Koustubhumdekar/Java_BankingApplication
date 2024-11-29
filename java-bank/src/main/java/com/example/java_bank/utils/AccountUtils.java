package com.example.java_bank.utils;

import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an existing account!!";
    public static final String ACCOUNT_CREATION_SUCCESS = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account created successfully";
    public static final String ACCOUNT_DOESNOT_EXISTS_CODE = "003";
    public static final String ACCOUNT_DOESNOT_EXISTS_MESSAGE = "This account doesn't exist!!";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_SUCCESS = "User account found...";
    public static final String ACCOUNT_CREDITED = "005";
    public static final String ACCOUNT_CREDITED_MESSAGE = "User Account has been credited";
    public static final String BALANCE_INSUFFICIENT_CODE = "006";
    public static final String BALANCE_INSUFFICIENT_MESSAGE = "Insufficient balance";
    public static final String ACCOUNT_DEBITED = "007";
    public static final String ACCOUNT_DEBITED_MESSAGE = "User account has been debited!!";

    public static String generateAccountNumber(){
        int min = 100000;
        int max = 999999;

        int randNumber = (int) Math.floor(Math.random() * (max - min +1)+min);
        Year currentYear = Year.now();
        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);

        /**Concatenating year and randomNumber to generate
         * an account number starting with current year
         * and any random number**/

        StringBuilder accountNumber = new StringBuilder();

        return accountNumber.append(year).append(randomNumber).toString();
    }
}
