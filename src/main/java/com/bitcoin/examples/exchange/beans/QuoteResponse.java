package com.bitcoin.examples.exchange.beans;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.UUID;

@Getter
@Setter
public class QuoteResponse {

    private String status;
    private UUID quoteId;
    private String expiry;
    private boolean directBroadcast;
    private String address;
    private long amount;
    private BigDecimal rate;
    private String payoutAsset;
    private String payoutAssetType;
    private String payoutAssetId;
    private BigDecimal payoutAmount;
    private String outputAccount;
    private String routingNumber;

    public QuoteResponse(Quote quote) {
        this.status = quote.getStatus().toString();
        this.quoteId = quote.getQuoteId();
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(quote.getExpiry());
        this.expiry = nowAsISO;
        this.directBroadcast = true;
        this.address = quote.getAddress().toString();
        this.amount = quote.getAmount().movePointRight(8).longValue();
        this.payoutAsset = quote.getFiatCounter();
        this.payoutAmount = quote.getFiatAmount();
        this.outputAccount = quote.getAccountNumber();
        this.payoutAssetType = quote.getAssetType().toString();
        this.rate = quote.getRate();
    }

    // Default object
    public QuoteResponse() {
        this.status = "DENIED";
    }

    public QuoteResponse(String status, UUID quoteId, String expiry, boolean directBroadcast, String address, long amount) {
        this.status = status;
        this.quoteId = quoteId;
        this.expiry = expiry;
        this.directBroadcast = directBroadcast;
    }


}
