package com.hospital.ui;

import com.hospital.model.User;
import com.hospital.dao.PatientDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.dao.AppointmentDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Main Dashboard Frame for Hospital Management System
 */
public class DashboardFrame extends JFrame {
    private User currentUser;
    private JLabel welcomeLabel;
    private JLabel timeLabel;
    private Timer timeTimer;
    
    // Statistics panels
    private JLabel totalPatientsLabel;
    private JLabel totalDoctorsLabel;
    private JLabel todayAppointmentsLabel;
    
    // DAOs
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private AppointmentDAO appointmentDAO;
    
    public DashboardFrame(User user) {
        this.currentUser = user;
        this.patientDAO = new PatientDAO();
        this.doctorDAO = new DoctorDAO();
        this.appointmentDAO = new AppointmentDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        updateStatistics();
        startTimeUpdater();
        
        setTitle("Hospital Management System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        timeLabel.setForeground(Color.WHITE);
        
        totalPatientsLabel = new JLabel("0");
        totalDoctorsLabel = new JLabel("0");
        todayAppointmentsLabel = new JLabel("0");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Enhanced Header Panel with gradient
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(41, 128, 185), w, 0, new Color(142, 68, 173));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        JPanel leftHeader = new JPanel(new GridLayout(2, 1, 0, 5));
        leftHeader.setOpaque(false);
        
        JLabel titleLabel = new JLabel("🏥 Hospital Management Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel userLabel = new JLabel("👋 Welcome back, " + currentUser.getUsername() + "!");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userLabel.setForeground(new Color(236, 240, 241));
        
        leftHeader.add(titleLabel);
        leftHeader.add(userLabel);
        
        JPanel rightHeader = new JPanel(new GridLayout(2, 1, 0, 5));
        rightHeader.setOpaque(false);
        
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        startTimeUpdater();
        
        JLabel dateLabel = new JLabel("📅 " + java.time.LocalDate.now().toString());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(236, 240, 241));
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        rightHeader.add(timeLabel);
        rightHeader.add(dateLabel);
        
        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);
        
        // Main Content Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(248, 249, 250));
        
        // Statistics Panel
        JPanel statsPanel = createStatisticsPanel();
        
        // Menu Panel
        JPanel menuPanel = createMenuPanel();
        
        mainPanel.add(statsPanel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        
        // Footer Panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(52, 58, 64));
        footerPanel.setPreferredSize(new Dimension(0, 40));
        
        JLabel footerLabel = new JLabel("Hospital Management System © 2024");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        footerPanel.add(footerLabel);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createStatisticsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(new Color(248, 249, 250));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Total Patients Card
        JPanel patientsCard = createStatCard("Total Patients", totalPatientsLabel, new Color(40, 167, 69), "👥");
        
        // Total Doctors Card
        JPanel doctorsCard = createStatCard("Total Doctors", totalDoctorsLabel, new Color(0, 123, 255), "👨‍⚕️");
        
        // Today's Appointments Card
        JPanel appointmentsCard = createStatCard("Today's Appointments", todayAppointmentsLabel, new Color(255, 193, 7), "📅");
        
        statsPanel.add(patientsCard);
        statsPanel.add(doctorsCard);
        statsPanel.add(appointmentsCard);
        
        return statsPanel;
    }
    
    private JPanel createStatCard(String title, JLabel valueLabel, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(108, 117, 125));
        
        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(topPanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        menuPanel.setBackground(new Color(248, 249, 250));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Menu buttons
        JButton patientsBtn = createMenuButton("Patient Management", "Manage patient records", new Color(40, 167, 69), "👥");
        JButton doctorsBtn = createMenuButton("Doctor Management", "Manage doctor profiles", new Color(0, 123, 255), "👨‍⚕️");
        JButton appointmentsBtn = createMenuButton("Appointments", "Schedule & manage appointments", new Color(255, 193, 7), "📅");
        JButton reportsBtn = createMenuButton("Reports", "View reports & analytics", new Color(108, 117, 125), "📊");
        JButton settingsBtn = createMenuButton("Settings", "System settings", new Color(220, 53, 69), "⚙️");
        JButton logoutBtn = createMenuButton("Logout", "Exit the system", new Color(52, 58, 64), "🚪");
        
        menuPanel.add(patientsBtn);
        menuPanel.add(doctorsBtn);
        menuPanel.add(appointmentsBtn);
        menuPanel.add(reportsBtn);
        menuPanel.add(settingsBtn);
        menuPanel.add(logoutBtn);
        
        return menuPanel;
    }
    
    private JButton createMenuButton(String title, String description, Color color, String icon) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setForeground(Color.BLACK); // Ensure icon is visible
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(Color.WHITE);
        textPanel.setOpaque(true);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(color);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setOpaque(false);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(new Color(108, 117, 125));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descLabel.setOpaque(false);
        
        textPanel.add(titleLabel);
        textPanel.add(descLabel);
        
        button.add(iconLabel, BorderLayout.CENTER);
        button.add(textPanel, BorderLayout.SOUTH);
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(248, 249, 250));
                textPanel.setBackground(new Color(248, 249, 250));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                textPanel.setBackground(Color.WHITE);
            }
        });
        
        return button;
    }
    
    private void setupEventHandlers() {
        // Get menu buttons
        JPanel menuPanel = (JPanel) ((JPanel) getContentPane().getComponent(1)).getComponent(1);
        
        JButton patientsBtn = (JButton) menuPanel.getComponent(0);
        JButton doctorsBtn = (JButton) menuPanel.getComponent(1);
        JButton appointmentsBtn = (JButton) menuPanel.getComponent(2);
        JButton reportsBtn = (JButton) menuPanel.getComponent(3);
        JButton settingsBtn = (JButton) menuPanel.getComponent(4);
        JButton logoutBtn = (JButton) menuPanel.getComponent(5);
        
        patientsBtn.addActionListener(e -> openPatientManagement());
        doctorsBtn.addActionListener(e -> openDoctorManagement());
        appointmentsBtn.addActionListener(e -> openAppointmentManagement());
        reportsBtn.addActionListener(e -> openReports());
        settingsBtn.addActionListener(e -> openSettings());
        logoutBtn.addActionListener(e -> logout());
    }
    
    private void openPatientManagement() {
        new PatientManagementFrame(currentUser).setVisible(true);
    }
    
    private void openDoctorManagement() {
        new DoctorManagementFrame(currentUser).setVisible(true);
    }
    
    private void openAppointmentManagement() {
        new AppointmentManagementFrame(currentUser).setVisible(true);
    }
    
    private void openReports() {
        JOptionPane.showMessageDialog(this, "Reports feature coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openSettings() {
        JOptionPane.showMessageDialog(this, "Settings feature coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Logout Confirmation", 
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    private void updateStatistics() {
        SwingUtilities.invokeLater(() -> {
            try {
                int totalPatients = patientDAO.getAllPatients().size();
                int totalDoctors = doctorDAO.getAllDoctors().size();
                int todayAppointments = appointmentDAO.getAppointmentsByDate(java.time.LocalDate.now()).size();
                
                totalPatientsLabel.setText(String.valueOf(totalPatients));
                totalDoctorsLabel.setText(String.valueOf(totalDoctors));
                todayAppointmentsLabel.setText(String.valueOf(todayAppointments));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    private void startTimeUpdater() {
        timeTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm:ss");
                timeLabel.setText(now.format(formatter));
            }
        });
        timeTimer.start();
        
        // Initial update
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm:ss");
        timeLabel.setText(now.format(formatter));
    }
    
    @Override
    public void dispose() {
        if (timeTimer != null) {
            timeTimer.stop();
        }
        super.dispose();
    }
}
