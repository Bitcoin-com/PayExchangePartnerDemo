package com.bitcoin.examples.exchange.services;

import com.bitcoin.examples.exchange.exceptions.BitcoinRpcException;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.params.MainNetParams;

import java.util.Random;

public class BitcoinWalletService {

    // Hard coded address since this i just a demo
    private static final Address hotwalletAddress = Address.fromBase58(MainNetParams.get(), "1NU99mbAzvvXxg2UxcDhvNEQsRRZxEYz7M");

    public static String broadcastTransaction(Transaction tx) throws BitcoinRpcException {

        Random rnd = new Random();
        int randomNumber = Math.abs(rnd.nextInt(100));

        if (randomNumber < 50) {
            throw new BitcoinRpcException("Transaction not accepted by bitcoin node");
        }

        // Fake broadcaster. In real life, you send the transaction to a bitcoin node via RPC

        return tx.getHashAsString();
    }

    public static Address getNewHotwalletAddress(String label) {
        // A label is really useful for every address, but not strictly needed
        return hotwalletAddress;
    }

}
