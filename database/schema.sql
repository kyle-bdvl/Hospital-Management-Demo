-- Hospital Management System Database Schema
-- Create database
CREATE DATABASE IF NOT EXISTS hospital_management;
USE hospital_management;

-- Create patients table
CREATE TABLE patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    phone VARCHAR(15) UNIQUE,
    email VARCHAR(100),
    address TEXT,
    disease VARCHAR(200),
    blood_group VARCHAR(5),
    emergency_contact VARCHAR(15),
    admission_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create doctors table
CREATE TABLE doctors (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    phone VARCHAR(15) UNIQUE,
    email VARCHAR(100) UNIQUE,
    experience_years INT,
    qualification VARCHAR(200),
    consultation_fee DECIMAL(10,2),
    available_days VARCHAR(50),
    available_time VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create appointments table
CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status ENUM('Scheduled', 'Completed', 'Cancelled', 'No Show') DEFAULT 'Scheduled',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE
);

-- Create bills table
CREATE TABLE bills (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    appointment_id INT,
    total_amount DECIMAL(10,2) NOT NULL,
    paid_amount DECIMAL(10,2) DEFAULT 0,
    payment_status ENUM('Pending', 'Partial', 'Paid') DEFAULT 'Pending',
    bill_date DATE NOT NULL,
    due_date DATE,
    services TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id) ON DELETE SET NULL
);

-- Create users table for login system
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('Admin', 'Staff', 'Doctor') NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert default admin user (password: admin123)
INSERT INTO users (username, password, role, full_name, email) 
VALUES ('admin', 'admin123', 'Admin', 'System Administrator', 'admin@hospital.com');

-- Insert sample doctors
INSERT INTO doctors (name, specialization, phone, email, experience_years, qualification, consultation_fee, available_days, available_time) VALUES
('Dr. John Smith', 'Cardiology', '9876543210', 'john.smith@hospital.com', 15, 'MD Cardiology', 500.00, 'Mon,Tue,Wed,Thu,Fri', '09:00-17:00'),
('Dr. Sarah Johnson', 'Pediatrics', '9876543211', 'sarah.johnson@hospital.com', 12, 'MD Pediatrics', 400.00, 'Mon,Wed,Fri,Sat', '10:00-16:00'),
('Dr. Michael Brown', 'Orthopedics', '9876543212', 'michael.brown@hospital.com', 18, 'MS Orthopedics', 600.00, 'Tue,Thu,Sat', '08:00-14:00'),
('Dr. Emily Davis', 'Dermatology', '9876543213', 'emily.davis@hospital.com', 10, 'MD Dermatology', 450.00, 'Mon,Tue,Thu,Fri', '11:00-18:00'),
('Dr. Robert Wilson', 'Neurology', '9876543214', 'robert.wilson@hospital.com', 20, 'DM Neurology', 700.00, 'Wed,Thu,Fri,Sat', '09:00-15:00');

-- Insert sample patients
INSERT INTO patients (name, age, gender, phone, email, address, disease, blood_group, emergency_contact, admission_date) VALUES
('Alice Johnson', 28, 'Female', '8765432109', 'alice.j@email.com', '123 Main St, City', 'Hypertension', 'A+', '8765432108', '2024-01-15'),
('Bob Smith', 45, 'Male', '8765432108', 'bob.smith@email.com', '456 Oak Ave, City', 'Diabetes', 'B+', '8765432107', '2024-01-20'),
('Carol Davis', 32, 'Female', '8765432107', 'carol.d@email.com', '789 Pine St, City', 'Migraine', 'O+', '8765432106', '2024-02-01'),
('David Wilson', 55, 'Male', '8765432106', 'david.w@email.com', '321 Elm St, City', 'Arthritis', 'AB+', '8765432105', '2024-02-10'),
('Eva Brown', 29, 'Female', '8765432105', 'eva.brown@email.com', '654 Maple Ave, City', 'Skin Allergy', 'A-', '8765432104', '2024-02-15');
