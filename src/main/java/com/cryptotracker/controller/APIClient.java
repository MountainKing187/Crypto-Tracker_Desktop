package com.cryptotracker.controller;

import com.cryptotracker.model.CryptoPrice;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class APIClient {
    private static final String API_URL = "http://localhost:5000/crypto-prices";

    public ArrayList<CryptoPrice> fetchPrices() throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return parsePrices(response.toString());
        }
    }

    private ArrayList<CryptoPrice> parsePrices(String json) {
        // Implementación con org.json
        JSONArray array = new JSONArray(json);
        ArrayList<CryptoPrice> prices = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            prices.add(new CryptoPrice(
                    obj.getString("symbol"),
                    obj.getDouble("price"),
                    obj.getLong("timestamp")
            ));
        }
        return prices;
    }
}