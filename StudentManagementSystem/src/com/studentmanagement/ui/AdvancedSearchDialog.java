package com.studentmanagement.ui;

import com.studentmanagement.dao.StudentDAO;
import com.studentmanagement.model.Student;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class AdvancedSearchDialog extends JDialog {
    private StudentDAO studentDAO;
    private JTextField txtStudentId, txtName, txtClassName, txtMajor, txtMinGpa, txtMaxGpa;
    private JComboBox<String> cmbGender;
    private JButton btnSearch, btnClear, btnCancel;
    private List<Student> searchResult;
    
    public AdvancedSearchDialog(Frame parent, StudentDAO studentDAO) {
        super(parent, "Tìm kiếm nâng cao", true);
        this.studentDAO = studentDAO;
        initComponents();
        setupLayout();
        setupEvents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        txtStudentId = new JTextField(15);
        txtName = new JTextField(20);
        txtClassName = new JTextField(15);
        txtMajor = new JTextField(20);
        txtMinGpa = new JTextField(10);
        txtMaxGpa = new JTextField(10);
        
        cmbGender = new JComboBox<>(new String[]{"Tất cả", "Nam", "Nữ"});
        
        btnSearch = new JButton("Tìm kiếm");
        btnClear = new JButton("Xóa");
        btnCancel = new JButton("Hủy");
        
        // Set tooltips
        txtMinGpa.setToolTipText("Điểm GPA tối thiểu (0.0 - 4.0)");
        txtMaxGpa.setToolTipText("Điểm GPA tối đa (0.0 - 4.0)");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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
        mainPanel.add(txtName, gbc);
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Lớp:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(txtClassName, gbc);
        
        gbc.gridx = 2;
        mainPanel.add(new JLabel("Ngành:"), gbc);
        gbc.gridx = 3;
        mainPanel.add(txtMajor, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(cmbGender, gbc);
        
        // Row 3
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("GPA từ:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(txtMinGpa, gbc);
        
        gbc.gridx = 2;
        mainPanel.add(new JLabel("đến:"), gbc);
        gbc.gridx = 3;
        mainPanel.add(txtMaxGpa, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnCancel);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
    }
    
    private void setupEvents() {
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAdvancedSearch();
            }
        });
        
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });
        
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void performAdvancedSearch() {
        try {
            List<Student> allStudents = studentDAO.findAll();
            List<Student> results = new ArrayList<>();
            
            for (Student student : allStudents) {
                boolean match = true;
                
                // Check student ID
                if (!txtStudentId.getText().trim().isEmpty()) {
                    if (!student.getStudentId().toLowerCase()
                            .contains(txtStudentId.getText().trim().toLowerCase())) {
                        match = false;
                    }
                }
                
                // Check name
                if (!txtName.getText().trim().isEmpty()) {
                    if (!student.getFullName().toLowerCase()
                            .contains(txtName.getText().trim().toLowerCase())) {
                        match = false;
                    }
                }
                
                // Check class name
                if (!txtClassName.getText().trim().isEmpty()) {
                    if (!student.getClassName().toLowerCase()
                            .contains(txtClassName.getText().trim().toLowerCase())) {
                        match = false;
                    }
                }
                
                // Check major
                if (!txtMajor.getText().trim().isEmpty()) {
                    if (!student.getMajor().toLowerCase()
                            .contains(txtMajor.getText().trim().toLowerCase())) {
                        match = false;
                    }
                }
                
                // Check gender
                String selectedGender = (String) cmbGender.getSelectedItem();
                if (!"Tất cả".equals(selectedGender)) {
                    if (!student.getGender().equals(selectedGender)) {
                        match = false;
                    }
                }
                
                // Check GPA range
                if (!txtMinGpa.getText().trim().isEmpty()) {
                    double minGpa = Double.parseDouble(txtMinGpa.getText().trim());
                    if (student.getGpa() < minGpa) {
                        match = false;
                    }
                }
                
                if (!txtMaxGpa.getText().trim().isEmpty()) {
                    double maxGpa = Double.parseDouble(txtMaxGpa.getText().trim());
                    if (student.getGpa() > maxGpa) {
                        match = false;
                    }
                }
                
                if (match) {
                    results.add(student);
                }
            }
            
            searchResult = results;
            
            JOptionPane.showMessageDialog(this, 
                "Tìm thấy " + results.size() + " sinh viên phù hợp!", 
                "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Giá trị GPA không hợp lệ!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tìm kiếm: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearFields() {
        txtStudentId.setText("");
        txtName.setText("");
        txtClassName.setText("");
        txtMajor.setText("");
        txtMinGpa.setText("");
        txtMaxGpa.setText("");
        cmbGender.setSelectedIndex(0);
    }
    
    public List<Student> getSearchResult() {
        return searchResult;
    }
}