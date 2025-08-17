package com.studentmanagement.ui;

import com.studentmanagement.dao.StudentDAO;
import com.studentmanagement.model.Student;
import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MainForm extends JFrame {
    private StudentDAO studentDAO;
    private StudentTableModel tableModel;
    private JTable studentTable;
    private JTextField txtSearch;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnExport;
    private JLabel lblTotal;
    
    public MainForm() {
        studentDAO = new StudentDAO();
        initComponents();
        setupLayout();
        setupEvents();
        refreshTable();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Hệ thống Quản lý Sinh viên");
        setSize(1200, 700);
        
        // Table
        tableModel = new StudentTableModel();
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setAutoCreateRowSorter(true);
        
        // Configure table appearance
        studentTable.setRowHeight(25);
        studentTable.getTableHeader().setReorderingAllowed(false);
        
        // Search components
        txtSearch = new JTextField(20);
        txtSearch.setToolTipText("Tìm kiếm theo mã SV, tên, lớp hoặc ngành");
        
        // Buttons
        btnAdd = new JButton("Thêm");
        btnEdit = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");
        btnExport = new JButton("Xuất Excel");
        
        // Status label
        lblTotal = new JLabel("Tổng số sinh viên: 0");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top panel - Search and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtSearch);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnExport);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Center panel - Table
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách sinh viên"));
        
        // Bottom panel - Status
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(lblTotal);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEvents() {
        // Search as you type
        txtSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        
        // Button events
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });
        
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editStudent();
            }
        });
        
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteStudent();
            }
        });
        
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });
        
        btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToExcel();
            }
        });
        
        // Double click to edit
        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editStudent();
                }
            }
        });
        
        // Enable/disable buttons based on selection
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = studentTable.getSelectedRow() >= 0;
            btnEdit.setEnabled(hasSelection);
            btnDelete.setEnabled(hasSelection);
        });
    }
    
    private void addStudent() {
        StudentDialog dialog = new StudentDialog(this, null, false);
        dialog.setVisible(true);
        
        if (dialog.getDialogResult()) {
            Student newStudent = dialog.getStudent();
            if (studentDAO.addStudent(newStudent)) {
                refreshTable();
                JOptionPane.showMessageDialog(this, 
                    "Thêm sinh viên thành công!", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Mã sinh viên đã tồn tại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn sinh viên cần sửa!", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Convert view index to model index
        int modelRow = studentTable.convertRowIndexToModel(selectedRow);
        Student student = tableModel.getStudentAt(modelRow);
        
        if (student != null) {
            StudentDialog dialog = new StudentDialog(this, student, true);
            dialog.setVisible(true);
            
            if (dialog.getDialogResult()) {
                Student updatedStudent = dialog.getStudent();
                if (studentDAO.updateStudent(updatedStudent)) {
                    refreshTable();
                    JOptionPane.showMessageDialog(this, 
                        "Cập nhật thông tin thành công!", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
    
    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn sinh viên cần xóa!", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = studentTable.convertRowIndexToModel(selectedRow);
        Student student = tableModel.getStudentAt(modelRow);
        
        if (student != null) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa sinh viên: " + student.getFullName() + "?", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (studentDAO.deleteStudent(student.getStudentId())) {
                    refreshTable();
                    JOptionPane.showMessageDialog(this, 
                        "Xóa sinh viên thành công!", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
    
    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        List<Student> results;
        
        if (keyword.isEmpty()) {
            results = studentDAO.getAllStudents();
        } else {
            results = studentDAO.searchStudents(keyword);
        }
        
        tableModel.setStudents(results);
        updateStatusLabel(results.size());
    }
    
    private void refreshTable() {
        txtSearch.setText("");
        List<Student> students = studentDAO.getAllStudents();
        tableModel.setStudents(students);
        updateStatusLabel(students.size());
    }
    
    private void updateStatusLabel(int count) {
        lblTotal.setText("Tổng số sinh viên: " + count);
    }
    
    private void exportToExcel() {
        // Simple CSV export implementation
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Xuất danh sách sinh viên");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String fileName = fileChooser.getSelectedFile().getAbsolutePath();
                if (!fileName.toLowerCase().endsWith(".csv")) {
                    fileName += ".csv";
                }
                
                java.io.FileWriter writer = new java.io.FileWriter(fileName);
                
                // Write header
                writer.write("Mã SV,Họ và tên,Ngày sinh,Giới tính,Địa chỉ,Điện thoại,Email,Lớp,Ngành,GPA,Xếp loại\n");
                
                // Write data
                List<Student> students = studentDAO.getAllStudents();
                for (Student student : students) {
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
                
                writer.close();
                JOptionPane.showMessageDialog(this, 
                    "Xuất file thành công: " + fileName, 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi xuất file: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }
}