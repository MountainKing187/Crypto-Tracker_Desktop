package com.cryptotracker.model;

public class CryptoPrice {
    private String symbol;
    private double price;
    private long timestamp; // Segundos desde epoch

    public CryptoPrice(String symbol, double price, long timestamp) {
        this.symbol = symbol;
        this.price = price;
        this.timestamp = timestamp;
    }

    // Getters
    public String getSymbol() { return symbol; }
    public double getPrice() { return price; }
    public long getTimestamp() { return timestamp; }

    // Formateador para la UI
    public String getFormattedPrice() {
        return String.format("$%.4f", price);
    }
}