package com.studentmanagement.ui;

import com.studentmanagement.dao.StudentDAO;
import com.studentmanagement.model.Student;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.io.File;

public class MainForm extends JFrame {
    private StudentDAO studentDAO;
    private StudentTableModel tableModel;
    private JTable studentTable;
    private JTextField txtSearch;
    private JComboBox<String> cmbSortBy;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnExport, btnImport, 
                    btnStatistics, btnBackup, btnRestore, btnAdvancedSearch;
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
        
        // Sort combo box
        cmbSortBy = new JComboBox<>(new String[]{
            "Không sắp xếp", "Mã SV (A→Z)", "Mã SV (Z→A)", 
            "Tên (A→Z)", "Tên (Z→A)", "GPA (Cao→Thấp)", "GPA (Thấp→Cao)",
            "Lớp (A→Z)", "Ngành (A→Z)"
        });
        
        // Buttons
        btnAdd = new JButton("Thêm");
        btnEdit = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");
        btnExport = new JButton("Xuất CSV");
        btnImport = new JButton("Nhập CSV");
        btnStatistics = new JButton("Thống kê");
        btnBackup = new JButton("Sao lưu");
        btnRestore = new JButton("Khôi phục");
        btnAdvancedSearch = new JButton("Tìm kiếm nâng cao");
        
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
        searchPanel.add(new JLabel("Sắp xếp:"));
        searchPanel.add(cmbSortBy);
        searchPanel.add(btnAdvancedSearch);
        
        // Button panel 1
        JPanel buttonPanel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel1.add(btnAdd);
        buttonPanel1.add(btnEdit);
        buttonPanel1.add(btnDelete);
        buttonPanel1.add(btnRefresh);
        
        // Button panel 2  
        JPanel buttonPanel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel2.add(btnImport);
        buttonPanel2.add(btnExport);
        buttonPanel2.add(btnStatistics);
        buttonPanel2.add(btnBackup);
        buttonPanel2.add(btnRestore);
        
        // Combine button panels
        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.add(buttonPanel1, BorderLayout.NORTH);
        buttonContainer.add(buttonPanel2, BorderLayout.SOUTH);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonContainer, BorderLayout.EAST);
        
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
        
        // Sort combo box event
        cmbSortBy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applySorting();
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
                exportToCSV();
            }
        });
        
        btnImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importFromCSV();
            }
        });
        
        btnStatistics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showStatistics();
            }
        });
        
        btnBackup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backupData();
            }
        });
        
        btnRestore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restoreData();
            }
        });
        
        btnAdvancedSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAdvancedSearch();
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
    
    private void applySorting() {
        List<Student> students = new ArrayList<>(tableModel.getStudents());
        String sortOption = (String) cmbSortBy.getSelectedItem();
        
        switch (sortOption) {
            case "Mã SV (A→Z)":
                students.sort((s1, s2) -> s1.getStudentId().compareToIgnoreCase(s2.getStudentId()));
                break;
            case "Mã SV (Z→A)":
                students.sort((s1, s2) -> s2.getStudentId().compareToIgnoreCase(s1.getStudentId()));
                break;
            case "Tên (A→Z)":
                students.sort((s1, s2) -> s1.getFullName().compareToIgnoreCase(s2.getFullName()));
                break;
            case "Tên (Z→A)":
                students.sort((s1, s2) -> s2.getFullName().compareToIgnoreCase(s1.getFullName()));
                break;
            case "GPA (Cao→Thấp)":
                students.sort((s1, s2) -> Double.compare(s2.getGpa(), s1.getGpa()));
                break;
            case "GPA (Thấp→Cao)":
                students.sort((s1, s2) -> Double.compare(s1.getGpa(), s2.getGpa()));
                break;
            case "Lớp (A→Z)":
                students.sort((s1, s2) -> s1.getClassName().compareToIgnoreCase(s2.getClassName()));
                break;
            case "Ngành (A→Z)":
                students.sort((s1, s2) -> s1.getMajor().compareToIgnoreCase(s2.getMajor()));
                break;
            default:
                // No sorting
                break;
        }
        
        tableModel.setStudents(students);
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
        cmbSortBy.setSelectedIndex(0);
        List<Student> students = studentDAO.getAllStudents();
        tableModel.setStudents(students);
        updateStatusLabel(students.size());
    }
    
    private void updateStatusLabel(int count) {
        lblTotal.setText("Tổng số sinh viên: " + count);
    }
    
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Xuất danh sách sinh viên");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("students.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getAbsolutePath();
            if (!fileName.toLowerCase().endsWith(".csv")) {
                fileName += ".csv";
            }
            
            List<Student> students = studentDAO.findAll();
            if (studentDAO.exportToCSV(fileName, students)) {
                JOptionPane.showMessageDialog(this, 
                    "Xuất file CSV thành công!\nĐường dẫn: " + fileName, 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi xuất file CSV!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void importFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file CSV để nhập dữ liệu");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            
            try {
                int importedCount = studentDAO.importFromCSV(filePath);
                refreshTable();
                
                JOptionPane.showMessageDialog(this, 
                    "Nhập dữ liệu thành công!\nSố sinh viên được nhập: " + importedCount, 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi nhập file CSV: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showStatistics() {
        Map<String, Object> stats = studentDAO.getAcademicStatistics();
        
        StringBuilder statsText = new StringBuilder();
        statsText.append("=== THỐNG KÊ SINH VIÊN ===\n\n");
        
        // Total statistics
        statsText.append("Tổng số sinh viên: ").append(stats.get("total")).append("\n\n");
        
        // Gender statistics
        @SuppressWarnings("unchecked")
        Map<String, Long> genderStats = (Map<String, Long>) stats.get("genderStats");
        statsText.append("=== Thống kê theo giới tính ===\n");
        for (Map.Entry<String, Long> entry : genderStats.entrySet()) {
            statsText.append(entry.getKey()).append(": ").append(entry.getValue()).append(" sinh viên\n");
        }
        
        // Academic rank statistics
        @SuppressWarnings("unchecked")
        Map<String, Long> rankStats = (Map<String, Long>) stats.get("rankStats");
        statsText.append("\n=== Thống kê theo xếp loại ===\n");
        for (Map.Entry<String, Long> entry : rankStats.entrySet()) {
            statsText.append(entry.getKey()).append(": ").append(entry.getValue()).append(" sinh viên\n");
        }
        
        // GPA statistics
        statsText.append("\n=== Thống kê điểm GPA ===\n");
        statsText.append(String.format("Điểm trung bình: %.2f\n", (Double) stats.get("averageGpa")));
        statsText.append(String.format("Điểm cao nhất: %.2f\n", (Double) stats.get("maxGpa")));
        statsText.append(String.format("Điểm thấp nhất: %.2f\n", (Double) stats.get("minGpa")));
        
        // Top 5 students
        List<Student> topStudents = studentDAO.findTopStudentsByGpa(5);
        statsText.append("\n=== Top 5 sinh viên xuất sắc ===\n");
        for (int i = 0; i < topStudents.size(); i++) {
            Student s = topStudents.get(i);
            statsText.append(String.format("%d. %s - %s - GPA: %.2f\n", 
                i + 1, s.getStudentId(), s.getFullName(), s.getGpa()));
        }
        
        // Class statistics
        @SuppressWarnings("unchecked")
        Map<String, Long> classStats = (Map<String, Long>) stats.get("classStats");
        statsText.append("\n=== Thống kê theo lớp ===\n");
        classStats.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> statsText.append(entry.getKey()).append(": ").append(entry.getValue()).append(" sinh viên\n"));
        
        // Show statistics in dialog
        JTextArea textArea = new JTextArea(statsText.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Thống kê sinh viên", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void backupData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí sao lưu dữ liệu");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("students_backup_" + 
            java.time.LocalDate.now().toString() + ".dat"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String backupPath = fileChooser.getSelectedFile().getAbsolutePath();
            
            if (studentDAO.backupData(backupPath)) {
                JOptionPane.showMessageDialog(this, 
                    "Sao lưu dữ liệu thành công!\nĐường dẫn: " + backupPath, 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi sao lưu dữ liệu!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void restoreData() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Khôi phục dữ liệu sẽ thay thế toàn bộ dữ liệu hiện tại.\nBạn có chắc chắn muốn tiếp tục?", 
            "Xác nhận khôi phục", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn file sao lưu để khôi phục");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Backup Files", "dat"));
            
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String backupPath = fileChooser.getSelectedFile().getAbsolutePath();
                
                if (studentDAO.restoreData(backupPath)) {
                    refreshTable();
                    JOptionPane.showMessageDialog(this, 
                        "Khôi phục dữ liệu thành công!", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Lỗi khi khôi phục dữ liệu!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void showAdvancedSearch() {
        AdvancedSearchDialog dialog = new AdvancedSearchDialog(this, studentDAO);
        dialog.setVisible(true);
        
        if (dialog.getSearchResult() != null) {
            List<Student> results = dialog.getSearchResult();
            tableModel.setStudents(results);
            updateStatusLabel(results.size());
            txtSearch.setText("[Tìm kiếm nâng cao] " + results.size() + " kết quả");
        }
    }
    
    public StudentTableModel getTableModel() {
        return tableModel;
    }
    
    public List<Student> getStudents() {
        return tableModel.getStudents();
    }
    
    public void setStudents(List<Student> students) {
        tableModel.setStudents(students);
        updateStatusLabel(students.size());
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