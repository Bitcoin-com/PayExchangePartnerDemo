package com.bitcoin.examples.exchange.endpoints;

import com.bitcoin.examples.exchange.beans.QuoteRequest;
import com.bitcoin.examples.exchange.beans.QuoteResponse;
import com.bitcoin.examples.exchange.services.QuotingService;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@Controller
public class QuoteRequestHandler {

    @RequestMapping(value = "/api/get-quote", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<QuoteResponse> newQuoteRequest(@RequestBody QuoteRequest paymentRequestCall, @RequestHeader("X-API-Key") String apiKey){

        // Check API-key, if you want to
        /*
         * API authentication stuff*
         */

        // Get the destination account so we know where to send the fiat money
        // In this demo, we will only accept the type EXCHANGE since we are going to credit the user's account directly
        if (!paymentRequestCall.getDestAccount().getType().equals("EXCHANGE")) {
            return new ResponseEntity<QuoteResponse>(new QuoteResponse(), HttpStatus.BAD_REQUEST);
        }

        UUID accountId = UUID.fromString(paymentRequestCall.getDestAccount().getAccountNumber());

        // Build the quote response object
        QuoteResponse response = new QuoteResponse(QuotingService.createQuote(paymentRequestCall.getCurrency(), paymentRequestCall.getFiatAmount(), accountId));

        return new ResponseEntity<QuoteResponse>(response, HttpStatus.OK);
    }

}
