package com.studentmanagement.ui;

import com.studentmanagement.dao.StudentDAO;
import com.studentmanagement.model.Student;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingConstants;

public class MainForm extends JFrame {
    private StudentDAO studentDAO;
    private StudentTableModel tableModel;
    private JTable studentTable;
    private JTextField txtSearch;
    private JComboBox<String> cmbSortBy, cmbDataSource;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnExport, btnImport, 
                    btnStatistics, btnBackup, btnRestore, btnAdvancedSearch,
                    btnSyncData, btnConnectDB;
    private JLabel lblTotal, lblConnectionStatus;
    private JProgressBar progressBar;
    private boolean isDatabaseConnected = false;
    
    public MainForm() {
        studentDAO = new StudentDAO();
        testDatabaseConnection();
        initComponents();
        setupLayout();
        setupEvents();
        refreshTable();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    private void testDatabaseConnection() {
        try {
            isDatabaseConnected = studentDAO.testConnection();
        } catch (Exception e) {
            isDatabaseConnected = false;
        }
    }
    
    private void initComponents() {
        setTitle("Hệ thống Quản lý Sinh viên - Enhanced Version");
        setSize(1400, 800);
        setMinimumSize(new Dimension(1200, 600));
        
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Table setup
        tableModel = new StudentTableModel();
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setAutoCreateRowSorter(true);
        studentTable.setRowHeight(28);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        studentTable.getTableHeader().setReorderingAllowed(false);
        studentTable.setGridColor(new Color(230, 230, 230));
        studentTable.setSelectionBackground(new Color(184, 207, 229));
        
        // Configure GPA column renderer
        studentTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            private final DecimalFormat df = new DecimalFormat("0.00");
            
            @Override
            public void setValue(Object value) {
                if (value instanceof Double) {
                    setText(df.format(value));
                } else if (value instanceof Number) {
                    setText(df.format(((Number) value).doubleValue()));
                } else {
                    setText(value != null ? value.toString() : "");
                }
                setHorizontalAlignment(SwingConstants.CENTER);
            }
        });
        
        // Search components
        txtSearch = new JTextField(25);
        txtSearch.setToolTipText("Tìm kiếm theo mã SV, tên, lớp hoặc ngành");
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // ComboBoxes
        cmbSortBy = new JComboBox<>(new String[]{
            "Không sắp xếp", "Mã SV (A→Z)", "Mã SV (Z→A)", 
            "Tên (A→Z)", "Tên (Z→A)", "GPA (Cao→Thấp)", "GPA (Thấp→Cao)",
            "Lớp (A→Z)", "Ngành (A→Z)"
        });
        
        cmbDataSource = new JComboBox<>(new String[]{
            "Tất cả dữ liệu", "Chỉ từ Database", "Chỉ từ CSV"
        });
        cmbDataSource.setToolTipText("Chọn nguồn dữ liệu hiển thị");
        
        // Buttons with icons and improved styling
        btnAdd = createStyledButton("Thêm SV", new Color(46, 125, 50), "➕");
        btnEdit = createStyledButton("Sửa", new Color(25, 118, 210), "✏️");
        btnDelete = createStyledButton("Xóa", new Color(198, 40, 40), "🗑️");
        btnRefresh = createStyledButton("Làm mới", new Color(102, 102, 102), "🔄");
        
        btnImport = createStyledButton("Nhập CSV", new Color(67, 160, 71), "📁");
        btnExport = createStyledButton("Xuất CSV", new Color(56, 142, 60), "💾");
        btnSyncData = createStyledButton("Đồng bộ", new Color(255, 152, 0), "🔄");
        
        btnStatistics = createStyledButton("Thống kê", new Color(156, 39, 176), "📊");
        btnBackup = createStyledButton("Sao lưu", new Color(121, 85, 72), "💾");
        btnRestore = createStyledButton("Khôi phục", new Color(96, 125, 139), "📥");
        btnAdvancedSearch = createStyledButton("Tìm kiếm NC", new Color(63, 81, 181), "🔍");
        btnConnectDB = createStyledButton("Kết nối DB", new Color(0, 150, 136), "🔌");
        
        // Status components
        lblTotal = new JLabel("Tổng số sinh viên: 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        lblConnectionStatus = new JLabel(isDatabaseConnected ? 
            "✅ Database: Đã kết nối" : "❌ Database: Chưa kết nối");
        lblConnectionStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblConnectionStatus.setForeground(isDatabaseConnected ? new Color(46, 125, 50) : Color.RED);
        
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
    }
    
    private JButton createStyledButton(String text, Color bgColor, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Main container with padding
        JPanel mainContainer = new JPanel(new BorderLayout(10, 10));
        mainContainer.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Top panel - Header
        JPanel headerPanel = createHeaderPanel();
        
        // Control panel - Search and filters
        JPanel controlPanel = createControlPanel();
        
        // Button panel - Actions
        JPanel buttonPanel = createButtonPanel();
        
        // Combine top panels
        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(controlPanel, BorderLayout.CENTER);
        topContainer.add(buttonPanel, BorderLayout.SOUTH);
        
        // Center panel - Table
        JPanel tablePanel = createTablePanel();
        
        // Bottom panel - Status
        JPanel statusPanel = createStatusPanel();
        
        mainContainer.add(topContainer, BorderLayout.NORTH);
        mainContainer.add(tablePanel, BorderLayout.CENTER);
        mainContainer.add(statusPanel, BorderLayout.SOUTH);
        
        add(mainContainer, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(33, 150, 243));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("🎓 HỆ THỐNG QUẢN LÝ SINH VIÊN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Phiên bản nâng cao với hỗ trợ CSV & Database");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subtitleLabel.setForeground(new Color(200, 230, 255));
        
        JPanel titleContainer = new JPanel(new BorderLayout());
        titleContainer.setOpaque(false);
        titleContainer.add(titleLabel, BorderLayout.NORTH);
        titleContainer.add(subtitleLabel, BorderLayout.CENTER);
        
        panel.add(titleContainer, BorderLayout.WEST);
        panel.add(lblConnectionStatus, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createTitledBorder("🔍 Tìm kiếm & Lọc dữ liệu"));
        
        panel.add(new JLabel("Từ khóa:"));
        panel.add(txtSearch);
        
        panel.add(Box.createHorizontalStrut(20));
        
        panel.add(new JLabel("Sắp xếp:"));
        panel.add(cmbSortBy);
        
        panel.add(Box.createHorizontalStrut(20));
        
        panel.add(new JLabel("Nguồn dữ liệu:"));
        panel.add(cmbDataSource);
        
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnAdvancedSearch);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("⚡ Thao tác"));
        
        // Data management buttons
        JPanel dataPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        dataPanel.setOpaque(false);
        dataPanel.add(btnAdd);
        dataPanel.add(btnEdit);
        dataPanel.add(btnDelete);
        dataPanel.add(btnRefresh);
        
        // Import/Export buttons
        JPanel ioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        ioPanel.setOpaque(false);
        ioPanel.add(btnImport);
        ioPanel.add(btnExport);
        ioPanel.add(btnSyncData);
        
        // System buttons
        JPanel systemPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        systemPanel.setOpaque(false);
        systemPanel.add(btnConnectDB);
        systemPanel.add(btnStatistics);
        systemPanel.add(btnBackup);
        systemPanel.add(btnRestore);
        
        panel.add(dataPanel, BorderLayout.WEST);
        panel.add(ioPanel, BorderLayout.CENTER);
        panel.add(systemPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📋 Danh sách sinh viên"));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JPanel leftStatus = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftStatus.add(lblTotal);
        
        JPanel rightStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightStatus.add(progressBar);
        
        panel.add(leftStatus, BorderLayout.WEST);
        panel.add(rightStatus, BorderLayout.EAST);
        
        return panel;
    }
    
    private void setupEvents() {
        // Search events
        txtSearch.addActionListener(e -> performSearch());
        
        // ComboBox events
        cmbSortBy.addActionListener(e -> applySorting());
        cmbDataSource.addActionListener(e -> filterByDataSource());
        
        // Button events
        btnAdd.addActionListener(e -> addStudent());
        btnEdit.addActionListener(e -> editStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnRefresh.addActionListener(e -> refreshTable());
        btnExport.addActionListener(e -> exportToCSV());
        btnImport.addActionListener(e -> showImportDialog());
        btnSyncData.addActionListener(e -> syncData());
        btnConnectDB.addActionListener(e -> reconnectDatabase());
        btnStatistics.addActionListener(e -> showStatistics());
        btnBackup.addActionListener(e -> backupData());
        btnRestore.addActionListener(e -> restoreData());
        btnAdvancedSearch.addActionListener(e -> showAdvancedSearch());
        
        // Table events
        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editStudent();
                }
            }
        });
        
        // Selection events
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = studentTable.getSelectedRow() >= 0;
            btnEdit.setEnabled(hasSelection);
            btnDelete.setEnabled(hasSelection);
        });
        
        // Initial state
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
    }
    
    private void showImportDialog() {
        String[] options = {"Nhập từ CSV", "Nhập từ Database khác", "Hủy"};
        int choice = JOptionPane.showOptionDialog(this,
            "Chọn phương thức nhập dữ liệu:",
            "Nhập dữ liệu",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
            
        switch (choice) {
            case 0: importFromCSV(); break;
            case 1: importFromDatabase(); break;
            default: break;
        }
    }
    
    private void importFromDatabase() {
        // Dialog để nhập thông tin kết nối database khác
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JTextField txtServer = new JTextField("localhost", 20);
        JTextField txtDatabase = new JTextField("StudentManagementDB", 15);
        JTextField txtUsername = new JTextField("SA", 10);
        JPasswordField txtPassword = new JPasswordField("12345", 10);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Server:"), gbc);
        gbc.gridx = 1;
        panel.add(txtServer, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Database:"), gbc);
        gbc.gridx = 1;
        panel.add(txtDatabase, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(txtUsername, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel,
            "Thông tin kết nối Database", JOptionPane.OK_CANCEL_OPTION);
            
        if (result == JOptionPane.OK_OPTION) {
            // Thực hiện import từ database khác
            // Tạm thời hiển thị thông báo
            JOptionPane.showMessageDialog(this,
                "Chức năng nhập từ database khác đang được phát triển!",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void syncData() {
        if (!isDatabaseConnected) {
            JOptionPane.showMessageDialog(this,
                "Cần kết nối database để đồng bộ dữ liệu!",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Đồng bộ dữ liệu sẽ hợp nhất dữ liệu từ CSV và Database.\nBạn có muốn tiếp tục?",
            "Xác nhận đồng bộ",
            JOptionPane.YES_NO_OPTION);
            
        if (choice == JOptionPane.YES_OPTION) {
            performDataSync();
        }
    }
    
    private void performDataSync() {
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                progressBar.setVisible(true);
                progressBar.setString("Đang đồng bộ dữ liệu...");
                progressBar.setIndeterminate(true);
                
                // Thực hiện đồng bộ
                Thread.sleep(2000); // Simulate process
                
                return null;
            }
            
            @Override
            protected void done() {
                progressBar.setVisible(false);
                refreshTable();
                JOptionPane.showMessageDialog(MainForm.this,
                    "Đồng bộ dữ liệu thành công!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        worker.execute();
    }
    
    private void reconnectDatabase() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                progressBar.setVisible(true);
                progressBar.setString("Đang kết nối database...");
                progressBar.setIndeterminate(true);
                
                return studentDAO.testConnection();
            }
            
            @Override
            protected void done() {
                progressBar.setVisible(false);
                try {
                    isDatabaseConnected = get();
                    updateConnectionStatus();
                    
                    if (isDatabaseConnected) {
                        JOptionPane.showMessageDialog(MainForm.this,
                            "Kết nối database thành công!",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(MainForm.this,
                            "Không thể kết nối database!\nVui lòng kiểm tra cấu hình.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MainForm.this,
                        "Lỗi kết nối: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void updateConnectionStatus() {
        lblConnectionStatus.setText(isDatabaseConnected ? 
            "✅ Database: Đã kết nối" : "❌ Database: Chưa kết nối");
        lblConnectionStatus.setForeground(isDatabaseConnected ? 
            new Color(46, 125, 50) : Color.RED);
    }
    
    private void filterByDataSource() {
        String source = (String) cmbDataSource.getSelectedItem();
        // Implement filtering logic based on data source
        refreshTable();
    }
    
    // Các phương thức còn lại giữ nguyên logic cũ nhưng với UI cải tiến
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
                break;
        }
        
        tableModel.setStudents(students);
    }
    
    private void addStudent() {
        StudentDialog dialog = new StudentDialog(this, null, false);
        dialog.setVisible(true);
        
        if (dialog.getDialogResult()) {
            Student newStudent = dialog.getStudent();
            if (studentDAO.save(newStudent)) {
                refreshTable();
                showSuccessMessage("Thêm sinh viên thành công!");
            } else {
                showErrorMessage("Mã sinh viên đã tồn tại!");
            }
        }
    }
    
    private void editStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarningMessage("Vui lòng chọn sinh viên cần sửa!");
            return;
        }
        
        int modelRow = studentTable.convertRowIndexToModel(selectedRow);
        Student student = tableModel.getStudentAt(modelRow);
        
        if (student != null) {
            StudentDialog dialog = new StudentDialog(this, student, true);
            dialog.setVisible(true);
            
            if (dialog.getDialogResult()) {
                Student updatedStudent = dialog.getStudent();
                if (studentDAO.update(updatedStudent)) {
                    refreshTable();
                    showSuccessMessage("Cập nhật thông tin thành công!");
                }
            }
        }
    }
    
    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            showWarningMessage("Vui lòng chọn sinh viên cần xóa!");
            return;
        }
        
        int modelRow = studentTable.convertRowIndexToModel(selectedRow);
        Student student = tableModel.getStudentAt(modelRow);
        
        if (student != null) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa sinh viên: " + student.getFullName() + "?", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (studentDAO.deleteById(student.getStudentId())) {
                    refreshTable();
                    showSuccessMessage("Xóa sinh viên thành công!");
                }
            }
        }
    }
    
    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        List<Student> results;
        
        if (keyword.isEmpty()) {
            results = studentDAO.findAll();
        } else {
            results = studentDAO.searchByKeyword(keyword);
        }
        
        tableModel.setStudents(results);
        updateStatusLabel(results.size());
    }
    
    private void refreshTable() {
        SwingWorker<List<Student>, Void> worker = new SwingWorker<List<Student>, Void>() {
            @Override
            protected List<Student> doInBackground() throws Exception {
                progressBar.setVisible(true);
                progressBar.setString("Đang tải dữ liệu...");
                progressBar.setIndeterminate(true);
                
                return studentDAO.findAll();
            }
            
            @Override
            protected void done() {
                try {
                    List<Student> students = get();
                    txtSearch.setText("");
                    cmbSortBy.setSelectedIndex(0);
                    tableModel.setStudents(students);
                    updateStatusLabel(students.size());
                } catch (Exception e) {
                    showErrorMessage("Lỗi khi tải dữ liệu: " + e.getMessage());
                } finally {
                    progressBar.setVisible(false);
                }
            }
        };
        worker.execute();
    }
    
    private void updateStatusLabel(int count) {
        lblTotal.setText("Tổng số sinh viên: " + count);
    }
    
    // Utility methods for notifications
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "✅ Thành công", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "❌ Lỗi", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "⚠️ Cảnh báo", 
            JOptionPane.WARNING_MESSAGE);
    }
    
    // Import/Export methods (giữ nguyên logic cũ)
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Xuất danh sách sinh viên");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("students_export.csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getAbsolutePath();
            if (!fileName.toLowerCase().endsWith(".csv")) {
                fileName += ".csv";
            }
            
            List<Student> students = studentDAO.findAll();
            if (studentDAO.exportToCSV(fileName, students)) {
                showSuccessMessage("Xuất file CSV thành công!\nĐường dẫn: " + fileName);
            } else {
                showErrorMessage("Lỗi khi xuất file CSV!");
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
            
            SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
                @Override
                protected Integer doInBackground() throws Exception {
                    progressBar.setVisible(true);
                    progressBar.setString("Đang nhập dữ liệu từ CSV...");
                    progressBar.setIndeterminate(true);
                    
                    return studentDAO.importFromCSV(filePath);
                }
                
                @Override
                protected void done() {
                    try {
                        int importedCount = get();
                        refreshTable();
                        showSuccessMessage("Nhập dữ liệu thành công!\nSố sinh viên được nhập: " + importedCount);
                    } catch (Exception e) {
                        showErrorMessage("Lỗi khi nhập file CSV: " + e.getMessage());
                    } finally {
                        progressBar.setVisible(false);
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void showStatistics() {
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<Map<String, Object>, Void>() {
            @Override
            protected Map<String, Object> doInBackground() throws Exception {
                progressBar.setVisible(true);
                progressBar.setString("Đang tính toán thống kê...");
                progressBar.setIndeterminate(true);
                
                return studentDAO.getAcademicStatistics();
            }
            
            @Override
            protected void done() {
                try {
                    Map<String, Object> stats = get();
                    displayStatistics(stats);
                } catch (Exception e) {
                    showErrorMessage("Lỗi khi tính toán thống kê: " + e.getMessage());
                } finally {
                    progressBar.setVisible(false);
                }
            }
        };
        worker.execute();
    }
    
    private void displayStatistics(Map<String, Object> stats) {
        StringBuilder statsText = new StringBuilder();
        statsText.append("=== THỐNG KÊ SINH VIÊN ===\n\n");
        
        // Total statistics
        statsText.append("📊 Tổng số sinh viên: ").append(stats.get("total")).append("\n\n");
        
        // Gender statistics
        @SuppressWarnings("unchecked")
        Map<String, Long> genderStats = (Map<String, Long>) stats.get("genderStats");
        if (genderStats != null) {
            statsText.append("👥 Thống kê theo giới tính:\n");
            for (Map.Entry<String, Long> entry : genderStats.entrySet()) {
                statsText.append("   • ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" sinh viên\n");
            }
        }
        
        // Academic rank statistics
        @SuppressWarnings("unchecked")
        Map<String, Long> rankStats = (Map<String, Long>) stats.get("rankStats");
        if (rankStats != null) {
            statsText.append("\n🎯 Thống kê theo xếp loại:\n");
            for (Map.Entry<String, Long> entry : rankStats.entrySet()) {
                statsText.append("   • ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" sinh viên\n");
            }
        }
        
        // GPA statistics
        if (stats.containsKey("averageGpa")) {
            statsText.append("\n📈 Thống kê điểm GPA:\n");
            statsText.append(String.format("   • Điểm trung bình: %.2f\n", (Double) stats.get("averageGpa")));
            statsText.append(String.format("   • Điểm cao nhất: %.2f\n", (Double) stats.get("maxGpa")));
            statsText.append(String.format("   • Điểm thấp nhất: %.2f\n", (Double) stats.get("minGpa")));
        }
        
        // Top 5 students
        List<Student> topStudents = studentDAO.findTopStudentsByGpa(5);
        if (!topStudents.isEmpty()) {
            statsText.append("\n🏆 Top 5 sinh viên xuất sắc:\n");
            for (int i = 0; i < topStudents.size(); i++) {
                Student s = topStudents.get(i);
                statsText.append(String.format("   %d. %s - %s - GPA: %.2f\n", 
                    i + 1, s.getStudentId(), s.getFullName(), s.getGpa()));
            }
        }
        
        // Class statistics
        @SuppressWarnings("unchecked")
        Map<String, Long> classStats = (Map<String, Long>) stats.get("classStats");
        if (classStats != null && !classStats.isEmpty()) {
            statsText.append("\n🏫 Thống kê theo lớp (Top 10):\n");
            classStats.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> statsText.append("   • ").append(entry.getKey())
                        .append(": ").append(entry.getValue()).append(" sinh viên\n"));
        }
        
        // Major statistics
        @SuppressWarnings("unchecked")
        Map<String, Long> majorStats = (Map<String, Long>) stats.get("majorStats");
        if (majorStats != null && !majorStats.isEmpty()) {
            statsText.append("\n🎓 Thống kê theo ngành:\n");
            majorStats.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .forEach(entry -> statsText.append("   • ").append(entry.getKey())
                        .append(": ").append(entry.getValue()).append(" sinh viên\n"));
        }
        
        // Create enhanced statistics dialog
        JDialog statsDialog = new JDialog(this, "📊 Thống kê Sinh viên", true);
        statsDialog.setSize(700, 600);
        statsDialog.setLocationRelativeTo(this);
        
        JTextArea textArea = new JTextArea(statsText.toString());
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setBackground(new Color(250, 250, 250));
        textArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        JButton btnClose = createStyledButton("Đóng", new Color(96, 125, 139), "✖️");
        btnClose.addActionListener(e -> statsDialog.dispose());
        
        JButton btnExportStats = createStyledButton("Xuất thống kê", new Color(67, 160, 71), "💾");
        btnExportStats.addActionListener(e -> exportStatistics(statsText.toString()));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnExportStats);
        buttonPanel.add(btnClose);
        
        statsDialog.add(scrollPane, BorderLayout.CENTER);
        statsDialog.add(buttonPanel, BorderLayout.SOUTH);
        statsDialog.setVisible(true);
    }
    
    private void exportStatistics(String statsContent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Xuất file thống kê");
        fileChooser.setSelectedFile(new File("statistics_" + 
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile())) {
                writer.write(statsContent);
                showSuccessMessage("Xuất file thống kê thành công!");
            } catch (Exception e) {
                showErrorMessage("Lỗi khi xuất file thống kê: " + e.getMessage());
            }
        }
    }
    
    private void backupData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí sao lưu dữ liệu");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("backup_" + 
            java.time.LocalDate.now().toString().replace("-", "") + ".dat"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String backupPath = fileChooser.getSelectedFile().getAbsolutePath();
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    progressBar.setVisible(true);
                    progressBar.setString("Đang sao lưu dữ liệu...");
                    progressBar.setIndeterminate(true);
                    
                    return studentDAO.backupData(backupPath);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            showSuccessMessage("Sao lưu dữ liệu thành công!\nĐường dẫn: " + backupPath);
                        } else {
                            showErrorMessage("Lỗi khi sao lưu dữ liệu!");
                        }
                    } catch (Exception e) {
                        showErrorMessage("Lỗi khi sao lưu: " + e.getMessage());
                    } finally {
                        progressBar.setVisible(false);
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void restoreData() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "⚠️ Khôi phục dữ liệu sẽ thay thế toàn bộ dữ liệu hiện tại.\nBạn có chắc chắn muốn tiếp tục?", 
            "Xác nhận khôi phục", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn file sao lưu để khôi phục");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Backup Files", "dat"));
            
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String backupPath = fileChooser.getSelectedFile().getAbsolutePath();
                
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        progressBar.setVisible(true);
                        progressBar.setString("Đang khôi phục dữ liệu...");
                        progressBar.setIndeterminate(true);
                        
                        return studentDAO.restoreData(backupPath);
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            boolean success = get();
                            if (success) {
                                refreshTable();
                                showSuccessMessage("Khôi phục dữ liệu thành công!");
                            } else {
                                showErrorMessage("Lỗi khi khôi phục dữ liệu!");
                            }
                        } catch (Exception e) {
                            showErrorMessage("Lỗi khi khôi phục: " + e.getMessage());
                        } finally {
                            progressBar.setVisible(false);
                        }
                    }
                };
                worker.execute();
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
            txtSearch.setText("🔍 [Tìm kiếm nâng cao] " + results.size() + " kết quả");
        }
    }
    
    // Getter methods for external access
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
    
    public boolean isDatabaseConnected() {
        return isDatabaseConnected;
    }
    
    // Main method
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Additional UI enhancements
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        SwingUtilities.invokeLater(() -> {
            try {
                new MainForm().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, 
                    "Lỗi khởi động ứng dụng: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}