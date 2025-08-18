package com.studentmanagement.dao;

import com.studentmanagement.model.Student;
import com.studentmanagement.database.DatabaseConnection;

// Database imports
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

// Date/Time imports
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// Collection imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// IO imports 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class StudentDAO implements StudentRepository {
    
    private static final String CSV_HEADER = "Mã SV,Họ và tên,Ngày sinh,Giới tính,Địa chỉ,Điện thoại,Email,Lớp,Ngành,GPA,Xếp loại";
    
    // ===== CRUD Repository Implementation =====
    
    @Override
    public boolean save(Student student) {
        if (existsById(student.getStudentId())) {
            return update(student);
        } else {
            return insert(student);
        }
    }
    
    private boolean insert(Student student) {
        String sql = "INSERT INTO students (student_id, full_name, birth_date, gender, address, phone, email, class_name, major, gpa) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getFullName());
            pstmt.setDate(3, student.getBirthDate() != null ? Date.valueOf(student.getBirthDate()) : null);
            pstmt.setString(4, student.getGender());
            pstmt.setString(5, student.getAddress());
            pstmt.setString(6, student.getPhone());
            pstmt.setString(7, student.getEmail());
            pstmt.setString(8, student.getClassName());
            pstmt.setString(9, student.getMajor());
            pstmt.setDouble(10, student.getGpa());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error inserting student: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeAll(conn, pstmt, null);
        }
    }
    
    @Override
    public Student findById(String studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding student by ID: " + e.getMessage());
        } finally {
            DatabaseConnection.closeAll(conn, pstmt, rs);
        }
        
        return null;
    }
    
    @Override
    public List<Student> findAll() {
        String sql = "SELECT * FROM students ORDER BY student_id";
        List<Student> students = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all students: " + e.getMessage());
        } finally {
            DatabaseConnection.closeAll(conn, pstmt, rs);
        }
        
        return students;
    }
    
    @Override
    public boolean update(Student student) {
        String sql = "UPDATE students SET full_name = ?, birth_date = ?, gender = ?, address = ?, phone = ?, email = ?, class_name = ?, major = ?, gpa = ? WHERE student_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, student.getFullName());
            pstmt.setDate(2, student.getBirthDate() != null ? Date.valueOf(student.getBirthDate()) : null);
            pstmt.setString(3, student.getGender());
            pstmt.setString(4, student.getAddress());
            pstmt.setString(5, student.getPhone());
            pstmt.setString(6, student.getEmail());
            pstmt.setString(7, student.getClassName());
            pstmt.setString(8, student.getMajor());
            pstmt.setDouble(9, student.getGpa());
            pstmt.setString(10, student.getStudentId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeAll(conn, pstmt, null);
        }
    }
    
    @Override
    public boolean deleteById(String studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeAll(conn, pstmt, null);
        }
    }
    
    @Override
    public boolean delete(Student student) {
        return deleteById(student.getStudentId());
    }
    
    @Override
    public boolean existsById(String studentId) {
        String sql = "SELECT COUNT(*) FROM students WHERE student_id = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, studentId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking student existence: " + e.getMessage());
        } finally {
            DatabaseConnection.closeAll(conn, pstmt, rs);
        }
        
        return false;
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM students";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting students: " + e.getMessage());
        } finally {
            DatabaseConnection.closeAll(conn, pstmt, rs);
        }
        
        return 0;
    }
    
    // ===== Enhanced Search Methods =====
    
    @Override
    public List<Student> searchByKeyword(String keyword) {
        String sql = "SELECT * FROM students WHERE student_id LIKE ? OR full_name LIKE ? OR class_name LIKE ? OR major LIKE ? OR address LIKE ? OR phone LIKE ? OR email LIKE ? ORDER BY student_id";
        List<Student> students = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            for (int i = 1; i <= 7; i++) {
                pstmt.setString(i, searchPattern);
            }
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching students: " + e.getMessage());
        } finally {
            DatabaseConnection.closeAll(conn, pstmt, rs);
        }
        
        return students;
    }
    
    @Override
    public List<Student> findByClassName(String className) {
        return findByField("class_name", className);
    }
    
    @Override
    public List<Student> findByMajor(String major) {
        return findByField("major", major);
    }
    
    @Override
    public List<Student> findByGender(String gender) {
        return findByField("gender", gender);
    }
    
    private List<Student> findByField(String fieldName, String value) {
        String sql = "SELECT * FROM students WHERE " + fieldName + " = ? ORDER BY student_id";
        List<Student> students = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, value);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding students by " + fieldName + ": " + e.getMessage());
        } finally {
            DatabaseConnection.closeAll(conn, pstmt, rs);
        }
        
        return students;
    }
    
    @Override
    public List<Student> findByGPARange(double minGpa, double maxGpa) {
        String sql = "SELECT * FROM students WHERE gpa BETWEEN ? AND ? ORDER BY gpa DESC";
        List<Student> students = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, minGpa);
            pstmt.setDouble(2, maxGpa);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding students by GPA range: " + e.getMessage());
        } finally {
            DatabaseConnection.closeAll(conn, pstmt, rs);
        }
        
        return students;
    }
    
    @Override
    public List<Student> findTopStudentsByGpa(int limit) {
        String sql = "SELECT TOP (?) * FROM students WHERE gpa > 0 ORDER BY gpa DESC, student_id ASC";
        List<Student> students = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding top students: " + e.getMessage());
        } finally {
            DatabaseConnection.closeAll(conn, pstmt, rs);
        }
        
        return students;
    }
    
    // ===== Enhanced Statistics cho SQL Server =====
    
    @Override
    public Map<String, Object> getAcademicStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            
            // Thống kê tổng quan
            pstmt = conn.prepareStatement("SELECT COUNT(*) as total, AVG(gpa) as avg_gpa, MAX(gpa) as max_gpa, MIN(gpa) as min_gpa FROM students");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                stats.put("total", rs.getInt("total"));
                stats.put("averageGpa", rs.getDouble("avg_gpa"));
                stats.put("maxGpa", rs.getDouble("max_gpa"));
                stats.put("minGpa", rs.getDouble("min_gpa"));
            }
            rs.close();
            pstmt.close();
            
            // Thống kê theo giới tính
            pstmt = conn.prepareStatement("SELECT gender, COUNT(*) as count FROM students GROUP BY gender");
            rs = pstmt.executeQuery();
            Map<String, Long> genderStats = new HashMap<>();
            while (rs.next()) {
                genderStats.put(rs.getString("gender"), rs.getLong("count"));
            }
            stats.put("genderStats", genderStats);
            rs.close();
            pstmt.close();
            
            // Thống kê theo xếp loại - Compatible với SQL Server
            String rankQuery = "SELECT " +
                "CASE " +
                "WHEN gpa >= 3.6 THEN 'Xuất sắc' " +
                "WHEN gpa >= 3.2 THEN 'Giỏi' " +
                "WHEN gpa >= 2.5 THEN 'Khá' " +
                "WHEN gpa >= 2.0 THEN 'Trung bình' " +
                "ELSE 'Yếu' " +
                "END as rank, COUNT(*) as count " +
                "FROM students " +
                "GROUP BY CASE " +
                "WHEN gpa >= 3.6 THEN 'Xuất sắc' " +
                "WHEN gpa >= 3.2 THEN 'Giỏi' " +
                "WHEN gpa >= 2.5 THEN 'Khá' " +
                "WHEN gpa >= 2.0 THEN 'Trung bình' " +
                "ELSE 'Yếu' END";
                
            pstmt = conn.prepareStatement(rankQuery);
            rs = pstmt.executeQuery();
            Map<String, Long> rankStats = new HashMap<>();
            while (rs.next()) {
                rankStats.put(rs.getString("rank"), rs.getLong("count"));
            }
            stats.put("rankStats", rankStats);
            rs.close();
            pstmt.close();
            
            // Thống kê theo lớp
            pstmt = conn.prepareStatement("SELECT class_name, COUNT(*) as count FROM students GROUP BY class_name");
            rs = pstmt.executeQuery();
            Map<String, Long> classStats = new HashMap<>();
            while (rs.next()) {
                classStats.put(rs.getString("class_name"), rs.getLong("count"));
            }
            stats.put("classStats", classStats);
            rs.close();
            pstmt.close();
            
            // Thống kê theo ngành
            pstmt = conn.prepareStatement("SELECT major, COUNT(*) as count FROM students GROUP BY major");
            rs = pstmt.executeQuery();
            Map<String, Long> majorStats = new HashMap<>();
            while (rs.next()) {
                majorStats.put(rs.getString("major"), rs.getLong("count"));
            }
            stats.put("majorStats", majorStats);
            
        } catch (SQLException e) {
            System.err.println("Error getting statistics: " + e.getMessage());
        } finally {
            DatabaseConnection.closeAll(conn, pstmt, rs);
        }
        
        return stats;
    }
    
    // ===== Helper Methods =====
    
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getString("student_id"));
        student.setFullName(rs.getString("full_name"));
        
        Date birthDate = rs.getDate("birth_date");
        if (birthDate != null) {
            student.setBirthDate(birthDate.toLocalDate());
        }
        
        student.setGender(rs.getString("gender"));
        student.setAddress(rs.getString("address"));
        student.setPhone(rs.getString("phone"));
        student.setEmail(rs.getString("email"));
        student.setClassName(rs.getString("class_name"));
        student.setMajor(rs.getString("major"));
        student.setGpa(rs.getDouble("gpa"));
        
        return student;
    }
    
    // ===== Enhanced CSV Import/Export  =====
    
    public ImportResult importFromCSV(String filePath) {
        ImportResult result = new ImportResult();
        List<String> errors = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }

                try {
                    Student student = parseCSVLine(line, lineNumber);
                    if (student != null) {
                        if (!existsById(student.getStudentId())) {
                            if (save(student)) {
                                result.successCount++;
                            } else {
                                result.failureCount++;
                                errors.add("Dòng " + lineNumber + ": Lỗi lưu sinh viên " + student.getStudentId());
                            }
                        } else {
                            result.duplicateCount++;
                            errors.add("Dòng " + lineNumber + ": Mã SV " + student.getStudentId() + " đã tồn tại");
                        }
                    } else {
                        result.failureCount++;
                        errors.add("Dòng " + lineNumber + ": Dữ liệu không hợp lệ");
                    }

                } catch (Exception e) {
                    result.failureCount++;
                    errors.add("Dòng " + lineNumber + ": " + e.getMessage());
                }
            }
            
            result.errors = errors;
            
        } catch (IOException e) {
            result.failureCount++;
            errors.add("Lỗi đọc file: " + e.getMessage());
            result.errors = errors;
        }

        return result;
    }
    
    private Student parseCSVLine(String line, int lineNumber) throws Exception {
        String[] rawData = line.split(",");
        
        if (rawData.length < 10) {
            throw new Exception("Không đủ cột dữ liệu (cần ít nhất 10 cột)");
        }
        
        Student student = new Student();
        
        // Mã sinh viên
        String studentId = rawData[0].trim();
        if (studentId.isEmpty()) {
            throw new Exception("Mã sinh viên không được để trống");
        }
        student.setStudentId(studentId);
        
        // Họ tên
        String fullName = rawData[1].trim();
        if (fullName.isEmpty()) {
            throw new Exception("Họ tên không được để trống");
        }
        student.setFullName(fullName);
        
        // Ngày sinh
        String birthDateStr = rawData[2].trim();
        if (!birthDateStr.isEmpty()) {
            try {
                LocalDate birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                student.setBirthDate(birthDate);
            } catch (Exception e) {
                throw new Exception("Ngày sinh không đúng định dạng dd/MM/yyyy");
            }
        }
        
        // Giới tính
        String gender = rawData[3].trim();
        if (!gender.equals("Nam") && !gender.equals("Nữ")) {
            throw new Exception("Giới tính phải là 'Nam' hoặc 'Nữ'");
        }
        student.setGender(gender);
        
        // Xử lý địa chỉ (có thể có dấu phẩy)
        int phoneIndex = findPhoneIndex(rawData);
        if (phoneIndex == -1) {
            throw new Exception("Không tìm thấy số điện thoại hợp lệ");
        }
        
        StringBuilder address = new StringBuilder();
        for (int i = 4; i < phoneIndex; i++) {
            if (address.length() > 0) address.append(", ");
            address.append(rawData[i].trim());
        }
        student.setAddress(address.toString());
        
        // Điện thoại
        String phone = rawData[phoneIndex].trim();
        if (!phone.matches("0\\d{9,10}")) {
            throw new Exception("Số điện thoại không hợp lệ");
        }
        student.setPhone(phone);
        
        // Email
        student.setEmail(rawData[phoneIndex + 1].trim());
        
        // Lớp
        String className = rawData[phoneIndex + 2].trim();
        if (className.isEmpty()) {
            throw new Exception("Tên lớp không được để trống");
        }
        student.setClassName(className);
        
        // Ngành
        student.setMajor(rawData[phoneIndex + 3].trim());
        
        // GPA
        String gpaStr = rawData[phoneIndex + 4].trim();
        if (!gpaStr.isEmpty()) {
            try {
                double gpa = Double.parseDouble(gpaStr);
                if (gpa < 0.0 || gpa > 4.0) {
                    throw new Exception("GPA phải từ 0.0 đến 4.0");
                }
                student.setGpa(gpa);
            } catch (NumberFormatException e) {
                throw new Exception("GPA không đúng định dạng số");
            }
        } else {
            student.setGpa(0.0);
        }
        
        return student;
    }
    
    private int findPhoneIndex(String[] data) {
        for (int i = 4; i < data.length; i++) {
            String field = data[i].trim();
            if (field.matches("0\\d{9,10}")) {
                return i;
            }
        }
        return -1;
    }
    
    // ===== Enhanced CSV Export =====
    
    public boolean exportToCSV(String filePath, List<Student> studentsToExport) {
        try (FileWriter writer = new FileWriter(filePath, false)) { // false = overwrite
            // Write BOM for UTF-8 (để hiển thị tiếng Việt đúng trong Excel)
            writer.write('\ufeff');
            writer.write(CSV_HEADER + "\n");
            
            for (Student student : studentsToExport) {
                String csvLine = String.format("%s,%s,%s,%s,\"%s\",%s,%s,%s,%s,%.2f,%s",
                    escapeCSVField(student.getStudentId()),
                    escapeCSVField(student.getFullName()),
                    student.getBirthDateString(),
                    escapeCSVField(student.getGender()),
                    escapeCSVField(student.getAddress()), // Quoted vì có thể chứa dấu phẩy
                    escapeCSVField(student.getPhone()),
                    escapeCSVField(student.getEmail()),
                    escapeCSVField(student.getClassName()),
                    escapeCSVField(student.getMajor()),
                    student.getGpa(),
                    escapeCSVField(student.getAcademyRank())
                );
                writer.write(csvLine + "\n");
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting to CSV: " + e.getMessage());
            return false;
        }
    }
    
    private String escapeCSVField(String field) {
        if (field == null) return "";
        // Escape quotes và wrap trong quotes nếu có dấu phẩy
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
    
    // ===== Batch Operations =====
    
    public BatchResult batchInsert(List<Student> students) {
        BatchResult result = new BatchResult();
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            String sql = "INSERT INTO students (student_id, full_name, birth_date, gender, address, phone, email, class_name, major, gpa) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            
            for (Student student : students) {
                try {
                    if (!existsById(student.getStudentId())) {
                        pstmt.setString(1, student.getStudentId());
                        pstmt.setString(2, student.getFullName());
                        pstmt.setDate(3, student.getBirthDate() != null ? Date.valueOf(student.getBirthDate()) : null);
                        pstmt.setString(4, student.getGender());
                        pstmt.setString(5, student.getAddress());
                        pstmt.setString(6, student.getPhone());
                        pstmt.setString(7, student.getEmail());
                        pstmt.setString(8, student.getClassName());
                        pstmt.setString(9, student.getMajor());
                        pstmt.setDouble(10, student.getGpa());
                        
                        pstmt.addBatch();
                        result.processedCount++;
                    } else {
                        result.duplicateCount++;
                    }
                } catch (Exception e) {
                    result.errors.add("Lỗi với SV " + student.getStudentId() + ": " + e.getMessage());
                }
            }
            
            int[] results = pstmt.executeBatch();
            conn.commit();
            
            for (int i : results) {
                if (i > 0) result.successCount++;
            }
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            result.errors.add("Lỗi batch insert: " + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DatabaseConnection.closeAll(conn, pstmt, null);
        }
        
        return result;
    }
    
    // ===== Data Sync Methods =====
    
    public SyncResult syncWithCSV(String csvFilePath) {
        SyncResult result = new SyncResult();
        
        try {
            // Đọc dữ liệu từ CSV
            ImportResult csvResult = importFromCSV(csvFilePath);
            result.csvImported = csvResult.successCount;
            result.csvErrors = csvResult.errors;
            
            // Đồng bộ với database
            List<Student> dbStudents = findAll();
            result.dbCount = dbStudents.size();
            
            result.totalAfterSync = count();
            result.success = true;
            
        } catch (Exception e) {
            result.success = false;
            result.csvErrors.add("Lỗi đồng bộ: " + e.getMessage());
        }
        
        return result;
    }
    
    // ===== Backup/Restore cho SQL Server =====
    
    public boolean backupData(String backupPath) {
        // Cho SQL Server, ta sẽ backup bằng cách export ra CSV thay vì database backup
        try {
            List<Student> allStudents = findAll();
            return exportToCSV(backupPath.replace(".dat", ".csv"), allStudents);
        } catch (Exception e) {
            System.err.println("Error creating backup: " + e.getMessage());
            return false;
        }
    }
    
    public boolean restoreData(String backupPath) {
        try {
            ImportResult result = importFromCSV(backupPath);
            return result.successCount > 0;
        } catch (Exception e) {
            System.err.println("Error restoring data: " + e.getMessage());
            return false;
        }
    }
    
    // ===== Connection Test =====
    
    public boolean testConnection() {
        return DatabaseConnection.testConnection();
    }
    
    // ===== Result Classes cho NetBeans 8.2 =====
    
    public static class ImportResult {
        public int successCount = 0;
        public int failureCount = 0;
        public int duplicateCount = 0;
        public List<String> errors = new ArrayList<>();
        
        public int getTotalProcessed() {
            return successCount + failureCount + duplicateCount;
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public String getSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append("Kết quả nhập dữ liệu:\n");
            sb.append("- Thành công: ").append(successCount).append("\n");
            sb.append("- Thất bại: ").append(failureCount).append("\n");
            sb.append("- Trùng lặp: ").append(duplicateCount).append("\n");
            if (hasErrors()) {
                sb.append("\nChi tiết lỗi:\n");
                for (String error : errors) {
                    sb.append("- ").append(error).append("\n");
                }
            }
            return sb.toString();
        }
    }
    
    public static class BatchResult {
        public int processedCount = 0;
        public int successCount = 0;
        public int duplicateCount = 0;
        public List<String> errors = new ArrayList<>();
        
        public boolean isSuccess() {
            return successCount > 0 && errors.isEmpty();
        }
    }
    
    public static class SyncResult {
        public boolean success = false;
        public int csvImported = 0;
        public int dbCount = 0;
        public long totalAfterSync = 0;
        public List<String> csvErrors = new ArrayList<>();
        
        public String getSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append("Kết quả đồng bộ:\n");
            sb.append("- Từ CSV: ").append(csvImported).append(" bản ghi\n");
            sb.append("- Từ DB: ").append(dbCount).append(" bản ghi\n");
            sb.append("- Tổng sau đồng bộ: ").append(totalAfterSync).append(" bản ghi\n");
            if (!csvErrors.isEmpty()) {
                sb.append("\nLỗi:\n");
                for (String error : csvErrors) {
                    sb.append("- ").append(error).append("\n");
                }
            }
            return sb.toString();
        }
    }
    
    // ===== Data Validation =====
    
    public ValidationResult validateStudent(Student student) {
        ValidationResult result = new ValidationResult();
        
        // Validate student ID
        if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            result.addError("Mã sinh viên không được để trống");
        } else if (!student.getStudentId().matches("^[A-Z0-9]{6,10}$")) {
            result.addWarning("Mã sinh viên nên có 6-10 ký tự chữ và số");
        }
        
        // Validate full name
        if (student.getFullName() == null || student.getFullName().trim().isEmpty()) {
            result.addError("Họ tên không được để trống");
        } else if (student.getFullName().length() < 2) {
            result.addError("Họ tên quá ngắn");
        }
        
        // Validate birth date
        if (student.getBirthDate() != null) {
            LocalDate now = LocalDate.now();
            LocalDate minDate = now.minusYears(100);
            LocalDate maxDate = now.minusYears(16);
            
            if (student.getBirthDate().isBefore(minDate)) {
                result.addError("Ngày sinh quá xa trong quá khứ");
            } else if (student.getBirthDate().isAfter(maxDate)) {
                result.addWarning("Sinh viên dưới 16 tuổi");
            }
        }
        
        // Validate phone
        if (student.getPhone() != null && !student.getPhone().isEmpty()) {
            if (!student.getPhone().matches("0\\d{9,10}")) {
                result.addError("Số điện thoại không đúng định dạng");
            }
        }
        
        // Validate email
        if (student.getEmail() != null && !student.getEmail().isEmpty()) {
            if (!student.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                result.addWarning("Email có thể không đúng định dạng");
            }
        }
        
        // Validate GPA
        if (student.getGpa() < 0.0 || student.getGpa() > 4.0) {
            result.addError("GPA phải từ 0.0 đến 4.0");
        }
        
        return result;
    }
    
    public static class ValidationResult {
        private List<String> errors = new ArrayList<>();
        private List<String> warnings = new ArrayList<>();
        
        public void addError(String error) {
            errors.add(error);
        }
        
        public void addWarning(String warning) {
            warnings.add(warning);
        }
        
        public boolean isValid() {
            return errors.isEmpty();
        }
        
        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }
        
        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }
        
        public List<String> getWarnings() {
            return new ArrayList<>(warnings);
        }
        
        public String getAllMessages() {
            StringBuilder sb = new StringBuilder();
            if (!errors.isEmpty()) {
                sb.append("Lỗi:\n");
                for (String error : errors) {
                    sb.append("- ").append(error).append("\n");
                }
            }
            if (!warnings.isEmpty()) {
                if (sb.length() > 0) sb.append("\n");
                sb.append("Cảnh báo:\n");
                for (String warning : warnings) {
                    sb.append("- ").append(warning).append("\n");
                }
            }
            return sb.toString();
        }
    }
    
    // ===== Enhanced Data Source Management =====
    
    public DataSourceInfo getDataSourceInfo() {
        DataSourceInfo info = new DataSourceInfo();
        
        try {
            info.databaseConnected = testConnection();
            if (info.databaseConnected) {
                info.databaseRecordCount = count();
            }
        } catch (Exception e) {
            info.databaseConnected = false;
            info.databaseError = e.getMessage();
        }
        
        return info;
    }
    
    public static class DataSourceInfo {
        public boolean databaseConnected = false;
        public long databaseRecordCount = 0;
        public String databaseError = null;
        
        public String getStatusSummary() {
            StringBuilder sb = new StringBuilder();
            sb.append("Trạng thái kết nối:\n");
            if (databaseConnected) {
                sb.append("✅ Database: Đã kết nối (").append(databaseRecordCount).append(" bản ghi)\n");
            } else {
                sb.append("❌ Database: Chưa kết nối");
                if (databaseError != null) {
                    sb.append(" - ").append(databaseError);
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    }
    
    // ===== Advanced Search với Filter =====
    
    public List<Student> advancedSearch(SearchCriteria criteria) {
        StringBuilder sql = new StringBuilder("SELECT * FROM students WHERE 1=1");
        List<Object> parameters = new ArrayList<>();
        
        if (criteria.studentId != null && !criteria.studentId.trim().isEmpty()) {
            sql.append(" AND student_id LIKE ?");
            parameters.add("%" + criteria.studentId.trim() + "%");
        }
        
        if (criteria.fullName != null && !criteria.fullName.trim().isEmpty()) {
            sql.append(" AND full_name LIKE ?");
            parameters.add("%" + criteria.fullName.trim() + "%");
        }
        
        if (criteria.className != null && !criteria.className.trim().isEmpty()) {
            sql.append(" AND class_name LIKE ?");
            parameters.add("%" + criteria.className.trim() + "%");
        }
        
        if (criteria.major != null && !criteria.major.trim().isEmpty()) {
            sql.append(" AND major LIKE ?");
            parameters.add("%" + criteria.major.trim() + "%");
        }
        
        if (criteria.gender != null && !criteria.gender.equals("Tất cả")) {
            sql.append(" AND gender = ?");
            parameters.add(criteria.gender);
        }
        
        if (criteria.minGpa != null) {
            sql.append(" AND gpa >= ?");
            parameters.add(criteria.minGpa);
        }
        
        if (criteria.maxGpa != null) {
            sql.append(" AND gpa <= ?");
            parameters.add(criteria.maxGpa);
        }
        
        sql.append(" ORDER BY student_id");
        
        return executeSearchQuery(sql.toString(), parameters);
    }
    
    private List<Student> executeSearchQuery(String sql, List<Object> parameters) {
        List<Student> students = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof String) {
                    pstmt.setString(i + 1, (String) param);
                } else if (param instanceof Double) {
                    pstmt.setDouble(i + 1, (Double) param);
                } else if (param instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) param);
                }
            }
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error executing search query: " + e.getMessage());
        } finally {
            DatabaseConnection.closeAll(conn, pstmt, rs);
        }
        
        return students;
    }
    
    public static class SearchCriteria {
        public String studentId;
        public String fullName;
        public String className;
        public String major;
        public String gender;
        public Double minGpa;
        public Double maxGpa;
        
        public SearchCriteria() {}
        
        public boolean isEmpty() {
            return (studentId == null || studentId.trim().isEmpty()) &&
                   (fullName == null || fullName.trim().isEmpty()) &&
                   (className == null || className.trim().isEmpty()) &&
                   (major == null || major.trim().isEmpty()) &&
                   (gender == null || "Tất cả".equals(gender)) &&
                   minGpa == null && maxGpa == null;
        }
    }
    
    // ===== Legacy Methods cho backward compatibility =====
    
    public boolean addStudent(Student student) {
        return save(student);
    }
    
    public boolean updateStudent(Student student) {
        return update(student);
    }
    
    public boolean deleteStudent(String studentId) {
        return deleteById(studentId);
    }
    
    public Student findByStudentId(String studentId) {
        return findById(studentId);
    }
    
    public List<Student> getAllStudents() {
        return findAll();
    }
    
    public List<Student> searchStudents(String keyword) {
        return searchByKeyword(keyword);
    }
    
    public List<Student> getStudentByClass(String className) {
        return findByClassName(className);
    }
    
    public List<Student> getStudentsByGpaRange(double minGpa, double maxGpa) {
        return findByGPARange(minGpa, maxGpa);
    }
    
    // ===== Database Connection Test =====
    
    public static void testDatabaseConnection() {
        if (DatabaseConnection.testConnection()) {
            System.out.println("✓ Database connection successful!");
        } else {
            System.err.println("✗ Database connection failed!");
            DatabaseConnection.printConfiguration();
        }
    }
    
    // ===== Cleanup =====
    
    public void shutdown() {
        // Cleanup resources if needed
        System.out.println("StudentDAO shutdown completed.");
    }
}