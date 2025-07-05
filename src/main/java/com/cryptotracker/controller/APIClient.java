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

    // Obtener bloques recientes
    public List<Block> getRecentBlocks(int limit) throws IOException {
        String endpoint = baseUrl + "/api/blocks/recent";
        JSONArray response = fetchJsonArray(endpoint);
        List<Block> blocks = new ArrayList<>();

        for (int i = 0; i < Math.min(limit, response.length()); i++) {
            blocks.add(parseBlock(response.getJSONObject(i)));
        }

        return blocks;
    }

    // Obtener transacciones de un bloque
    public List<Transaction> getBlockTransactions(long blockNumber) throws IOException {
        String endpoint = baseUrl + "/api/transactions/" + blockNumber;
        JSONArray response = fetchJsonArray(endpoint);
        List<Transaction> transactions = new ArrayList<>();

        for (int i = 0; i < response.length(); i++) {
            transactions.add(parseTransaction(response.getJSONObject(i)));
        }

        return transactions;
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

    // Parsers para los diferentes modelos
    private CryptoPrice parseCryptoPrice(JSONObject obj) {
        String symbol = obj.getString("symbol");
        double price = obj.getDouble("price");

        // Parsear fecha ISO 8601
        String dateStr = obj.getJSONObject("timestamp")
                .getString("$date");

        Instant instant = Instant.from(DATE_FORMATTER.parse(dateStr));
        long timestamp = instant.getEpochSecond();

        return new CryptoPrice(symbol, price, timestamp);
    }

    private Block parseBlock(JSONObject obj) {
        long blockNumber = obj.getLong("blockNumber");
        String hash = obj.getString("hash");
        String miner = obj.getString("miner");
        long timestamp = obj.getLong("timestamp");

        // Parsear transacciones
        JSONArray txArray = obj.getJSONArray("transactions");
        List<String> transactions = new ArrayList<>();
        for (int i = 0; i < txArray.length(); i++) {
            transactions.add(txArray.getString(i));
        }

        return new Block(blockNumber, hash, miner, timestamp, transactions);
    }

    private Transaction parseTransaction(JSONObject obj) {
        String hash = obj.getString("hash");
        String asset = obj.getString("asset");
        long blockNumber = obj.getLong("blockNumber");
        String from = obj.getString("from");
        String to = obj.getString("to");
        String value = obj.getString("value");
        long timestamp = obj.getLong("timestamp");

        return new Transaction(hash, asset, blockNumber, from, to, value, timestamp);
    }
}