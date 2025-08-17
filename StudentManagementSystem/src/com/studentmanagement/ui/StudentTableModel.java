package com.studentmanagement.ui;

import com.studentmanagement.model.Student;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class StudentTableModel extends AbstractTableModel {
    private final String[] columnNames = {
        "Mã SV", "Họ và tên", "Ngày sinh", "Giới tính", 
        "Lớp", "Ngành", "GPA", "Xếp loại"
    };
    
    private List<Student> students;
    
    public StudentTableModel () {
        this.students = new ArrayList<>();
    }
    
    public StudentTableModel (List<Student> students) {
        this.students = students != null ? students : new ArrayList<>();
    }
            
    @Override
    public int getRowCount() {
        return students.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
       Student student = students.get(rowIndex);
       switch (columnIndex) {
            case 0: return student.getStudentId();
            case 1: return student.getFullName();
            case 2: return student.getBirthDateString();
            case 3: return student.getGender();
            case 4: return student.getClassName();
            case 5: return student.getMajor();
            case 6: return String.format("%.2f", student.getGpa());
            case 7: return student.getAcademyRank();
            default: return null; 
        }
    }
    
    public void setStudents (List<Student> students) {
        this.students = students != null ? students : new ArrayList<>();
        fireTableDataChanged();
    }
    
    public Student getStudentAt (int rowIndex) {
        if (rowIndex >= 0 && rowIndex < students.size()) {
            return students.get(rowIndex);
        }
        return null;
    }
    
    public void addStudent (Student student) {
        students.add(student);
        fireTableRowsInserted(students.size() - 1, students.size() -1);
    }
    
    public void removeStudent (int rowIndex) {
        if (rowIndex >= 0 && rowIndex < students.size()) {
            students.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }
    
    public void updateStudent (int rowIndex, Student student) {
        if (rowIndex >= 0 && rowIndex < students.size()) {
            students.set(rowIndex, student);
            fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }     
}
    
