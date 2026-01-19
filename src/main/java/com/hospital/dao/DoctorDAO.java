package com.hospital.dao;

import com.hospital.model.Doctor;
import com.hospital.util.AppLogger;
import com.hospital.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Doctor operations
 */
public class DoctorDAO {

    private void rowMappingHelper(ResultSet rs, Doctor doctor) throws SQLException {
        doctor.setDoctorId(rs.getInt("doctor_id"));
        doctor.setName(rs.getString("name"));
        doctor.setSpecialization(rs.getString("specialization"));
        doctor.setPhone(rs.getString("phone"));
        doctor.setEmail(rs.getString("email"));
        doctor.setExperienceYears(rs.getInt("experience_years"));
        doctor.setQualification(rs.getString("qualification"));
        doctor.setConsultationFee(rs.getBigDecimal("consultation_fee"));
        doctor.setAvailableDays(rs.getString("available_days"));
        doctor.setAvailableTime(rs.getString("available_time"));
    }

    private void setDoctorParameter(PreparedStatement pstmt, Doctor doctor) throws SQLException {
        pstmt.setString(1, doctor.getName());
        pstmt.setString(2, doctor.getSpecialization());
        pstmt.setString(3, doctor.getPhone());
        pstmt.setString(4, doctor.getEmail());
        pstmt.setInt(5, doctor.getExperienceYears());
        pstmt.setString(6, doctor.getQualification());
        pstmt.setBigDecimal(7, doctor.getConsultationFee());
        pstmt.setString(8, doctor.getAvailableDays());
        pstmt.setString(9, doctor.getAvailableTime());
    }

    /**
     * Add a new doctor to the database
     */
    public boolean addDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors (name, specialization, phone, email, experience_years, " +
                "qualification, consultation_fee, available_days, available_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setDoctorParameter(pstmt, doctor);
            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            AppLogger.error(getClass().getSimpleName(), "", "Error occurred", e);
            return false;
        }
    }

    /**
     * Get all doctors from the database
     */
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Doctor doctor = new Doctor();
                rowMappingHelper(rs, doctor);
                doctors.add(doctor);
            }

        } catch (SQLException e) {
            AppLogger.error(getClass().getSimpleName(), "", "Error occurred", e);
        }

        return doctors;
    }

    /**
     * Get doctor by ID
     */
    public Doctor getDoctorById(int doctorId) {
        String sql = "SELECT * FROM doctors WHERE doctor_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Doctor doctor = new Doctor();
                rowMappingHelper(rs, doctor);
                return doctor;
            }

        } catch (SQLException e) {
            AppLogger.error(getClass().getSimpleName(), "", "Error occurred", e);
        }

        return null;
    }

    /**
     * Update doctor information
     */
    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctors SET name = ?, specialization = ?, phone = ?, email = ?, " +
                "experience_years = ?, qualification = ?, consultation_fee = ?, " +
                "available_days = ?, available_time = ? WHERE doctor_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setDoctorParameter(pstmt, doctor);
            pstmt.setInt(10, doctor.getDoctorId());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            AppLogger.error(getClass().getSimpleName(), "", "Error occurred", e);
            return false;
        }
    }

    /**
     * Delete doctor by ID
     */
    public boolean deleteDoctor(int doctorId) {
        String sql = "DELETE FROM doctors WHERE doctor_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            AppLogger.error(getClass().getSimpleName(), "", "Error occurred", e);
            return false;
        }
    }

    /**
     * Search doctors by name or specialization
     */
    public List<Doctor> searchDoctors(String searchTerm) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE name LIKE ? OR specialization LIKE ? ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Doctor doctor = new Doctor();
                rowMappingHelper(rs, doctor);
                doctors.add(doctor);
            }

        } catch (SQLException e) {
            AppLogger.error(getClass().getSimpleName(), "", "Error occurred", e);
        }

        return doctors;
    }

    /**
     * Get doctors by specialization
     */
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE specialization = ? ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, specialization);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Doctor doctor = new Doctor();
                rowMappingHelper(rs, doctor);
                doctors.add(doctor);
            }

        } catch (SQLException e) {
            AppLogger.error(getClass().getSimpleName(), "", "Error occurred", e);
        }

        return doctors;
    }
}
