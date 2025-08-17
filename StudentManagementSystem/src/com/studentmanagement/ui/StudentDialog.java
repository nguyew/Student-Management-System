package com.studentmanagement.ui;

import com.studentmanagement.model.Student;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StudentDialog extends JDialog {
   private JTextField txtStudentId, txtFullName, txtBirthDate, txtAddress, 
                       txtPhone, txtEmail, txtMajor, txtGpa, txtClassName;
    private JComboBox<String> cmbGender;
    private JButton btnSave, btnCancel;
    private Student student;
    private boolean isEdit;
    private boolean dialogResult = false;
    
    public StudentDialog (Frame parent, Student student, boolean isEdit) {
        super(parent, isEdit ? "Sửa thông tin sinh viên" : "Thêm sinh viên mới", true);
        this.student = student;
        this.isEdit = isEdit;
        initComponents();
        setupLayout();
        setupEvents();
        loadStudentData();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        txtStudentId = new JTextField(15);
        txtFullName = new JTextField(20);
        txtBirthDate = new JTextField(10);
        txtAddress = new JTextField(30);
        txtPhone = new JTextField(15);
        txtEmail = new JTextField(20);
        txtMajor = new JTextField(20);
        txtGpa = new JTextField(10);
        txtClassName = new JTextField(15);
        
        cmbGender = new JComboBox<>(new String[]{"Nam", "Nữ"});
        
        btnSave = new JButton("Lưu");
        btnCancel = new JButton("Hủy");
        
        // Set tooltips
        txtBirthDate.setToolTipText("Định dạng: dd/MM/yyyy (VD: 15/03/2000)");
        txtGpa.setToolTipText("Điểm GPA từ 0.0 đến 4.0");
        
        if (isEdit) {
            txtStudentId.setEditable(false);
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 0
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Mã sinh viên:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(txtStudentId, gbc);
        
        gbc.gridx = 2;
        mainPanel.add(new JLabel("Họ và tên:"), gbc);
        gbc.gridx = 3;
        mainPanel.add(txtFullName, gbc);
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Ngày sinh:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(txtBirthDate, gbc);
        
        gbc.gridx = 2;
        mainPanel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 3;
        mainPanel.add(cmbGender, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(txtAddress, gbc);
        
        // Row 3
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(txtPhone, gbc);
        
        gbc.gridx = 2;
        mainPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3;
        mainPanel.add(txtEmail, gbc);
        
        // Row 4
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Ngành:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(txtMajor, gbc);
        
        gbc.gridx = 2;
        mainPanel.add(new JLabel("GPA:"), gbc);
        gbc.gridx = 3;
        mainPanel.add(txtGpa, gbc);
        
        // Row 5
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("Lớp:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(txtClassName, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
    }
    
    private void setupEvents() {
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateAndSaveStudent()) {
                    dialogResult = true;
                    dispose();
                }
            }
        });
        
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void loadStudentData() {
        if (student != null) {
            txtStudentId.setText(student.getStudentId());
            txtFullName.setText(student.getFullName());
            txtBirthDate.setText(student.getBirthDateString());
            cmbGender.setSelectedItem(student.getGender());
            txtAddress.setText(student.getAddress());
            txtPhone.setText(student.getPhone());
            txtEmail.setText(student.getEmail());
            txtMajor.setText(student.getMajor());
            txtGpa.setText(String.valueOf(student.getGpa()));
            txtClassName.setText(student.getClassName());
        }
    }
    
    private boolean validateAndSaveStudent() {
        // Validate required fields
        if (txtStudentId.getText().trim().isEmpty() ||
            txtFullName.getText().trim().isEmpty() ||
            txtBirthDate.getText().trim().isEmpty() ||
            txtClassName.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "Vui lòng điền đầy đủ thông tin bắt buộc!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate birth date
        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(txtBirthDate.getText().trim(), 
                                      DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, 
                "Ngày sinh không hợp lệ! Định dạng: dd/MM/yyyy", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate GPA
        double gpa;
        try {
            gpa = Double.parseDouble(txtGpa.getText().trim());
            if (gpa < 0.0 || gpa > 4.0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "GPA phải là số từ 0.0 đến 4.0!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Create or update student object
        if (student == null) {
            student = new Student();
        }
        
        student.setStudentId(txtStudentId.getText().trim());
        student.setFullName(txtFullName.getText().trim());
        student.setBirthDate(birthDate);
        student.setGender((String) cmbGender.getSelectedItem());
        student.setAddress(txtAddress.getText().trim());
        student.setPhone(txtPhone.getText().trim());
        student.setEmail(txtEmail.getText().trim());
        student.setMajor(txtMajor.getText().trim());
        student.setGpa(gpa);
        student.setClassName(txtClassName.getText().trim());
        
        return true;
    }
    
    public Student getStudent() {
        return student;
    }
    
    public boolean getDialogResult() {
        return dialogResult;
    }
}
