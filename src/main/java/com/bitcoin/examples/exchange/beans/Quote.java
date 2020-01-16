package com.bitcoin.examples.exchange.beans;

import com.bitcoin.examples.exchange.enums.AssetType;
import com.bitcoin.examples.exchange.enums.QuoteStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bitcoinj.core.Address;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@ToString
public class Quote implements Serializable {
    private QuoteStatus status;
    private UUID quoteId;
    private Date expiry;
    private Address address;
    private String addressString;
    private BigDecimal amount;
    private String fiatCounter;
    private BigDecimal fiatAmount;
    private AssetType assetType;
    private String accountNumber;
    private BigDecimal rate;

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
        long sleepTime = expiry.getTime() - new Date().getTime();
        new Thread(new Runnable() {
            public void run(){
                try {
                    Thread.sleep(sleepTime);
                    if (status.equals(QuoteStatus.OPEN)) {
                        status = QuoteStatus.EXPIRED;
                        System.out.println("Quote " + quoteId + " have expired");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
