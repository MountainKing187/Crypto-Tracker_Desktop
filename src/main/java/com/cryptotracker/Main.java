package com.cryptotracker;

import com.cryptotracker.view.MainFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setDefaultCloseOperation(MainFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}