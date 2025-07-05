package com.cryptotracker.view;

import com.cryptotracker.controller.PriceController;
import com.cryptotracker.model.CryptoPrice;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MainFrame extends JFrame {
    private PricePanel pricePanel;
    private ChartPanel chartPanel;

    public MainFrame() {
        setTitle("Crypto Price Monitor");
        setSize(1000, 600);
        setLayout(new BorderLayout());

        pricePanel = new PricePanel();
        chartPanel = new ChartPanel();

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                pricePanel,
                chartPanel
        );
        add(splitPane, BorderLayout.CENTER);

        // Iniciar controlador
        new PriceController(this).startPriceUpdates(30);
    }

    public void updatePrices(ArrayList<CryptoPrice> prices) {
        pricePanel.updateTable(prices);
        chartPanel.updateChart(prices);
    }

    public void showError(String s) {
    }
}