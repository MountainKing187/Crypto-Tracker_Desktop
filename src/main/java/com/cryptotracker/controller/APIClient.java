package com.cryptotracker.controller;

import com.cryptotracker.model.Block;
import com.cryptotracker.model.CryptoPrice;
import com.cryptotracker.model.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class APIClient {
    private final String baseUrl;
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ISO_INSTANT;

    public APIClient() {
        this.baseUrl = loadBaseUrlFromConfig();
    }

    private String loadBaseUrlFromConfig() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new FileNotFoundException("config.properties not found");
            }

            prop.load(input);
            return prop.getProperty("api.base.url");
        } catch (IOException ex) {
            throw new RuntimeException("Error loading configuration", ex);
        }
    }

    // Obtener el último precio de una criptomoneda
    public CryptoPrice getLatestPrice(String symbol) throws IOException {
        String endpoint = baseUrl + "/api/price/" + symbol + "?hours=1";
        JSONArray response = fetchJsonArray(endpoint);

        if (response.length() == 0) {
            return null;
        }

        // Tomar el elemento más reciente
        JSONObject latest = response.getJSONObject(response.length() - 1);
        return parseCryptoPrice(latest);
    }

    // Obtener precios de múltiples símbolos
    public List<CryptoPrice> getLatestPrices(List<String> symbols) throws IOException {
        List<CryptoPrice> prices = new ArrayList<>();
        for (String symbol : symbols) {
            CryptoPrice price = getLatestPrice(symbol);
            if (price != null) {
                prices.add(price);
            }
        }
        return prices;
    }

    private JSONArray fetchJsonArray(String endpoint) throws IOException {
        HttpURLConnection conn = createConnection(endpoint);
        return parseResponse(conn);
    }

    private HttpURLConnection createConnection(String endpoint) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        return conn;
    }

    private JSONArray parseResponse(HttpURLConnection conn) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return new JSONArray(response.toString());
        } finally {
            conn.disconnect();
        }
    }

    // Metodo para obtener datos históricos
    public List<CryptoPrice> getHistoricalPrices(String symbol, int hours) throws IOException {
        String endpoint = baseUrl + "/api/price/" + symbol + "?hours=" + hours;
        JSONArray response = fetchJsonArray(endpoint);
        List<CryptoPrice> prices = new ArrayList<>();

        for (int i = 0; i < response.length(); i++) {
            JSONObject obj = response.getJSONObject(i);
            prices.add(parseCryptoPrice(obj));
        }

        return prices;
    }

    // Metodo mejorado para parsear precios (maneja diferentes formatos)
    private CryptoPrice parseCryptoPrice(JSONObject obj) {
        String symbol = obj.getString("symbol");
        double price = obj.getDouble("price");
        long timestamp;

        // Manejar diferentes formatos de timestamp
        if (obj.has("timestamp")) {
            if (obj.get("timestamp") instanceof JSONObject) {
                JSONObject timestampObj = obj.getJSONObject("timestamp");
                String dateStr = timestampObj.getString("$date");

                // Parsear fecha y redondear a minutos
                Instant instant = Instant.from(DATE_FORMATTER.parse(dateStr));
                timestamp = instant.getEpochSecond();
                timestamp = timestamp - (timestamp % 60); // Redondear al minuto completo
            } else {
                // Formato directo (long) - redondear a minutos
                timestamp = obj.getLong("timestamp");
                timestamp = timestamp - (timestamp % 60);
            }
        } else {
            throw new IllegalArgumentException("Missing timestamp in price data");
        }

        return new CryptoPrice(symbol, price, timestamp);
    }
}