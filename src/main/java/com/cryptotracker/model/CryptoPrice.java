package com.cryptotracker.model;

import java.util.Date;

public class CryptoPrice {
    private String symbol;
    private double price;
    private long timestamp; // Segundos desde epoch

    public CryptoPrice(String symbol, double price, long timestamp) {
        this.symbol = symbol;
        this.price = price;
        this.timestamp = timestamp;
    }

    // Nuevo metodo para convertir timestamp a Date
    public Date getTimestampAsDate() {
        return new Date(timestamp * 1000);
    }

    public long getMinuteTimestamp() {
        return timestamp - (timestamp % 60);
    }

    // Getters
    public String getSymbol() { return symbol; }
    public double getPrice() { return price; }
    public long getTimestamp() { return timestamp; }
}