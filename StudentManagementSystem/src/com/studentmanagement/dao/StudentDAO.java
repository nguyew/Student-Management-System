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

public class StudentDAO implements StudentRepository {
    
    // ===== CRUD Repository Implementation với Database =====
    
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
        String sql = "UPDATE students SET full_name = ?, birth_date = ?, gender = ?, address = ?, phone = ?, email = ?, class_name = ?, major = ?, gpa = ?, updated_date = GETDATE() WHERE student_id = ?";
        
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
    
    // ===== Student-specific Repository Implementation =====
    
    @Override
    public List<Student> searchByKeyword(String keyword) {
        String sql = "SELECT * FROM students WHERE student_id LIKE ? OR full_name LIKE ? OR class_name LIKE ? OR major LIKE ? ORDER BY student_id";
        List<Student> students = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
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
        String sql = "SELECT * FROM students WHERE class_name = ? ORDER BY student_id";
        List<Student> students = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, className);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding students by class: " + e.getMessage());
        } finally {
            DatabaseConnection.closeAll(conn, pstmt, rs);
        }
        
        return students;
    }
    
    @Override
    public List<Student> findByMajor(String major) {
        String sql = "SELECT * FROM students WHERE major = ? ORDER BY student_id";
        List<Student> students = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, major);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding students by major: " + e.getMessage());
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
    public List<Student> findByGender(String gender) {
        String sql = "SELECT * FROM students WHERE gender = ? ORDER BY student_id";
        List<Student> students = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, gender);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error finding students by gender: " + e.getMessage());
        } finally {
            DatabaseConnection.closeAll(conn, pstmt, rs);
        }
        
        return students;
    }
    
    @Override
    public List<Student> findTopStudentsByGpa(int limit) {
        String sql = "SELECT TOP (?) * FROM students ORDER BY gpa DESC";
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
            
            // Thống kê theo xếp loại
            pstmt = conn.prepareStatement("SELECT CASE WHEN gpa >= 3.6 THEN N'Xuất sắc' WHEN gpa >= 3.2 THEN N'Giỏi' WHEN gpa >= 2.5 THEN N'Khá' WHEN gpa >= 2.0 THEN N'Trung bình' ELSE N'Yếu' END as rank, COUNT(*) as count FROM students GROUP BY CASE WHEN gpa >= 3.6 THEN N'Xuất sắc' WHEN gpa >= 3.2 THEN N'Giỏi' WHEN gpa >= 2.5 THEN N'Khá' WHEN gpa >= 2.0 THEN N'Trung bình' ELSE N'Yếu' END");
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
    
    /**
     * Map ResultSet to Student object
     */
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
    
    // ===== Import/Export Operations =====
    
    public int importFromCSV(String filePath) throws Exception {
        int importedCount = 0;
        List<Student> newStudents = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }

                try {
                    String[] rawData = line.split(",");

                    if (rawData.length >= 11) {
                        String[] data = new String[11];
                        data[0] = rawData[0].trim(); // Mã SV
                        data[1] = rawData[1].trim(); // Tên
                        data[2] = rawData[2].trim(); // Ngày sinh  
                        data[3] = rawData[3].trim(); // Giới tính

                        int phoneIndex = findPhoneIndex(rawData);
                        if (phoneIndex == -1) {
                            System.err.println("Cannot find phone number in row: " + line);
                            continue;
                        }

                        StringBuilder address = new StringBuilder();
                        for (int i = 4; i < phoneIndex; i++) {
                            if (address.length() > 0) address.append(", ");
                            address.append(rawData[i].trim());
                        }
                        data[4] = address.toString(); // Địa chỉ

                        data[5] = rawData[phoneIndex].trim();     // Phone
                        data[6] = rawData[phoneIndex + 1].trim(); // Email
                        data[7] = rawData[phoneIndex + 2].trim(); // Lớp
                        data[8] = rawData[phoneIndex + 3].trim(); // Ngành
                        data[9] = rawData[phoneIndex + 4].trim(); // GPA
                        data[10] = rawData[phoneIndex + 5].trim(); // Xếp loại

                        Student student = new Student();
                        student.setStudentId(data[0]);
                        student.setFullName(data[1]);

                        if (!data[2].isEmpty()) {
                            student.setBirthDate(LocalDate.parse(data[2], 
                                DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                        }

                        student.setGender(data[3]);
                        student.setAddress(data[4]);
                        student.setPhone(data[5]);
                        student.setEmail(data[6]);
                        student.setClassName(data[7]);
                        student.setMajor(data[8]);

                        if (!data[9].isEmpty()) {
                            try {
                                double gpa = Double.parseDouble(data[9]);
                                student.setGpa(gpa);
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid GPA: " + data[9]);
                                student.setGpa(0.0);
                            }
                        } else {
                            student.setGpa(0.0);
                        }

                        if (!existsById(student.getStudentId())) {
                            if (save(student)) {
                                importedCount++;
                            }
                        }

                    } else {
                        System.err.println("Not enough columns in row: " + line);
                    }

                } catch (Exception e) {
                    System.err.println("Error parsing row: " + line + " - " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        return importedCount;
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
    
    public boolean exportToCSV(String filePath, List<Student> studentsToExport) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Mã SV,Họ và tên,Ngày sinh,Giới tính,Địa chỉ,Điện thoại,Email,Lớp,Ngành,GPA,Xếp loại\n");
            
            for (Student student : studentsToExport) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%.2f,%s\n",
                    student.getStudentId(),
                    student.getFullName(),
                    student.getBirthDateString(),
                    student.getGender(),
                    student.getAddress(),
                    student.getPhone(),
                    student.getEmail(),
                    student.getClassName(),
                    student.getMajor(),
                    student.getGpa(),
                    student.getAcademyRank()
                ));
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error exporting to CSV: " + e.getMessage());
            return false;
        }
    }
    
    public boolean backupData(String backupPath) {
        String sql = "BACKUP DATABASE StudentManagementDB TO DISK = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, backupPath);
            pstmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error creating backup: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnection.closeAll(conn, pstmt, null);
        }
    }
    
    public boolean restoreData(String backupPath) {
        System.out.println("Restore operation requires administrative privileges");
        return false;
    }
    
    // ===== Legacy methods for backward compatibility =====
    
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
}