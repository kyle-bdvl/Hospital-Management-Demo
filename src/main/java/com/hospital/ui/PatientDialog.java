package com.hospital.ui;

import com.hospital.model.Patient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Dialog for adding/editing patient information
 */
public class PatientDialog extends JDialog {
    private JTextField nameField, ageField, phoneField, emailField, addressField, diseaseField, emergencyContactField;
    private JComboBox<String> genderCombo, bloodGroupCombo;
    private JTextField admissionDateField;
    private JButton saveButton, cancelButton;
    private boolean confirmed = false;
    private Patient patient;
    
    public PatientDialog(Frame parent, String title, Patient existingPatient) {
        super(parent, title, true);
        this.patient = existingPatient;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        if (existingPatient != null) {
            populateFields(existingPatient);
        }
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        nameField = new JTextField(20);
        ageField = new JTextField(20);
        phoneField = new JTextField(20);
        emailField = new JTextField(20);
        addressField = new JTextField(20);
        diseaseField = new JTextField(20);
        emergencyContactField = new JTextField(20);
        admissionDateField = new JTextField(20);
        
        String[] genders = {"Male", "Female", "Other"};
        genderCombo = new JComboBox<>(genders);
        
        String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        bloodGroupCombo = new JComboBox<>(bloodGroups);
        
        saveButton = new JButton("Save");
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(false);
        
        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(108, 117, 125));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setOpaque(true);
        cancelButton.setBorderPainted(false);
        
        // Set current date as default admission date
        admissionDateField.setText(LocalDate.now().toString());
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 123, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel(getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Form fields
        addFormField(formPanel, gbc, 0, "Name *:", nameField);
        addFormField(formPanel, gbc, 1, "Age *:", ageField);
        addFormField(formPanel, gbc, 2, "Gender *:", genderCombo);
        addFormField(formPanel, gbc, 3, "Phone *:", phoneField);
        addFormField(formPanel, gbc, 4, "Email:", emailField);
        addFormField(formPanel, gbc, 5, "Address:", addressField);
        addFormField(formPanel, gbc, 6, "Disease:", diseaseField);
        addFormField(formPanel, gbc, 7, "Blood Group:", bloodGroupCombo);
        addFormField(formPanel, gbc, 8, "Emergency Contact:", emergencyContactField);
        addFormField(formPanel, gbc, 9, "Admission Date (YYYY-MM-DD):", admissionDateField);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(field, gbc);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
    }
    
    private void setupEventHandlers() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateAndSave()) {
                    confirmed = true;
                    dispose();
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void populateFields(Patient patient) {
        nameField.setText(patient.getName());
        ageField.setText(String.valueOf(patient.getAge()));
        genderCombo.setSelectedItem(patient.getGender());
        phoneField.setText(patient.getPhone());
        emailField.setText(patient.getEmail());
        addressField.setText(patient.getAddress());
        diseaseField.setText(patient.getDisease());
        bloodGroupCombo.setSelectedItem(patient.getBloodGroup());
        emergencyContactField.setText(patient.getEmergencyContact());
        
        if (patient.getAdmissionDate() != null) {
            admissionDateField.setText(patient.getAdmissionDate().toString());
        }
    }
    
    private boolean validateAndSave() {
        // Validate required fields
        if (nameField.getText().trim().isEmpty()) {
            showError("Name is required.");
            nameField.requestFocus();
            return false;
        }
        
        if (ageField.getText().trim().isEmpty()) {
            showError("Age is required.");
            ageField.requestFocus();
            return false;
        }
        
        if (phoneField.getText().trim().isEmpty()) {
            showError("Phone is required.");
            phoneField.requestFocus();
            return false;
        }
        
        // Validate age
        int age;
        try {
            age = Integer.parseInt(ageField.getText().trim());
            if (age < 0 || age > 150) {
                showError("Please enter a valid age (0-150).");
                ageField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid age.");
            ageField.requestFocus();
            return false;
        }
        
        // Validate admission date
        LocalDate admissionDate;
        try {
            admissionDate = LocalDate.parse(admissionDateField.getText().trim());
        } catch (DateTimeParseException e) {
            showError("Please enter a valid date in YYYY-MM-DD format.");
            admissionDateField.requestFocus();
            return false;
        }
        
        // Create patient object
        if (patient == null) {
            patient = new Patient();
        }
        
        patient.setName(nameField.getText().trim());
        patient.setAge(age);
        patient.setGender((String) genderCombo.getSelectedItem());
        patient.setPhone(phoneField.getText().trim());
        patient.setEmail(emailField.getText().trim().isEmpty() ? null : emailField.getText().trim());
        patient.setAddress(addressField.getText().trim().isEmpty() ? null : addressField.getText().trim());
        patient.setDisease(diseaseField.getText().trim().isEmpty() ? null : diseaseField.getText().trim());
        patient.setBloodGroup((String) bloodGroupCombo.getSelectedItem());
        patient.setEmergencyContact(emergencyContactField.getText().trim().isEmpty() ? null : emergencyContactField.getText().trim());
        patient.setAdmissionDate(admissionDate);
        
        return true;
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Patient getPatient() {
        return patient;
    }
}
