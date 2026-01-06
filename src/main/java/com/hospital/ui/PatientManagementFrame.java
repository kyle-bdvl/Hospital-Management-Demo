package com.hospital.ui;

import com.hospital.dao.PatientDAO;
import com.hospital.model.Patient;
import com.hospital.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

/**
 * Patient Management Frame
 */
public class PatientManagementFrame extends JFrame {
    private User currentUser;
    private PatientDAO patientDAO;
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton;
    
    public PatientManagementFrame(User user) {
        this.currentUser = user;
        this.patientDAO = new PatientDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadPatients();
        
        setTitle("Patient Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        // Table setup
        String[] columnNames = {"ID", "Name", "Age", "Gender", "Phone", "Email", "Disease", "Blood Group", "Admission Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.setRowHeight(25);
        patientTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        patientTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Buttons
        addButton = createStyledButton("Add Patient", new Color(40, 167, 69));
        editButton = createStyledButton("Edit Patient", new Color(0, 123, 255));
        deleteButton = createStyledButton("Delete Patient", new Color(220, 53, 69));
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
        
        JLabel titleLabel = new JLabel("Patient Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JButton searchButton = createStyledButton("Search", new Color(23, 162, 184));
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        // Top Panel combining search and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Table Panel
        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        add(headerPanel, BorderLayout.NORTH);
        add(topPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
        
        // Search button event
        searchButton.addActionListener(e -> searchPatients());
        
        // Enter key search
        searchField.addActionListener(e -> searchPatients());
    }
    
    private void setupEventHandlers() {
        addButton.addActionListener(e -> openAddPatientDialog());
        editButton.addActionListener(e -> openEditPatientDialog());
        deleteButton.addActionListener(e -> deleteSelectedPatient());
        refreshButton.addActionListener(e -> loadPatients());
    }
    
    private void loadPatients() {
        SwingUtilities.invokeLater(() -> {
            try {
                tableModel.setRowCount(0);
                List<Patient> patients = patientDAO.getAllPatients();
                
                for (Patient patient : patients) {
                    Object[] row = {
                        patient.getPatientId(),
                        patient.getName(),
                        patient.getAge(),
                        patient.getGender(),
                        patient.getPhone(),
                        patient.getEmail(),
                        patient.getDisease(),
                        patient.getBloodGroup(),
                        patient.getAdmissionDate()
                    };
                    tableModel.addRow(row);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading patients: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
    
    private void searchPatients() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadPatients();
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                tableModel.setRowCount(0);
                List<Patient> patients = patientDAO.searchPatients(searchTerm);
                
                for (Patient patient : patients) {
                    Object[] row = {
                        patient.getPatientId(),
                        patient.getName(),
                        patient.getAge(),
                        patient.getGender(),
                        patient.getPhone(),
                        patient.getEmail(),
                        patient.getDisease(),
                        patient.getBloodGroup(),
                        patient.getAdmissionDate()
                    };
                    tableModel.addRow(row);
                }
                
                if (patients.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "No patients found matching: " + searchTerm, 
                        "Search Results", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error searching patients: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
    
    private void openAddPatientDialog() {
        PatientDialog dialog = new PatientDialog(this, "Add New Patient", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Patient patient = dialog.getPatient();
            try {
                if (patientDAO.addPatient(patient)) {
                    JOptionPane.showMessageDialog(this, 
                        "Patient added successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadPatients();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to add patient.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error adding patient: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void openEditPatientDialog() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a patient to edit.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int patientId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        try {
            Patient patient = patientDAO.getPatientById(patientId);
            if (patient != null) {
                PatientDialog dialog = new PatientDialog(this, "Edit Patient", patient);
                dialog.setVisible(true);
                
                if (dialog.isConfirmed()) {
                    Patient updatedPatient = dialog.getPatient();
                    updatedPatient.setPatientId(patientId);
                    
                    if (patientDAO.updatePatient(updatedPatient)) {
                        JOptionPane.showMessageDialog(this, 
                            "Patient updated successfully!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadPatients();
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Failed to update patient.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error editing patient: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void deleteSelectedPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a patient to delete.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String patientName = (String) tableModel.getValueAt(selectedRow, 1);
        int patientId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete patient: " + patientName + "?", 
            "Delete Confirmation", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                if (patientDAO.deletePatient(patientId)) {
                    JOptionPane.showMessageDialog(this, 
                        "Patient deleted successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadPatients();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete patient.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting patient: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}
