package com.hospital;

import com.hospital.ui.LoginFrame;
import com.hospital.util.ErrorHandler;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main class for Hospital Management System
 * Entry point of the application
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // bootstrap errors: console only (no dialog)
            ErrorHandler.console("Failed to set system Look & Feel.", e);
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}