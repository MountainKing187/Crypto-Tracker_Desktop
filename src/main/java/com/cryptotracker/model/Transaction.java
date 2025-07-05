package com.cryptotracker.model;

public class Transaction {
    private String hash;
    private String asset;
    private long blockNumber;
    private String from;
    private String to;
    private String value;
    private long timestamp;

    public Transaction(String hash, String asset, long blockNumber,
                       String from, String to, String value, long timestamp) {
        this.hash = hash;
        this.asset = asset;
        this.blockNumber = blockNumber;
        this.from = from;
        this.to = to;
        this.value = value;
        this.timestamp = timestamp;
    }

    // Getters
    public String getHash() { return hash; }
    public String getAsset() { return asset; }
    public long getBlockNumber() { return blockNumber; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getValue() { return value; }
    public long getTimestamp() { return timestamp; }
}