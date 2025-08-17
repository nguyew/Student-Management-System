package com.studentmanagement.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String studentId;
    private String fullName;
    private LocalDate birthDate;
    private String gender;
    private String address;
    private String phone;
    private String email;
    private String major;
    private double gpa;
    private String className;
    
    public Student () {
        
    }

    public Student(String studentId, String fullName, LocalDate birthDate, String gender, String address, String phone, String email, String major, double gpa, String className) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.major = major;
        this.gpa = gpa;
        this.className = className;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
    
    // Utility methods
    public String getBirthDateString () {
        return birthDate != null ? birthDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }
    
    public String getAcademyRank () {
        if (gpa >= 3.6) return "Xuất sắc";
        if (gpa >= 3.2) return "Giỏi";
        if (gpa >= 2.5) return "Khá";
        if (gpa >= 2.0) return "Trung bình";
        return "Yếu";
    }
    
    public String toString (){
        return String.format("%s - %s - %s", studentId, fullName, className);
    }
}
