package com.hospital.ui;

import com.hospital.model.Doctor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

/**
 * Dialog for adding/editing doctor information
 */
public class DoctorDialog extends JDialog {
    private JTextField nameField, phoneField, emailField, experienceField, qualificationField, feeField;
    private JTextField availableDaysField, availableTimeField;
    private JComboBox<String> specializationCombo;
    private JButton saveButton, cancelButton;
    private boolean confirmed = false;
    private Doctor doctor;
    
    public DoctorDialog(Frame parent, String title, Doctor existingDoctor) {
        super(parent, title, true);
        this.doctor = existingDoctor;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        if (existingDoctor != null) {
            populateFields(existingDoctor);
        }
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        nameField = new JTextField(20);
        phoneField = new JTextField(20);
        emailField = new JTextField(20);
        experienceField = new JTextField(20);
        qualificationField = new JTextField(20);
        feeField = new JTextField(20);
        availableDaysField = new JTextField(20);
        availableTimeField = new JTextField(20);
        
        String[] specializations = {
            "Cardiology", "Pediatrics", "Orthopedics", "Dermatology", "Neurology",
            "Gynecology", "Psychiatry", "Radiology", "Anesthesiology", "Emergency Medicine",
            "Internal Medicine", "Surgery", "Oncology", "Ophthalmology", "ENT"
        };
        specializationCombo = new JComboBox<>(specializations);
        specializationCombo.setEditable(true);
        
        saveButton = new JButton("Save");
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        
        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(108, 117, 125));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        
        // Set default values
        availableDaysField.setText("Mon,Tue,Wed,Thu,Fri");
        availableTimeField.setText("09:00-17:00");
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
        addFormField(formPanel, gbc, 1, "Specialization *:", specializationCombo);
        addFormField(formPanel, gbc, 2, "Phone *:", phoneField);
        addFormField(formPanel, gbc, 3, "Email:", emailField);
        addFormField(formPanel, gbc, 4, "Experience (years):", experienceField);
        addFormField(formPanel, gbc, 5, "Qualification:", qualificationField);
        addFormField(formPanel, gbc, 6, "Consultation Fee:", feeField);
        addFormField(formPanel, gbc, 7, "Available Days:", availableDaysField);
        addFormField(formPanel, gbc, 8, "Available Time:", availableTimeField);
        
        // Add help text
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        JLabel helpLabel = new JLabel("<html><i>Available Days: Mon,Tue,Wed,Thu,Fri<br>Available Time: 09:00-17:00</i></html>");
        helpLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        helpLabel.setForeground(new Color(108, 117, 125));
        formPanel.add(helpLabel, gbc);
        
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
    
    private void populateFields(Doctor doctor) {
        nameField.setText(doctor.getName());
        specializationCombo.setSelectedItem(doctor.getSpecialization());
        phoneField.setText(doctor.getPhone());
        emailField.setText(doctor.getEmail());
        experienceField.setText(String.valueOf(doctor.getExperienceYears()));
        qualificationField.setText(doctor.getQualification());
        
        if (doctor.getConsultationFee() != null) {
            feeField.setText(doctor.getConsultationFee().toString());
        }
        
        availableDaysField.setText(doctor.getAvailableDays());
        availableTimeField.setText(doctor.getAvailableTime());
    }
    
    private boolean validateAndSave() {
        // Validate required fields
        if (nameField.getText().trim().isEmpty()) {
            showError("Name is required.");
            nameField.requestFocus();
            return false;
        }
        
        if (specializationCombo.getSelectedItem() == null || 
            specializationCombo.getSelectedItem().toString().trim().isEmpty()) {
            showError("Specialization is required.");
            specializationCombo.requestFocus();
            return false;
        }
        
        if (phoneField.getText().trim().isEmpty()) {
            showError("Phone is required.");
            phoneField.requestFocus();
            return false;
        }
        
        // Validate experience years
        int experienceYears = 0;
        if (!experienceField.getText().trim().isEmpty()) {
            try {
                experienceYears = Integer.parseInt(experienceField.getText().trim());
                if (experienceYears < 0 || experienceYears > 50) {
                    showError("Please enter valid experience years (0-50).");
                    experienceField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                showError("Please enter a valid number for experience years.");
                experienceField.requestFocus();
                return false;
            }
        }
        
        // Validate consultation fee
        BigDecimal consultationFee = BigDecimal.ZERO;
        if (!feeField.getText().trim().isEmpty()) {
            try {
                consultationFee = new BigDecimal(feeField.getText().trim());
                if (consultationFee.compareTo(BigDecimal.ZERO) < 0) {
                    showError("Consultation fee cannot be negative.");
                    feeField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                showError("Please enter a valid consultation fee.");
                feeField.requestFocus();
                return false;
            }
        }
        
        // Create doctor object
        if (doctor == null) {
            doctor = new Doctor();
        }
        
        doctor.setName(nameField.getText().trim());
        doctor.setSpecialization(specializationCombo.getSelectedItem().toString().trim());
        doctor.setPhone(phoneField.getText().trim());
        doctor.setEmail(emailField.getText().trim().isEmpty() ? null : emailField.getText().trim());
        doctor.setExperienceYears(experienceYears);
        doctor.setQualification(qualificationField.getText().trim().isEmpty() ? null : qualificationField.getText().trim());
        doctor.setConsultationFee(consultationFee);
        doctor.setAvailableDays(availableDaysField.getText().trim().isEmpty() ? null : availableDaysField.getText().trim());
        doctor.setAvailableTime(availableTimeField.getText().trim().isEmpty() ? null : availableTimeField.getText().trim());
        
        return true;
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Doctor getDoctor() {
        return doctor;
    }
}
