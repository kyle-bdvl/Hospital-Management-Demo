package com.hospital.ui;

import com.hospital.dao.AppointmentDAO;
import com.hospital.model.Appointment;
import com.hospital.model.Patient;
import com.hospital.model.Doctor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Dialog for booking/editing appointments
 */
public class AppointmentDialog extends JDialog {
    private JComboBox<Patient> patientCombo;
    private JComboBox<Doctor> doctorCombo;
    private JTextField dateField, timeField, notesField;
    private JComboBox<String> statusCombo;
    private JButton saveButton, cancelButton, checkAvailabilityButton;
    private boolean confirmed = false;
    private Appointment appointment;
    private List<Patient> patients;
    private List<Doctor> doctors;
    private AppointmentDAO appointmentDAO;
    
    public AppointmentDialog(Frame parent, String title, Appointment existingAppointment, 
                           List<Patient> patients, List<Doctor> doctors) {
        super(parent, title, true);
        this.appointment = existingAppointment;
        this.patients = patients;
        this.doctors = doctors;
        this.appointmentDAO = new AppointmentDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        if (existingAppointment != null) {
            populateFields(existingAppointment);
        }
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        // Patient combo
        patientCombo = new JComboBox<>();
        for (Patient patient : patients) {
            patientCombo.addItem(patient);
        }
        patientCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Patient) {
                    Patient patient = (Patient) value;
                    setText(patient.getName() + " (ID: " + patient.getPatientId() + ")");
                }
                return this;
            }
        });
        
        // Doctor combo
        doctorCombo = new JComboBox<>();
        for (Doctor doctor : doctors) {
            doctorCombo.addItem(doctor);
        }
        doctorCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Doctor) {
                    Doctor doctor = (Doctor) value;
                    setText("Dr. " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
                }
                return this;
            }
        });
        
        dateField = new JTextField(20);
        timeField = new JTextField(20);
        notesField = new JTextField(20);
        
        String[] statuses = {"Scheduled", "Completed", "Cancelled", "No Show"};
        statusCombo = new JComboBox<>(statuses);
        
        saveButton = new JButton("Save");
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        
        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(108, 117, 125));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        
        checkAvailabilityButton = new JButton("Check Availability");
        checkAvailabilityButton.setBackground(new Color(23, 162, 184));
        checkAvailabilityButton.setForeground(Color.WHITE);
        checkAvailabilityButton.setFocusPainted(false);
        
        // Set default values
        dateField.setText(LocalDate.now().toString());
        timeField.setText("10:00");
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
        addFormField(formPanel, gbc, 0, "Patient *:", patientCombo);
        addFormField(formPanel, gbc, 1, "Doctor *:", doctorCombo);
        addFormField(formPanel, gbc, 2, "Date (YYYY-MM-DD) *:", dateField);
        addFormField(formPanel, gbc, 3, "Time (HH:MM) *:", timeField);
        addFormField(formPanel, gbc, 4, "Status:", statusCombo);
        addFormField(formPanel, gbc, 5, "Notes:", notesField);
        
        // Check availability button
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(checkAvailabilityButton, gbc);
        
        // Add help text
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        JLabel helpLabel = new JLabel("<html><i>Date format: YYYY-MM-DD (e.g., 2024-03-15)<br>Time format: HH:MM (e.g., 14:30)</i></html>");
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
        
        checkAvailabilityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkDoctorAvailability();
            }
        });
    }
    
    private void populateFields(Appointment appointment) {
        // Find and select patient
        for (int i = 0; i < patientCombo.getItemCount(); i++) {
            Patient patient = patientCombo.getItemAt(i);
            if (patient.getPatientId() == appointment.getPatientId()) {
                patientCombo.setSelectedIndex(i);
                break;
            }
        }
        
        // Find and select doctor
        for (int i = 0; i < doctorCombo.getItemCount(); i++) {
            Doctor doctor = doctorCombo.getItemAt(i);
            if (doctor.getDoctorId() == appointment.getDoctorId()) {
                doctorCombo.setSelectedIndex(i);
                break;
            }
        }
        
        dateField.setText(appointment.getAppointmentDate().toString());
        timeField.setText(appointment.getAppointmentTime().toString());
        statusCombo.setSelectedItem(appointment.getStatus());
        notesField.setText(appointment.getNotes());
    }
    
    private void checkDoctorAvailability() {
        try {
            Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            LocalTime time = LocalTime.parse(timeField.getText().trim());
            
            if (selectedDoctor != null) {
                boolean isAvailable = appointmentDAO.isDoctorAvailable(selectedDoctor.getDoctorId(), date, time);
                
                if (isAvailable) {
                    JOptionPane.showMessageDialog(this, 
                        "Dr. " + selectedDoctor.getName() + " is available on " + date + " at " + time, 
                        "Available", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Dr. " + selectedDoctor.getName() + " is NOT available on " + date + " at " + time + 
                        "\nPlease choose a different time.", 
                        "Not Available", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid date and time formats.", 
                "Invalid Format", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error checking availability: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateAndSave() {
        // Validate required fields
        if (patientCombo.getSelectedItem() == null) {
            showError("Please select a patient.");
            patientCombo.requestFocus();
            return false;
        }
        
        if (doctorCombo.getSelectedItem() == null) {
            showError("Please select a doctor.");
            doctorCombo.requestFocus();
            return false;
        }
        
        if (dateField.getText().trim().isEmpty()) {
            showError("Date is required.");
            dateField.requestFocus();
            return false;
        }
        
        if (timeField.getText().trim().isEmpty()) {
            showError("Time is required.");
            timeField.requestFocus();
            return false;
        }
        
        // Validate date and time
        LocalDate appointmentDate;
        LocalTime appointmentTime;
        
        try {
            appointmentDate = LocalDate.parse(dateField.getText().trim());
        } catch (DateTimeParseException e) {
            showError("Please enter a valid date in YYYY-MM-DD format.");
            dateField.requestFocus();
            return false;
        }
        
        try {
            appointmentTime = LocalTime.parse(timeField.getText().trim());
        } catch (DateTimeParseException e) {
            showError("Please enter a valid time in HH:MM format.");
            timeField.requestFocus();
            return false;
        }
        
        // Check if appointment is in the past
        if (appointmentDate.isBefore(LocalDate.now()) || 
            (appointmentDate.equals(LocalDate.now()) && appointmentTime.isBefore(LocalTime.now()))) {
            showError("Cannot schedule appointments in the past.");
            return false;
        }
        
        // Check doctor availability (only for new appointments or if time changed)
        Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();
        if (appointment == null || 
            !appointmentDate.equals(appointment.getAppointmentDate()) || 
            !appointmentTime.equals(appointment.getAppointmentTime()) ||
            selectedDoctor.getDoctorId() != appointment.getDoctorId()) {
            
            try {
                if (!appointmentDAO.isDoctorAvailable(selectedDoctor.getDoctorId(), appointmentDate, appointmentTime)) {
                    showError("Dr. " + selectedDoctor.getName() + " is not available at the selected time.");
                    return false;
                }
            } catch (Exception e) {
                showError("Error checking doctor availability: " + e.getMessage());
                return false;
            }
        }
        
        // Create appointment object
        if (appointment == null) {
            appointment = new Appointment();
        }
        
        Patient selectedPatient = (Patient) patientCombo.getSelectedItem();
        
        appointment.setPatientId(selectedPatient.getPatientId());
        appointment.setDoctorId(selectedDoctor.getDoctorId());
        appointment.setAppointmentDate(appointmentDate);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setStatus((String) statusCombo.getSelectedItem());
        appointment.setNotes(notesField.getText().trim().isEmpty() ? null : notesField.getText().trim());
        
        return true;
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Appointment getAppointment() {
        return appointment;
    }
}
