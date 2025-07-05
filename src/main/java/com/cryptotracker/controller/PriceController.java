package com.cryptotracker.controller;

import com.cryptotracker.model.CryptoPrice;
import com.cryptotracker.view.MainFrame;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PriceController {
    private final APIClient apiClient;
    private final MainFrame view;
    private final List<String> trackedSymbols;

    public PriceController(MainFrame view) {
        this.view = view;
        this.apiClient = new APIClient();
        this.trackedSymbols = new ArrayList<>(Arrays.asList(
                "bitcoin", "ethereum", "dogecoin", "litecoin"
        ));
    }

    // Método modificado para ser llamado por el timer
    public void startPriceUpdates() {
        try {
            List<CryptoPrice> prices = apiClient.getLatestPrices(trackedSymbols);
            view.updatePrices(prices);
        } catch (IOException ex) {
            view.showError("Error fetching data: " + ex.getMessage());
        }
    }
}