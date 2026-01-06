package com.hospital.ui;

import com.hospital.dao.AppointmentDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.model.Appointment;
import com.hospital.model.Patient;
import com.hospital.model.Doctor;
import com.hospital.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

/**
 * Appointment Management Frame
 */
public class AppointmentManagementFrame extends JFrame {
    private User currentUser;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, cancelButton, deleteButton, refreshButton;
    private JComboBox<String> statusFilter;
    
    public AppointmentManagementFrame(User user) {
        this.currentUser = user;
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        this.doctorDAO = new DoctorDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadAppointments();
        
        setTitle("Appointment Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        // Table setup
        String[] columnNames = {"ID", "Patient", "Doctor", "Specialization", "Date", "Time", "Status", "Notes"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        appointmentTable = new JTable(tableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.setRowHeight(25);
        appointmentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        appointmentTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Status filter
        String[] statuses = {"All", "Scheduled", "Completed", "Cancelled", "No Show"};
        statusFilter = new JComboBox<>(statuses);
        statusFilter.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Buttons
        addButton = createStyledButton("Book Appointment", new Color(40, 167, 69));
        editButton = createStyledButton("Edit Appointment", new Color(0, 123, 255));
        cancelButton = createStyledButton("Cancel Appointment", new Color(255, 193, 7));
        deleteButton = createStyledButton("Delete Appointment", new Color(220, 53, 69));
        refreshButton = createStyledButton("Refresh", new Color(108, 117, 125));
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false);
        return button;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 123, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Appointment Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JButton searchButton = createStyledButton("Search", new Color(23, 162, 184));
        JButton filterButton = createStyledButton("Filter", new Color(23, 162, 184));
        
        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(statusLabel);
        filterPanel.add(statusFilter);
        filterPanel.add(filterButton);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Top Panel combining filter and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filterPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Table Panel
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        add(headerPanel, BorderLayout.NORTH);
        add(topPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
        
        // Event handlers for search and filter
        searchButton.addActionListener(e -> searchAppointments());
        filterButton.addActionListener(e -> filterAppointments());
        searchField.addActionListener(e -> searchAppointments());
    }
    
    private void setupEventHandlers() {
        addButton.addActionListener(e -> openBookAppointmentDialog());
        editButton.addActionListener(e -> openEditAppointmentDialog());
        cancelButton.addActionListener(e -> cancelSelectedAppointment());
        deleteButton.addActionListener(e -> deleteSelectedAppointment());
        refreshButton.addActionListener(e -> loadAppointments());
    }
    
    private void loadAppointments() {
        SwingUtilities.invokeLater(() -> {
            try {
                tableModel.setRowCount(0);
                List<Appointment> appointments = appointmentDAO.getAllAppointments();
                
                for (Appointment appointment : appointments) {
                    Object[] row = {
                        appointment.getAppointmentId(),
                        appointment.getPatientName(),
                        appointment.getDoctorName(),
                        appointment.getDoctorSpecialization(),
                        appointment.getAppointmentDate(),
                        appointment.getAppointmentTime(),
                        appointment.getStatus(),
                        appointment.getNotes()
                    };
                    tableModel.addRow(row);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading appointments: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
    
    private void searchAppointments() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadAppointments();
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                tableModel.setRowCount(0);
                List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
                
                for (Appointment appointment : allAppointments) {
                    if (appointment.getPatientName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        appointment.getDoctorName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        appointment.getDoctorSpecialization().toLowerCase().contains(searchTerm.toLowerCase())) {
                        
                        Object[] row = {
                            appointment.getAppointmentId(),
                            appointment.getPatientName(),
                            appointment.getDoctorName(),
                            appointment.getDoctorSpecialization(),
                            appointment.getAppointmentDate(),
                            appointment.getAppointmentTime(),
                            appointment.getStatus(),
                            appointment.getNotes()
                        };
                        tableModel.addRow(row);
                    }
                }
                
                if (tableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, 
                        "No appointments found matching: " + searchTerm, 
                        "Search Results", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error searching appointments: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
    
    private void filterAppointments() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        
        if ("All".equals(selectedStatus)) {
            loadAppointments();
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                tableModel.setRowCount(0);
                List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
                
                for (Appointment appointment : allAppointments) {
                    if (appointment.getStatus().equals(selectedStatus)) {
                        Object[] row = {
                            appointment.getAppointmentId(),
                            appointment.getPatientName(),
                            appointment.getDoctorName(),
                            appointment.getDoctorSpecialization(),
                            appointment.getAppointmentDate(),
                            appointment.getAppointmentTime(),
                            appointment.getStatus(),
                            appointment.getNotes()
                        };
                        tableModel.addRow(row);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error filtering appointments: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
    
    private void openBookAppointmentDialog() {
        try {
            List<Patient> patients = patientDAO.getAllPatients();
            List<Doctor> doctors = doctorDAO.getAllDoctors();
            
            if (patients.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No patients found. Please add patients first.", 
                    "No Patients", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (doctors.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No doctors found. Please add doctors first.", 
                    "No Doctors", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            AppointmentDialog dialog = new AppointmentDialog(this, "Book New Appointment", null, patients, doctors);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                Appointment appointment = dialog.getAppointment();
                try {
                    if (appointmentDAO.bookAppointment(appointment)) {
                        JOptionPane.showMessageDialog(this, 
                            "Appointment booked successfully!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadAppointments();
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Failed to book appointment.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Error booking appointment: " + e.getMessage(), 
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading data: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void openEditAppointmentDialog() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an appointment to edit.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int appointmentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        try {
            Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
            List<Patient> patients = patientDAO.getAllPatients();
            List<Doctor> doctors = doctorDAO.getAllDoctors();
            
            if (appointment != null) {
                AppointmentDialog dialog = new AppointmentDialog(this, "Edit Appointment", appointment, patients, doctors);
                dialog.setVisible(true);
                
                if (dialog.isConfirmed()) {
                    Appointment updatedAppointment = dialog.getAppointment();
                    updatedAppointment.setAppointmentId(appointmentId);
                    
                    if (appointmentDAO.updateAppointment(updatedAppointment)) {
                        JOptionPane.showMessageDialog(this, 
                            "Appointment updated successfully!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadAppointments();
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Failed to update appointment.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error editing appointment: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void cancelSelectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an appointment to cancel.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String patientName = (String) tableModel.getValueAt(selectedRow, 1);
        String doctorName = (String) tableModel.getValueAt(selectedRow, 2);
        int appointmentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel the appointment for " + patientName + " with " + doctorName + "?", 
            "Cancel Confirmation", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                if (appointmentDAO.cancelAppointment(appointmentId)) {
                    JOptionPane.showMessageDialog(this, 
                        "Appointment cancelled successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAppointments();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to cancel appointment.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error cancelling appointment: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void deleteSelectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an appointment to delete.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String patientName = (String) tableModel.getValueAt(selectedRow, 1);
        String doctorName = (String) tableModel.getValueAt(selectedRow, 2);
        int appointmentId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete the appointment for " + patientName + " with " + doctorName + "?", 
            "Delete Confirmation", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                if (appointmentDAO.deleteAppointment(appointmentId)) {
                    JOptionPane.showMessageDialog(this, 
                        "Appointment deleted successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAppointments();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete appointment.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting appointment: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}
