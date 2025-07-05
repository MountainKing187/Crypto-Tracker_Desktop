package com.cryptotracker.controller;

import com.cryptotracker.model.CryptoPrice;
import com.cryptotracker.view.MainFrame;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;

public class PriceController {
    private final APIClient apiClient;
    private final MainFrame view;
    private Timer timer;

    public PriceController(MainFrame view) {
        this.view = view;
        this.apiClient = new APIClient();
    }

    public void startPriceUpdates(int intervalSeconds) {
        timer = new Timer(intervalSeconds * 1000, e -> {
            try {
                ArrayList<CryptoPrice> prices = apiClient.fetchPrices();
                view.updatePrices(prices);
            } catch (IOException ex) {
                view.showError("Error fetching data: " + ex.getMessage());
            }
        });
        timer.start();
    }
}
