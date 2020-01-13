package com.bitcoin.examples.exchange.beans;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class QuoteAcceptanceResponse {
    private String status;
    private UUID quoteId;
    private String message;
    private String txid;
}
