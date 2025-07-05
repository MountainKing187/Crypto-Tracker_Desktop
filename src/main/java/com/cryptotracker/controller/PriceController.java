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

    // Obtener datos históricos
    public List<CryptoPrice> getHistoricalPrices(String symbol, int hours) throws IOException {
        return apiClient.getHistoricalPrices(symbol, hours);
    }

    // Obtener precios actuales
    public List<CryptoPrice> getLatestPrices() throws IOException {
        List<CryptoPrice> prices = new ArrayList<>();
        for (String symbol : trackedSymbols) {
            // Obtener el precio más reciente (última hora)
            List<CryptoPrice> recentPrices = apiClient.getHistoricalPrices(symbol, 1);
            if (!recentPrices.isEmpty()) {
                prices.add(recentPrices.get(recentPrices.size() - 1));
            }
        }
        return prices;
    }
}