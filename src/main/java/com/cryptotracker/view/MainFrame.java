package com.cryptotracker.view;

import com.cryptotracker.controller.PriceController;
import com.cryptotracker.model.CryptoPrice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class MainFrame extends JFrame {
    private final PricePanel pricePanel;
    private final CryptoChartPanel cryptoChartPanel;
    private PriceController priceController;
    private Timer timer;

    public MainFrame() {
        setTitle("Crypto Price Monitor");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear componentes principales
        pricePanel = new PricePanel();
        cryptoChartPanel = new CryptoChartPanel();

        // Configurar layout
        setLayout(new BorderLayout());

        // Panel dividido para tabla y gráfico
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(pricePanel),
                new JScrollPane(cryptoChartPanel)
        );
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(0.5);

        add(splitPane, BorderLayout.CENTER);

        // Panel inferior para controles
        JPanel controlPanel = new JPanel();
        JButton startButton = new JButton("Iniciar Monitoreo");
        JButton stopButton = new JButton("Detener Monitoreo");

        startButton.addActionListener(e -> startMonitoring());
        stopButton.addActionListener(e -> stopMonitoring());

        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        add(controlPanel, BorderLayout.SOUTH);

        // Barra de estado
        JLabel statusBar = new JLabel("Estado: Inactivo");
        add(statusBar, BorderLayout.NORTH);

        // Manejar cierre de ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopMonitoring();
            }
        });

        // Inicializar controlador
        priceController = new PriceController(this);
    }

    public void startMonitoring() {
        if (timer != null && timer.isRunning()) {
            return;
        }

        // Iniciar actualizaciones cada 30 segundos
        timer = new Timer(30000, e -> {
            try {
                priceController.startPriceUpdates();
            } catch (Exception ex) {
                showError("Error en actualización: " + ex.getMessage());
            }
        });
        timer.setInitialDelay(0);
        timer.start();

        updateStatus("Monitoreo activo - Actualizando datos...");
    }

    public void stopMonitoring() {
        if (timer != null) {
            timer.stop();
        }
        updateStatus("Monitoreo detenido");
    }

    public void updatePrices(List<CryptoPrice> prices) {
        SwingUtilities.invokeLater(() -> {
            pricePanel.updateTable(prices);
            cryptoChartPanel.updateChart(prices);
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

    private void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            JLabel statusBar = (JLabel) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.NORTH);
            if (statusBar != null) {
                statusBar.setText("Estado: " + message);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}