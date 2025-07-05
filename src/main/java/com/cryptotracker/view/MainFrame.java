package com.cryptotracker.view;

import com.cryptotracker.controller.PriceController;
import com.cryptotracker.model.CryptoPrice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class MainFrame extends JFrame {
    private final PricePanel pricePanel;
    private final CryptoChartPanel cryptoChartPanel;
    private PriceController priceController;
    private Timer timer;
    private String selectedCrypto = "ethereum"; // Moneda por defecto

    public MainFrame() {
        setTitle("Crypto Price Monitor");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear componentes principales
        pricePanel = new PricePanel();
        cryptoChartPanel = new CryptoChartPanel();

        // Configurar layout principal
        setLayout(new BorderLayout(10, 10));

        // Panel de selección de moneda
        JPanel selectorPanel = createSelectorPanel();
        add(selectorPanel, BorderLayout.NORTH);

        // Panel de contenido dividido
        JSplitPane contentPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(pricePanel),
                new JScrollPane(cryptoChartPanel)
        );
        contentPane.setResizeWeight(0.4);
        contentPane.setDividerLocation(0.4);
        add(contentPane, BorderLayout.CENTER);

        // Panel de control
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);

        // Inicializar controlador
        priceController = new PriceController(this);

        // Manejar cierre de ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopMonitoring();
            }
        });
    }

    private JPanel createSelectorPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Selección de Moneda"));

        // Botones para las principales criptomonedas
        String[] cryptos = {"bitcoin", "ethereum", "dogecoin"};
        for (String crypto : cryptos) {
            JButton btn = new JButton(crypto.toUpperCase());
            btn.setPreferredSize(new Dimension(100, 30));
            btn.addActionListener(e -> {
                selectedCrypto = crypto;
                updateChartWithSelectedCrypto();
                cryptoChartPanel.setChartTitle("Precio de " + crypto.toUpperCase());
            });
            panel.add(btn);
        }

        // Selector desplegable para más monedas
        JComboBox<String> cryptoCombo = new JComboBox<>(new String[]{
                "litecoin", "ripple","bitcoin-cash", "cardano", "polkadot", "solana", "tether"
        });
        cryptoCombo.setEditable(false);
        cryptoCombo.addActionListener(e -> {
            selectedCrypto = (String) cryptoCombo.getSelectedItem();
            updateChartWithSelectedCrypto();
            cryptoChartPanel.setChartTitle("Precio de " + selectedCrypto.toUpperCase());
        });

        panel.add(new JLabel("Otras monedas:"));
        panel.add(cryptoCombo);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        // Botones de control
        JButton startButton = new JButton("Iniciar Monitoreo");
        JButton stopButton = new JButton("Detener Monitoreo");
        JButton refreshButton = new JButton("Actualizar Ahora");

        startButton.addActionListener(e -> startMonitoring());
        stopButton.addActionListener(e -> stopMonitoring());
        refreshButton.addActionListener(e -> refreshData());

        panel.add(startButton);
        panel.add(stopButton);
        panel.add(refreshButton);

        return panel;
    }

    public void startMonitoring() {
        if (timer != null && timer.isRunning()) {
            return;
        }

        // Iniciar actualizaciones cada 30 segundos
        timer = new Timer(30000, e -> refreshData());
        timer.setInitialDelay(0);
        timer.start();

        // Primera actualización inmediata
        refreshData();

        showStatus("Monitoreo activo - Actualizando datos...");
    }

    public void stopMonitoring() {
        if (timer != null) {
            timer.stop();
        }
        showStatus("Monitoreo detenido");
    }

    public void refreshData() {
        try {
            List<CryptoPrice> prices = priceController.getLatestPrices();
            updatePrices(prices);
            showStatus("Datos actualizados: " + new java.util.Date());
        } catch (Exception ex) {
            showError("Error en actualización: " + ex.getMessage());
        }
    }

    private void updateChartWithSelectedCrypto() {
        if (pricePanel.getLastPrices() != null) {
            cryptoChartPanel.updateChartForCrypto(
                    pricePanel.getLastPrices(),
                    selectedCrypto
            );
        }
    }

    public void updatePrices(List<CryptoPrice> prices) {
        SwingUtilities.invokeLater(() -> {
            pricePanel.updateTable(prices);
            cryptoChartPanel.updateChartForCrypto(prices, selectedCrypto);
        });
    }

    public void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    this,
                    message,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        });
    }

    public void showStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            setTitle("Crypto Price Monitor - " + message);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}