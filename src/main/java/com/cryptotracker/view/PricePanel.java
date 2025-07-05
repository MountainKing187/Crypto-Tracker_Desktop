package com.cryptotracker.view;

import com.cryptotracker.model.CryptoPrice;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PricePanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JLabel lastUpdateLabel;
    private List<CryptoPrice> lastPrices;

    public PricePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Configurar modelo de tabla
        tableModel = new DefaultTableModel(
                new Object[]{"Símbolo", "Precio", "Cambio 24h"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setAutoCreateRowSorter(true);

        // Configurar renderizado para valores numéricos
        table.setDefaultRenderer(Double.class, new PriceCellRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        // Panel inferior para información de actualización
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lastUpdateLabel = new JLabel("Última actualización: --:--:--");
        bottomPanel.add(lastUpdateLabel);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void updateTable(List<CryptoPrice> prices) {
        SwingUtilities.invokeLater(() -> {
            this.lastPrices = prices; // Guardar última lista
            tableModel.setRowCount(0); // Limpiar tabla

            for (CryptoPrice price : prices) {
                // Simular cambio porcentual (en implementación real vendría del servidor)
                double change = (Math.random() * 10) - 5; // Entre -5% y +5%

                tableModel.addRow(new Object[]{
                        price.getSymbol(),
                        price.getPrice(),
                        change
                });
            }

            // Actualizar timestamp
            lastUpdateLabel.setText("Última actualización: " + new java.util.Date());
        });
    }

    // Renderer personalizado para colores de precios
    private static class PriceCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (column == 1) { // Columna de precio
                c.setForeground(new Color(0, 100, 0)); // Verde oscuro
                setHorizontalAlignment(SwingConstants.RIGHT);
            }
            else if (column == 2) { // Columna de cambio porcentual
                double change = (Double) value;
                if (change > 0) {
                    c.setForeground(new Color(0, 128, 0)); // Verde
                    setText("+" + String.format("%.2f%%", change));
                } else {
                    c.setForeground(Color.RED);
                    setText(String.format("%.2f%%", change));
                }
                setHorizontalAlignment(SwingConstants.RIGHT);
            }
            return c;
        }
    }

    public List<CryptoPrice> getLastPrices() {
        return lastPrices;
    }
}