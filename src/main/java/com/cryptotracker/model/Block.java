package com.cryptotracker.model;

import java.util.List;

public class Block {
    private long blockNumber;
    private String hash;
    private String miner;
    private long timestamp;
    private List<String> transactions;

    public Block(long blockNumber, String hash, String miner,
                 long timestamp, List<String> transactions) {
        this.blockNumber = blockNumber;
        this.hash = hash;
        this.miner = miner;
        this.timestamp = timestamp;
        this.transactions = transactions;
    }

    // Getters
    public long getBlockNumber() { return blockNumber; }
    public String getHash() { return hash; }
    public String getMiner() { return miner; }
    public long getTimestamp() { return timestamp; }
    public List<String> getTransactions() { return transactions; }
}