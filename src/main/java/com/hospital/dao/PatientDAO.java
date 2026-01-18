import com.hospital.model.Patient;
import com.hospital.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
// need to rollback 
/**
 * Data Access Object for Patient operations with Pagination support
 */
public class PatientDAO {

    private void rowMappingHelper(ResultSet rs, Patient patient) throws SQLException {
        patient.setPatientId(rs.getInt("patient_id"));
        patient.setName(rs.getString("name"));
        patient.setAge(rs.getInt("age"));
        patient.setGender(rs.getString("gender"));
        patient.setPhone(rs.getString("phone"));
        patient.setEmail(rs.getString("email"));
        patient.setAddress(rs.getString("address"));
        patient.setDisease(rs.getString("disease"));
        patient.setBloodGroup(rs.getString("blood_group"));
        patient.setEmergencyContact(rs.getString("emergency_contact"));
    }

    private void setPatientParameters(PreparedStatement pstmt, Patient patient) throws SQLException {
        pstmt.setString(1, patient.getName());
        pstmt.setInt(2, patient.getAge());
        pstmt.setString(3, patient.getGender());
        pstmt.setString(4, patient.getPhone());
        pstmt.setString(5, patient.getEmail());
        pstmt.setString(6, patient.getAddress());
        pstmt.setString(7, patient.getDisease());
        pstmt.setString(8, patient.getBloodGroup());
        pstmt.setString(9, patient.getEmergencyContact());
        pstmt.setDate(10, patient.getAdmissionDate() != null ? Date.valueOf(patient.getAdmissionDate())
                : Date.valueOf(LocalDate.now()));

    }

    /**
     * Get a paginated list of patients (New for Pagination)
     */
    public List<Patient> getPatientsPaginated(int limit, int offset) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY patient_id DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    /**
     * Get total count of patients (New for Pagination)
     */
    public int getTotalPatientCount() {
        String sql = "SELECT COUNT(*) FROM patients";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get all patients (Restored to fix the compilation error)
     */
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY patient_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    /**
     * Helper method to map ResultSet to Patient object
     */
    private Patient extractPatientFromResultSet(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setPatientId(rs.getInt("patient_id"));
        patient.setName(rs.getString("name"));
        patient.setAge(rs.getInt("age"));
        patient.setGender(rs.getString("gender"));
        patient.setPhone(rs.getString("phone"));
        patient.setEmail(rs.getString("email"));
        patient.setAddress(rs.getString("address"));
        patient.setDisease(rs.getString("disease"));
        patient.setBloodGroup(rs.getString("blood_group"));
        patient.setEmergencyContact(rs.getString("emergency_contact"));
        
        Date admissionDate = rs.getDate("admission_date");
        if (admissionDate != null) {
            patient.setAdmissionDate(admissionDate.toLocalDate());
        }
        return patient;
    }

    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO patients (name, age, gender, phone, email, address, disease, " +
                    "blood_group, emergency_contact, admission_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patient.getName());
            pstmt.setInt(2, patient.getAge());
            pstmt.setString(3, patient.getGender());
            pstmt.setString(4, patient.getPhone());
            pstmt.setString(5, patient.getEmail());
            pstmt.setString(6, patient.getAddress());
            pstmt.setString(7, patient.getDisease());
            pstmt.setString(8, patient.getBloodGroup());
            pstmt.setString(9, patient.getEmergencyContact());
            pstmt.setDate(10, patient.getAdmissionDate() != null ? 
                         Date.valueOf(patient.getAdmissionDate()) : Date.valueOf(LocalDate.now()));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Patient getPatientById(int patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return extractPatientFromResultSet(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET name = ?, age = ?, gender = ?, phone = ?, email = ?, " +
                    "address = ?, disease = ?, blood_group = ?, emergency_contact = ?, " +
                    "admission_date = ? WHERE patient_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patient.getName());
            pstmt.setInt(2, patient.getAge());
            pstmt.setString(3, patient.getGender());
            pstmt.setString(4, patient.getPhone());
            pstmt.setString(5, patient.getEmail());
            pstmt.setString(6, patient.getAddress());
            pstmt.setString(7, patient.getDisease());
            pstmt.setString(8, patient.getBloodGroup());
            pstmt.setString(9, patient.getEmergencyContact());
            pstmt.setDate(10, patient.getAdmissionDate() != null ? 
                         Date.valueOf(patient.getAdmissionDate()) : null);
            pstmt.setInt(11, patient.getPatientId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePatient(int patientId) {
        String sql = "DELETE FROM patients WHERE patient_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Patient> searchPatients(String searchTerm) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE name LIKE ? OR phone LIKE ? ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String pattern = "%" + searchTerm + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) patients.add(extractPatientFromResultSet(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }
}