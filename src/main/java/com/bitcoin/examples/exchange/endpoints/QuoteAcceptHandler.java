package com.bitcoin.examples.exchange.endpoints;

import com.bitcoin.examples.exchange.beans.QuoteAcceptanceRequest;
import com.bitcoin.examples.exchange.beans.QuoteAcceptanceResponse;
import com.bitcoin.examples.exchange.exceptions.InvalidPaymentException;
import com.bitcoin.examples.exchange.services.QuotingService;
import com.bitcoin.examples.exchange.tools.HexStringHandler;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.params.MainNetParams;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class QuoteAcceptHandler {

    @RequestMapping(value = "/api/accept-quote", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<QuoteAcceptanceResponse> newAcceptQuoteRequest(@RequestBody QuoteAcceptanceRequest quoteAcceptanceRequest, @RequestHeader("X-API-Key") String apiKey) {

        // Parse the raw transaction
        Transaction transaction = new Transaction(MainNetParams.get(), HexStringHandler.hexStringToByteArray(quoteAcceptanceRequest.getRawTx()));

        QuoteAcceptanceResponse response = new QuoteAcceptanceResponse();

        try {
            QuotingService.acceptPayment(transaction, quoteAcceptanceRequest.getQuoteId());
            response.setTxid(transaction.getHashAsString());
            response.setStatus("ACCEPTED");
            response.setQuoteId(quoteAcceptanceRequest.getQuoteId());
        } catch (InvalidPaymentException e) {
            response.setMessage(e.getMessage());
            response.setStatus("REJECTED");
        }

        return new ResponseEntity<QuoteAcceptanceResponse>(response, HttpStatus.OK);
    }
}
