package com.hospital.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Doctor model class representing doctor entity
 */
public class Doctor {
    private int doctorId;
    private String name;
    private String specialization;
    private String phone;
    private String email;
    private int experienceYears;
    private String qualification;
    private BigDecimal consultationFee;
    private String availableDays;
    private String availableTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public Doctor() {}
    
    // Constructor with essential fields
    public Doctor(String name, String specialization, String phone, BigDecimal consultationFee) {
        this.name = name;
        this.specialization = specialization;
        this.phone = phone;
        this.consultationFee = consultationFee;
    }
    
    // Full constructor
    public Doctor(int doctorId, String name, String specialization, String phone, String email,
                  int experienceYears, String qualification, BigDecimal consultationFee,
                  String availableDays, String availableTime) {
        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
        this.phone = phone;
        this.email = email;
        this.experienceYears = experienceYears;
        this.qualification = qualification;
        this.consultationFee = consultationFee;
        this.availableDays = availableDays;
        this.availableTime = availableTime;
    }
    
    // Getters and Setters
    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }
    
    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    
    public BigDecimal getConsultationFee() { return consultationFee; }
    public void setConsultationFee(BigDecimal consultationFee) { this.consultationFee = consultationFee; }
    
    public String getAvailableDays() { return availableDays; }
    public void setAvailableDays(String availableDays) { this.availableDays = availableDays; }
    
    public String getAvailableTime() { return availableTime; }
    public void setAvailableTime(String availableTime) { this.availableTime = availableTime; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return "Dr. " + name + " (" + specialization + ")";
    }
}
