package com.bitcoin.examples.exchange.endpoints;

import com.bitcoin.examples.exchange.beans.Quote;
import com.bitcoin.examples.exchange.services.AccountService;
import com.bitcoin.examples.exchange.services.QuotingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

@Controller
public class DebugEndpoints {

    @RequestMapping(value = "/api/get-quotes", method = RequestMethod.GET)
    public ResponseEntity<HashMap<UUID, Quote>> quoteListRequest(){

        System.out.println("List all active quotes");
        try {
            HashMap<UUID, Quote> activeQuotes = QuotingService.getActiveQuotes();
            return new ResponseEntity<HashMap<UUID, Quote>>(activeQuotes, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<HashMap<UUID, Quote>>(new HashMap<UUID, Quote>(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/api/get-accounts", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String, HashMap<String, BigDecimal>>> accountListRequest(){

        System.out.println("List all active quotes");
        HashMap<String, HashMap<String, BigDecimal>> allAccounts = AccountService.getAllAccounts();

        return new ResponseEntity<HashMap<String, HashMap<String, BigDecimal>>>(allAccounts, HttpStatus.OK);
    }
}
