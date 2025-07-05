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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CryptoChartPanel extends JPanel {
    private final TimeSeriesCollection dataset;
    private final Map<String, TimeSeries> cryptoSeries;
    private final JFreeChart chart;

    public CryptoChartPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Crear dataset y series temporales
        dataset = new TimeSeriesCollection();
        cryptoSeries = new HashMap<>();

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
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Formato para eje de tiempo
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));

        // Crear panel de gráfico
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 300));
        chartPanel.setMouseZoomable(true);

        add(chartPanel, BorderLayout.CENTER);
    }

    public void updateChart(List<CryptoPrice> prices) {
        SwingUtilities.invokeLater(() -> {
            for (CryptoPrice price : prices) {
                String symbol = price.getSymbol();
                TimeSeries series = cryptoSeries.get(symbol);

                if (series == null) {
                    // Crear nueva serie para esta criptomoneda
                    series = new TimeSeries(symbol);
                    cryptoSeries.put(symbol, series);
                    dataset.addSeries(series);
                }

                // Añadir punto de datos (usando timestamp actual)
                series.addOrUpdate(new Minute(), price.getPrice());

                // Limitar a 30 puntos por serie
                if (series.getItemCount() > 30) {
                    series.delete(0, 0);
                }
            }
        });
    }
}