package com.hospital.util;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class ErrorHandler {
    private ErrorHandler() {}

    public static void ui(Component parent, String userMessage, Throwable e) {
        if (SwingUtilities.isEventDispatchThread()) {
            showDialog(parent, userMessage);
        } else {
            SwingUtilities.invokeLater(() -> showDialog(parent, userMessage));
        }
        console("UI error: " + userMessage, e);
    }

    public static void console(String message, Throwable e) {
        System.err.println("[ERROR] " + message);
        if (e != null) {
            System.err.println("[ERROR] " + e.getClass().getName() + ": " + e.getMessage());
            System.err.println(getStackTrace(e));
        }
    }

    private static void showDialog(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
