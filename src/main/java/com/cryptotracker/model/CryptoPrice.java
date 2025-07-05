package com.cryptotracker.model;

public class CryptoPrice {
    private String symbol;
    private double price;
    private long timestamp;

    public CryptoPrice(String symbol,double price, long timestamp){
        this.symbol = symbol;
        this.price = price;
        this.timestamp = timestamp;
    }

    // Getters y setters
    // ...

    public String getFormattedPrice() {
        return String.format("$%.2f", price);
    }

    public String getSymbol() { return this.symbol; }
    public double getPrice() { return this.price; }
    public long getTimestamp() { return this.timestamp; }
}