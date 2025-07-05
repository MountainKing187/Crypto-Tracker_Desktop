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
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CryptoChartPanel extends JPanel {
    private final TimeSeriesCollection dataset;
    private final JFreeChart chart;
    private TimeSeries priceSeries;
    private String currentCrypto = "";

    public CryptoChartPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        // Crear dataset y serie temporal
        dataset = new TimeSeriesCollection();
        priceSeries = new TimeSeries("");
        dataset.addSeries(priceSeries);

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

    // Cargar datos históricos
    public void loadHistoricalData(List<CryptoPrice> historicalPrices, String crypto) {
        SwingUtilities.invokeLater(() -> {
            priceSeries.clear();
            currentCrypto = crypto;

            // Usar un mapa para detectar y manejar duplicados
            Map<Minute, Double> minutePrices = new HashMap<>();

            for (CryptoPrice price : historicalPrices) {
                if (price.getSymbol().equals(crypto)) {
                    Minute minute = new Minute(price.getTimestampAsDate());

                    // Solo mantener el último precio por minuto
                    minutePrices.put(minute, price.getPrice());
                }
            }

            // Añadir puntos únicos al gráfico
            minutePrices.forEach((minute, price) ->
                    priceSeries.addOrUpdate(minute, price)
            );

            // Actualizar título
            chart.setTitle("Precio histórico de " + crypto.toUpperCase());

            // Ajustar eje de tiempo
            DateAxis axis = (DateAxis) chart.getXYPlot().getDomainAxis();
            if (priceSeries.getItemCount() > 24) {
                axis.setDateFormatOverride(new SimpleDateFormat("dd/MM HH:mm"));
            } else {
                axis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));
            }
        });
    }

    // Añadir un nuevo punto de precio
    public void addPricePoint(CryptoPrice price) {
        SwingUtilities.invokeLater(() -> {
            if (price.getSymbol().equals(currentCrypto)) {
                Minute minute = new Minute(price.getTimestampAsDate());

                // Usar siempre addOrUpdate para evitar duplicados
                priceSeries.addOrUpdate(minute, price.getPrice());

                // Mantener un máximo de 200 puntos
                if (priceSeries.getItemCount() > 200) {
                    priceSeries.delete(0, 0);
                }

                // Actualizar el eje de tiempo
                updateTimeAxis();
            }
        });
    }

    private void updateTimeAxis() {
        XYPlot plot = chart.getXYPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();

        if (priceSeries.getItemCount() > 24) {
            axis.setDateFormatOverride(new SimpleDateFormat("dd/MM HH:mm"));
        } else {
            axis.setDateFormatOverride(new SimpleDateFormat("HH:mm"));
        }
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
// Personalizar eje Y para precios pequeños
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(new SmallValueFormat());

        // Personalizar tooltips
        XYItemRenderer renderer = plot.getRenderer();
        if (renderer instanceof XYLineAndShapeRenderer) {
            ((XYLineAndShapeRenderer) renderer).setDefaultToolTipGenerator(
                    new StandardXYToolTipGenerator(
                            StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                            new SmallValueFormat(),
                            new SmallValueFormat()
                    )
            );
        }
    }

    // Formateador personalizado para valores pequeños
    private static class SmallValueFormat extends NumberFormat {
        @Override
        public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
            if (Math.abs(number) < 0.0001) {
                BigDecimal bd = BigDecimal.valueOf(number);
                bd = bd.setScale(10, RoundingMode.HALF_UP);
                toAppendTo.append(bd.stripTrailingZeros().toPlainString());
            } else {
                toAppendTo.append(NumberFormat.getNumberInstance().format(number));
            }
            return toAppendTo;
        }

        @Override
        public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
            return format((double) number, toAppendTo, pos);
        }

        @Override
        public Number parse(String source, ParsePosition parsePosition) {
            // No necesario para visualización
            return null;
        }
    }

    public void setChartTitle(String title) {
        chart.setTitle(title);
    }

    public void updateChartForCrypto(List<CryptoPrice> prices, String crypto) {
        SwingUtilities.invokeLater(() -> {
            // Limpiar serie si cambió la criptomoneda
            if (!crypto.equals(currentCrypto)) {
                priceSeries.clear();
                currentCrypto = crypto;
                chart.setTitle("Precio de " + crypto.toUpperCase());
            }

            // Añadir nuevos puntos para la criptomoneda seleccionada
            for (CryptoPrice price : prices) {
                if (price.getSymbol().equals(crypto)) {
                    Minute minute = new Minute(price.getTimestampAsDate());
                    priceSeries.addOrUpdate(minute, price.getPrice());

                    // Limitar a 60 puntos
                    if (priceSeries.getItemCount() > 60) {
                        priceSeries.delete(0, 0);
                    }
                }
            }
        });
    }
}