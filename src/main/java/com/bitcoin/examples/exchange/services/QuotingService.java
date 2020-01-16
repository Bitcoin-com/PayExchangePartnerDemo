package com.bitcoin.examples.exchange.services;

import com.bitcoin.examples.exchange.beans.Quote;
import com.bitcoin.examples.exchange.enums.AssetType;
import com.bitcoin.examples.exchange.enums.QuoteStatus;
import com.bitcoin.examples.exchange.exceptions.BitcoinRpcException;
import com.bitcoin.examples.exchange.exceptions.InvalidPaymentException;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.SerializationUtils;
import org.bitcoinj.core.*;
import org.bitcoinj.params.MainNetParams;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class QuotingService {

    // Hard coded rates to make this example easy
    private static BigDecimal rateUSD = new BigDecimal(200);
    private static BigDecimal rateEUR = new BigDecimal(180);
    private static BigDecimal rateJPY = new BigDecimal(22000);

    private static HashMap<String, BigDecimal> ratesTable = new HashMap<String, BigDecimal>();
    private static HashMap<UUID, Quote> activeQuotes = new HashMap<UUID, Quote>();



    @PostConstruct
    private void init() {
        System.out.println("Starting quoting service");

        ratesTable.put("USD", rateUSD);
        ratesTable.put("EUR", rateEUR);
        ratesTable.put("JPY", rateJPY);
    }

    @PreDestroy
    private void destroy() {
        System.out.println("Stopping quoting service");
    }

    public static Quote createQuote(String currency, BigDecimal fiatAmount, String accountNumber) {

        Quote quote = new Quote();
        quote.setAccountNumber(accountNumber);

        // Generate a unique quote id
        quote.setQuoteId(UUID.randomUUID());

        // This should throw an exception if the system could not offer a quote.
        // For example, if the customer would request 1,000,000 USD quote, it should throw an error saying it's too large

        // Get a new address for this quote
        Address depositAddress = BitcoinWalletService.getNewHotwalletAddress(quote.getQuoteId().toString());

        // Get the current market rate, from spot price or from a bot. Make this how advanced you want.
        BigDecimal rate = ratesTable.get(currency);
        System.out.println("We are willing to offer the rate " + rate + " " + currency + "/BCH");
        BigDecimal bchAmount = fiatAmount.divide(rate, 8, RoundingMode.UP); // 8 decimals
        System.out.println("We want to receive a transaction with " + bchAmount + " to " + depositAddress + " for " + fiatAmount + " " + currency);
        quote.setAmount(bchAmount);
        quote.setAddress(depositAddress);
        quote.setFiatCounter(currency);
        quote.setFiatAmount(fiatAmount);
        quote.setRate(rate);
        quote.setAssetType(AssetType.FIAT);

        // Set an expiry time
        Date timeNow = new Date();
        timeNow.setTime(timeNow.getTime() + 600000); // Add 10 minutes from now
        quote.setExpiry(timeNow);

        quote.setStatus(QuoteStatus.OPEN);

        // Add to active quotes
        activeQuotes.put(quote.getQuoteId(), quote);

        return quote;
    }

    public static void acceptPayment(Transaction tx, UUID quoteId) throws InvalidPaymentException {
        Quote quote = activeQuotes.get(quoteId);
        System.out.println(quote);

        // The quote should only be in the state of ACCEPTED
        if (!quote.getStatus().equals(QuoteStatus.OPEN)) {
            throw new InvalidPaymentException("Quote not open, currently in state " + quote.getStatus());
        }

        // Check if the quote is expired, in case we had a glitch in the system and the Quote wasn't changed to EXPIRED
        if (!quote.getExpiry().after(new Date())) {
            throw new InvalidPaymentException("Quote has expired");
        }

        HashMap<Address, Coin> addressNetAmount = new HashMap<Address, Coin>();

        for (TransactionOutput txOutput : tx.getOutputs()) {
            System.out.println(txOutput);
            if (addressNetAmount.containsKey(getAddressFromOutput(txOutput))) {
                Coin amount = addressNetAmount.get(getAddressFromOutput(txOutput));
                amount = amount.add(txOutput.getValue());
                addressNetAmount.put(getAddressFromOutput(txOutput), amount);
            } else if (!addressNetAmount.containsKey(getAddressFromOutput(txOutput))) {
                addressNetAmount.put(getAddressFromOutput(txOutput), txOutput.getValue());
            }
        }

        if (!addressNetAmount.containsKey(quote.getAddress())) {
            throw new InvalidPaymentException("Transaction does not pay to address " + quote.getAddress());
        } else {
            if (addressNetAmount.get(quote.getAddress()).isGreaterThan(Coin.valueOf(quote.getAmount().movePointRight(8).longValue())) ) {
                // If a payment was sent to the address, but it's underpaid
                throw new InvalidPaymentException("Transaction is underpaid");
            } else if (addressNetAmount.get(quote.getAddress()).isGreaterThan(Coin.valueOf(quote.getAmount().movePointRight(8).longValue())) ) {
                // If a payment was sent to the address, but it's more than we want
                throw new InvalidPaymentException("Transaction is overpaid");
            }
        }

        // Broadcast transaction
        try {
            BitcoinWalletService.broadcastTransaction(tx);
            quote.setStatus(QuoteStatus.PAID);
        } catch (BitcoinRpcException e) {
            throw new InvalidPaymentException(e.getMessage());
        }

        // Put code for updating database here if the transaction was accepted by the node
        // *Database stuff*
        AccountService.creditAccount(quote.getAccountNumber(), quote.getFiatCounter(), quote.getFiatAmount());
    }

    public static HashMap<UUID, Quote> getActiveQuotes() {
        HashMap<UUID, Quote> quotes = new HashMap<UUID, Quote>();

        for (UUID key : activeQuotes.keySet()) {
            Quote quote = (Quote) SerializationUtils.clone(activeQuotes.get(key));
            quote.setAddressString(quote.getAddress().toString());
            quote.setAddress(null);
            quotes.put(key, quote);
        }
        return quotes;
    }

    private static Address getAddressFromOutput(TransactionOutput txOutput) {
        Address addr = txOutput.getAddressFromP2PKHScript(MainNetParams.get());
        if (addr == null) {
            addr = txOutput.getAddressFromP2SH(MainNetParams.get());
        }
        return addr;
    }
}
