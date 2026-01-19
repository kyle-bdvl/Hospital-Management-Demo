package com.hospital.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;

/**
 * Centralized logging utility for the Hospital Management System.
 * Provides consistent logging across all application components.
 */
public class AppLogger {
    private static final Logger LOGGER = Logger.getLogger("HospitalManagement");
    
    static {
        // Configure logger
        LOGGER.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(handler);
        LOGGER.setUseParentHandlers(false);
    }
    
    /**
     * Log an error message with exception details.
     * @param className The class where the error occurred
     * @param methodName The method where the error occurred
     * @param message The error message
     * @param throwable The exception that was thrown
     */
    public static void error(String className, String methodName, String message, Throwable throwable) {
        LOGGER.logp(Level.SEVERE, className, methodName, message, throwable);
    }
    
    /**
     * Log an error message without exception.
     * @param className The class where the error occurred
     * @param methodName The method where the error occurred
     * @param message The error message
     */
    public static void error(String className, String methodName, String message) {
        LOGGER.logp(Level.SEVERE, className, methodName, message);
    }
    
    /**
     * Log a warning message.
     * @param className The class name
     * @param methodName The method name
     * @param message The warning message
     */
    public static void warning(String className, String methodName, String message) {
        LOGGER.logp(Level.WARNING, className, methodName, message);
    }
    
    /**
     * Log an info message.
     * @param className The class name
     * @param methodName The method name
     * @param message The info message
     */
    public static void info(String className, String methodName, String message) {
        LOGGER.logp(Level.INFO, className, methodName, message);
    }
}
