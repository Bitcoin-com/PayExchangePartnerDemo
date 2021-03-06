package com.bitcoin.examples.exchange.services;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

@Component
public class AccountService {

    private static HashMap<String, HashMap<String, BigDecimal>> accountsBchBalance = new HashMap<String, HashMap<String, BigDecimal>>();

    @PostConstruct
    private void init() {
        System.out.println("Starting account service");
    }

    @PreDestroy
    private void destroy() {
        System.out.println("Stopping account service");
    }

    public static BigDecimal creditAccount(String accountNumber, String currency, BigDecimal amount) {
        if (accountsBchBalance.containsKey(accountNumber) && accountsBchBalance.get(accountNumber).containsKey(currency)) {
            HashMap<String, BigDecimal> accountBalances = accountsBchBalance.get(accountNumber);
            BigDecimal currentBalance = accountBalances.get(currency).add(amount);
            currentBalance = currentBalance.add(amount);
            accountBalances.put(currency, currentBalance);
            accountsBchBalance.put(accountNumber, accountBalances);
            return currentBalance;
        } else if (accountsBchBalance.containsKey(accountNumber) && !accountsBchBalance.get(accountNumber).containsKey(currency)) {
            HashMap<String, BigDecimal> accountBalances = accountsBchBalance.get(accountNumber);
            accountBalances.put(currency, amount);
            accountsBchBalance.put(accountNumber, accountBalances);
            return  amount;
        } else {
            HashMap<String, BigDecimal> accountBalances = new HashMap<String, BigDecimal>();
            accountBalances.put(currency, amount);
            accountsBchBalance.put(accountNumber, accountBalances);
            return amount;
        }
    }

    public static HashMap<String, HashMap<String, BigDecimal>> getAllAccounts() {
        return accountsBchBalance;
    }

}
