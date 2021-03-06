package com.bitcoin.examples.exchange.beans;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@ToString
public class QuoteRequest {

    private String inputAsset;
    private BigDecimal outputAmount;
    private String outputAsset;
    private DestinationAccount destAccount;

    @Getter
    @Setter
    @ToString
    public class DestinationAccount {
        private String type;
        private String accountHolder;
        private String country;
        private Map<String, String> accountHolderAddress;
        private String accountNumber;
        private String routingNumber;
    }
}
