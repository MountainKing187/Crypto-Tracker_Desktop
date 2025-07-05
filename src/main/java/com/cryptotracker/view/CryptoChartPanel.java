package com.cryptotracker.view;

import com.cryptotracker.model.CryptoPrice;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class CryptoChartPanel extends JPanel {
    private final TimeSeriesCollection dataset;
    private final JFreeChart chart;
    private TimeSeries currentSeries;
    private String currentCrypto = "";

    public CryptoChartPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Crear dataset y serie temporal
        dataset = new TimeSeriesCollection();
        currentSeries = new TimeSeries("");
        dataset.addSeries(currentSeries);

        // Crear gráfico
        chart = ChartFactory.createTimeSeriesChart(
                "Evolución de Precios", // Título
                "Hora",                // Eje X
                "Precio (USD)",        // Eje Y
                dataset,                // Datos
                true,                   // Leyenda
                true,                   // Tooltips
                false                   // URLs
        );

        // Personalizar gráfico
        customizeChart();

        // Crear panel de gráfico
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        chartPanel.setMouseZoomable(true);

        add(chartPanel, BorderLayout.CENTER);
    }

    private void customizeChart() {
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.getRenderer().setSeriesPaint(0, new Color(30, 144, 255)); // Azul

        // Formato para eje de tiempo
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));
    }

    public void setChartTitle(String title) {
        chart.setTitle(title);
    }

    public void updateChartForCrypto(List<CryptoPrice> prices, String crypto) {
        SwingUtilities.invokeLater(() -> {
            // Limpiar serie si cambió la criptomoneda
            if (!crypto.equals(currentCrypto)) {
                currentSeries.clear();
                currentCrypto = crypto;
                chart.setTitle("Precio de " + crypto.toUpperCase());
            }

            // Añadir nuevos puntos para la criptomoneda seleccionada
            for (CryptoPrice price : prices) {
                if (price.getSymbol().equals(crypto)) {
                    Minute minute = new Minute(price.getTimestampAsDate());
                    currentSeries.addOrUpdate(minute, price.getPrice());

                    // Limitar a 60 puntos
                    if (currentSeries.getItemCount() > 60) {
                        currentSeries.delete(0, 0);
                    }
                }
            }
        });
    }
}