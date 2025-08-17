package com.studentmanagement.dao;

import com.studentmanagement.model.Student;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class StudentDAO implements StudentRepository {
    private static final String DATA_FILE = "student.dat";
    private List<Student> students;
    
    public StudentDAO () {
        students = new ArrayList<>();
        loadData();
    }
    
    // Load data from file
    @SuppressWarnings("unchecked")
    private void loadData () {
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
    public void saveData () {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(students);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    @Override
    public List<Student> searchByKeyword(String keyword) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Student> findByClassName(String className) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Student> findByMajor(String major) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Student> findByGPARange(double minGpa, double maxGpa) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Student> findByGender(String gender) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Student> findTopStudentsByGpa(int limit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, Object> getAcademicStatistics() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    // CRUD Operation
    public boolean addStudent (Student student) {
        if (findByStudentId(student.getStudentId()) != null) {
            return false;
        }
        students.add(student);
        saveData();
        return true;
    }
    
    public boolean updateStudent (Student student) {
        for(int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId().equals(student.getStudentId())) {
                students.set(i, student);
                saveData();
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteStudent (String studentId) {
        boolean removed = students.removeIf(s -> s.getStudentId().equals(studentId));
        if (removed) saveData();
        return removed;
    }
    
    public Student findByStudentId (String studentId) {
        return students.stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .findFirst()
                .orElse(null);
    }
    
    public List<Student> getAllStudents () {
        return new ArrayList<>(students);
    }
    
    public List<Student> searchStudents (String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return students.stream()
                .filter(s -> s.getStudentId().toLowerCase().contains(lowerKeyword) ||
                           s.getFullName().toLowerCase().contains(lowerKeyword) ||
                           s.getClassName().toLowerCase().contains(lowerKeyword) ||
                           s.getMajor().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }
    
    public List<Student> getStudentByClass (String className) {
        return students.stream()
                .filter(s -> s.getClassName().equals(className))
                .collect(Collectors.toList());
    }
    
    public List<Student> getStudentsByGpaRange (double minGpa, double maxGpa) {
        return students.stream()
                .filter(s -> s.getGpa() >= minGpa && s.getGpa() <= maxGpa)
                .collect(Collectors.toList());
    }
    @Override
    public boolean save(Student entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Student findById(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Student> findAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean update(Student entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deleteById(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean delete(Student entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean existsById(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
