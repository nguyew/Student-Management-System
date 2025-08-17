package com.studentmanagement.dao;

import com.studentmanagement.model.Student;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StudentDAO implements StudentRepository {
    private static final String DATA_FILE = "students.dat";
    private List<Student> students;
    
    public StudentDAO() {
        students = new ArrayList<>();
        loadData();
    }
    
    // Load data from file
    @SuppressWarnings("unchecked")
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            students = (List<Student>) ois.readObject();
        } catch (FileNotFoundException e) {
            students = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
            students = new ArrayList<>();
        }
    }
    
    // Save data to file
    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(students);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }
    
    // ===== CRUD Repository Implementation =====
    
    @Override
    public boolean save(Student student) {
        if (existsById(student.getStudentId())) {
            return update(student);
        } else {
            students.add(student);
            saveData();
            return true;
        }
    }
    
    @Override
    public Student findById(String studentId) {
        return students.stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<Student> findAll() {
        return new ArrayList<>(students);
    }
    
    @Override
    public boolean update(Student student) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId().equals(student.getStudentId())) {
                students.set(i, student);
                saveData();
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean deleteById(String studentId) {
        boolean removed = students.removeIf(s -> s.getStudentId().equals(studentId));
        if (removed) saveData();
        return removed;
    }
    
    @Override
    public boolean delete(Student student) {
        return deleteById(student.getStudentId());
    }
    
    @Override
    public boolean existsById(String studentId) {
        return students.stream()
                .anyMatch(s -> s.getStudentId().equals(studentId));
    }
    
    @Override
    public long count() {
        return students.size();
    }
    
    // ===== Student-specific Repository Implementation =====
    
    @Override
    public List<Student> searchByKeyword(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return students.stream()
                .filter(s -> s.getStudentId().toLowerCase().contains(lowerKeyword) ||
                           s.getFullName().toLowerCase().contains(lowerKeyword) ||
                           s.getClassName().toLowerCase().contains(lowerKeyword) ||
                           s.getMajor().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Student> findByClassName(String className) {
        return students.stream()
                .filter(s -> s.getClassName().equals(className))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Student> findByMajor(String major) {
        return students.stream()
                .filter(s -> s.getMajor().equals(major))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Student> findByGPARange(double minGpa, double maxGpa) {
        return students.stream()
                .filter(s -> s.getGpa() >= minGpa && s.getGpa() <= maxGpa)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Student> findByGender(String gender) {
        return students.stream()
                .filter(s -> s.getGender().equals(gender))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Student> findTopStudentsByGpa(int limit) {
        return students.stream()
                .sorted((s1, s2) -> Double.compare(s2.getGpa(), s1.getGpa()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getAcademicStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total students
        stats.put("total", students.size());
        
        // Gender statistics
        Map<String, Long> genderStats = students.stream()
                .collect(Collectors.groupingBy(Student::getGender, Collectors.counting()));
        stats.put("genderStats", genderStats);
        
        // Academic rank statistics
        Map<String, Long> rankStats = students.stream()
                .collect(Collectors.groupingBy(Student::getAcademyRank, Collectors.counting()));
        stats.put("rankStats", rankStats);
        
        // Class statistics
        Map<String, Long> classStats = students.stream()
                .collect(Collectors.groupingBy(Student::getClassName, Collectors.counting()));
        stats.put("classStats", classStats);
        
        // Major statistics
        Map<String, Long> majorStats = students.stream()
                .collect(Collectors.groupingBy(Student::getMajor, Collectors.counting()));
        stats.put("majorStats", majorStats);
        
        // GPA statistics
        OptionalDouble avgGpa = students.stream().mapToDouble(Student::getGpa).average();
        double maxGpa = students.stream().mapToDouble(Student::getGpa).max().orElse(0.0);
        double minGpa = students.stream().mapToDouble(Student::getGpa).min().orElse(0.0);
        
        stats.put("averageGpa", avgGpa.orElse(0.0));
        stats.put("maxGpa", maxGpa);
        stats.put("minGpa", minGpa);
        
        return stats;
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
                
                String[] data = line.split(",");
                if (data.length >= 9) { // Minimum required fields
                    try {
                        Student student = new Student();
                        student.setStudentId(data[0].trim());
                        student.setFullName(data[1].trim());
                        
                        // Parse birth date
                        if (!data[2].trim().isEmpty()) {
                            student.setBirthDate(LocalDate.parse(data[2].trim(), 
                                DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                        }
                        
                        student.setGender(data[3].trim());
                        student.setAddress(data[4].trim());
                        student.setPhone(data[5].trim());
                        student.setEmail(data[6].trim());
                        student.setClassName(data[7].trim());
                        student.setMajor(data[8].trim());
                        
                        // Parse GPA
                        if (data.length > 9 && !data[9].trim().isEmpty()) {
                            student.setGpa(Double.parseDouble(data[9].trim()));
                        } else {
                            student.setGpa(0.0);
                        }
                        
                        // Check if student already exists
                        if (!existsById(student.getStudentId())) {
                            newStudents.add(student);
                            importedCount++;
                        }
                        
                    } catch (Exception e) {
                        // Skip invalid rows
                        System.err.println("Error parsing row: " + line + " - " + e.getMessage());
                    }
                }
            }
        }
        
        // Add all valid students
        students.addAll(newStudents);
        saveData();
        
        return importedCount;
    }
    
    public boolean exportToCSV(String filePath, List<Student> studentsToExport) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write header
            writer.write("Mã SV,Họ và tên,Ngày sinh,Giới tính,Địa chỉ,Điện thoại,Email,Lớp,Ngành,GPA,Xếp loại\n");
            
            // Write data
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
        } catch (IOException e) {
            System.err.println("Error exporting to CSV: " + e.getMessage());
            return false;
        }
    }
    
    public boolean backupData(String backupPath) {
        try {
            // Create backup directory if not exists
            File backupFile = new File(backupPath);
            backupFile.getParentFile().mkdirs();
            
            // Copy current data file
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(backupPath))) {
                oos.writeObject(students);
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error creating backup: " + e.getMessage());
            return false;
        }
    }
    
    @SuppressWarnings("unchecked")
    public boolean restoreData(String backupPath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(backupPath))) {
            List<Student> backupStudents = (List<Student>) ois.readObject();
            students.clear();
            students.addAll(backupStudents);
            saveData();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error restoring backup: " + e.getMessage());
            return false;
        }
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
}