package com.hospital.ui;

import com.hospital.dao.DoctorDAO;
import com.hospital.model.Doctor;
import com.hospital.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Doctor Management Frame
 */
public class DoctorManagementFrame extends JFrame {
    private User currentUser;
    private DoctorDAO doctorDAO;
    private JTable doctorTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton;
    
    public DoctorManagementFrame(User user) {
        this.currentUser = user;
        this.doctorDAO = new DoctorDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadDoctors();
        
        setTitle("Doctor Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        // Table setup
        String[] columnNames = {"ID", "Name", "Specialization", "Phone", "Email", "Experience", "Qualification", "Fee", "Available Days", "Available Time"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        doctorTable = new JTable(tableModel);
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        doctorTable.setRowHeight(25);
        doctorTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        doctorTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Buttons
        addButton = createStyledButton("Add Doctor", new Color(40, 167, 69));
        editButton = createStyledButton("Edit Doctor", new Color(0, 123, 255));
        deleteButton = createStyledButton("Delete Doctor", new Color(220, 53, 69));
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
        
        JLabel titleLabel = new JLabel("Doctor Management");
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
        JScrollPane scrollPane = new JScrollPane(doctorTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        add(headerPanel, BorderLayout.NORTH);
        add(topPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
        
        // Search button event
        searchButton.addActionListener(e -> searchDoctors());
        
        // Enter key search
        searchField.addActionListener(e -> searchDoctors());
    }
    
    private void setupEventHandlers() {
        addButton.addActionListener(e -> openAddDoctorDialog());
        editButton.addActionListener(e -> openEditDoctorDialog());
        deleteButton.addActionListener(e -> deleteSelectedDoctor());
        refreshButton.addActionListener(e -> loadDoctors());
    }
    
    private void loadDoctors() {
        SwingUtilities.invokeLater(() -> {
            try {
                tableModel.setRowCount(0);
                List<Doctor> doctors = doctorDAO.getAllDoctors();
                
                for (Doctor doctor : doctors) {
                    Object[] row = {
                        doctor.getDoctorId(),
                        doctor.getName(),
                        doctor.getSpecialization(),
                        doctor.getPhone(),
                        doctor.getEmail(),
                        doctor.getExperienceYears() + " years",
                        doctor.getQualification(),
                        "$" + doctor.getConsultationFee(),
                        doctor.getAvailableDays(),
                        doctor.getAvailableTime()
                    };
                    tableModel.addRow(row);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading doctors: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
    
    private void searchDoctors() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadDoctors();
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                tableModel.setRowCount(0);
                List<Doctor> doctors = doctorDAO.searchDoctors(searchTerm);
                
                for (Doctor doctor : doctors) {
                    Object[] row = {
                        doctor.getDoctorId(),
                        doctor.getName(),
                        doctor.getSpecialization(),
                        doctor.getPhone(),
                        doctor.getEmail(),
                        doctor.getExperienceYears() + " years",
                        doctor.getQualification(),
                        "$" + doctor.getConsultationFee(),
                        doctor.getAvailableDays(),
                        doctor.getAvailableTime()
                    };
                    tableModel.addRow(row);
                }
                
                if (doctors.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "No doctors found matching: " + searchTerm, 
                        "Search Results", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error searching doctors: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
    
    private void openAddDoctorDialog() {
        DoctorDialog dialog = new DoctorDialog(this, "Add New Doctor", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Doctor doctor = dialog.getDoctor();
            try {
                if (doctorDAO.addDoctor(doctor)) {
                    JOptionPane.showMessageDialog(this, 
                        "Doctor added successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadDoctors();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to add doctor.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error adding doctor: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void openEditDoctorDialog() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a doctor to edit.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int doctorId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        try {
            Doctor doctor = doctorDAO.getDoctorById(doctorId);
            if (doctor != null) {
                DoctorDialog dialog = new DoctorDialog(this, "Edit Doctor", doctor);
                dialog.setVisible(true);
                
                if (dialog.isConfirmed()) {
                    Doctor updatedDoctor = dialog.getDoctor();
                    updatedDoctor.setDoctorId(doctorId);
                    
                    if (doctorDAO.updateDoctor(updatedDoctor)) {
                        JOptionPane.showMessageDialog(this, 
                            "Doctor updated successfully!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadDoctors();
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Failed to update doctor.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error editing doctor: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void deleteSelectedDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a doctor to delete.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String doctorName = (String) tableModel.getValueAt(selectedRow, 1);
        int doctorId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete doctor: " + doctorName + "?", 
            "Delete Confirmation", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                if (doctorDAO.deleteDoctor(doctorId)) {
                    JOptionPane.showMessageDialog(this, 
                        "Doctor deleted successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadDoctors();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete doctor.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting doctor: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}
