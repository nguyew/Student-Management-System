package com.studentmanagement.ui;

import com.studentmanagement.dao.StudentDAO;
import com.studentmanagement.dao.StudentDAO.ImportResult;
import com.studentmanagement.model.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class ImportDataDialog extends JDialog {
    private StudentDAO studentDAO;
    private JRadioButton rbCSV, rbDatabase;
    private JTextField txtFilePath, txtServer, txtDatabase, txtUsername;
    private JPasswordField txtPassword;
    private JButton btnBrowse, btnImport, btnCancel, btnTestConnection;
    private JTextArea txtLog;
    private JProgressBar progressBar;
    private JCheckBox chkReplaceExisting, chkValidateData;
    private ImportResult lastResult;
    
    public ImportDataDialog(Frame parent, StudentDAO studentDAO) {
        super(parent, "Nhập dữ liệu nâng cao", true);
        this.studentDAO = studentDAO;
        initComponents();
        setupLayout();
        setupEvents();
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setSize(700, 600);
        
        // Radio buttons cho chọn nguồn
        rbCSV = new JRadioButton("Nhập từ file CSV", true);
        rbDatabase = new JRadioButton("Nhập từ Database khác");
        ButtonGroup bgSource = new ButtonGroup();
        bgSource.add(rbCSV);
        bgSource.add(rbDatabase);
        
        // CSV components
        txtFilePath = new JTextField(30);
        txtFilePath.setEditable(false);
        btnBrowse = new JButton("Chọn file...");
        
        // Database components
        txtServer = new JTextField("localhost:1433", 20);
        txtDatabase = new JTextField("StudentManagementDB", 15);
        txtUsername = new JTextField("SA", 10);
        txtPassword = new JPasswordField("12345", 10);
        btnTestConnection = new JButton("Test kết nối");
        
        // Options
        chkReplaceExisting = new JCheckBox("Thay thế dữ liệu trùng lặp", false);
        chkValidateData = new JCheckBox("Kiểm tra tính hợp lệ dữ liệu", true);
        
        // Control buttons
        btnImport = new JButton("Bắt đầu nhập");
        btnCancel = new JButton("Hủy");
        
        // Progress và log
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Sẵn sàng");
        
        txtLog = new JTextArea(10, 50);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 11));
        txtLog.setBackground(new Color(245, 245, 245));
        
        // Initial state
        updateComponentsState();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Source selection panel
        JPanel sourcePanel = createSourcePanel();
        
        // Configuration panel
        JPanel configPanel = createConfigPanel();
        
        // Options panel
        JPanel optionsPanel = createOptionsPanel();
        
        // Control panel
        JPanel controlPanel = createControlPanel();
        
        // Log panel
        JPanel logPanel = createLogPanel();
        
        // Top container
        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        topContainer.add(sourcePanel, BorderLayout.NORTH);
        topContainer.add(configPanel, BorderLayout.CENTER);
        topContainer.add(optionsPanel, BorderLayout.SOUTH);
        
        mainPanel.add(topContainer, BorderLayout.NORTH);
        mainPanel.add(logPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createSourcePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("📂 Chọn nguồn dữ liệu"));
        
        panel.add(rbCSV);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(rbDatabase);
        
        return panel;
    }
    
    private JPanel createConfigPanel() {
        JPanel panel = new JPanel(new CardLayout());
        
        // CSV config panel
        JPanel csvPanel = new JPanel(new BorderLayout(10, 10));
        csvPanel.setBorder(BorderFactory.createTitledBorder("📁 Cấu hình file CSV"));
        
        JPanel csvInput = new JPanel(new FlowLayout(FlowLayout.LEFT));
        csvInput.add(new JLabel("Đường dẫn file:"));
        csvInput.add(txtFilePath);
        csvInput.add(btnBrowse);
        
        JLabel csvNote = new JLabel("<html><i>Định dạng CSV: Mã SV, Họ tên, Ngày sinh (dd/MM/yyyy), Giới tính, Địa chỉ, Điện thoại, Email, Lớp, Ngành, GPA</i></html>");
        csvNote.setForeground(Color.GRAY);
        
        csvPanel.add(csvInput, BorderLayout.CENTER);
        csvPanel.add(csvNote, BorderLayout.SOUTH);
        
        // Database config panel
        JPanel dbPanel = new JPanel(new GridBagLayout());
        dbPanel.setBorder(BorderFactory.createTitledBorder("🗄️ Cấu hình Database"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        dbPanel.add(new JLabel("Server:"), gbc);
        gbc.gridx = 1;
        dbPanel.add(txtServer, gbc);
        
        gbc.gridx = 2;
        dbPanel.add(new JLabel("Database:"), gbc);
        gbc.gridx = 3;
        dbPanel.add(txtDatabase, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        dbPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        dbPanel.add(txtUsername, gbc);
        
        gbc.gridx = 2;
        dbPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 3;
        dbPanel.add(txtPassword, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;
        dbPanel.add(btnTestConnection, gbc);
        
        panel.add(csvPanel, "CSV");
        panel.add(dbPanel, "DATABASE");
        
        return panel;
    }
    
    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("⚙️ Tùy chọn"));
        
        panel.add(chkValidateData);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(chkReplaceExisting);
        
        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(new JLabel("Tiến độ:"), BorderLayout.WEST);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnImport.setPreferredSize(new Dimension(120, 35));
        btnImport.setBackground(new Color(46, 125, 50));
        btnImport.setForeground(Color.WHITE);
        
        btnCancel.setPreferredSize(new Dimension(100, 35));
        
        buttonPanel.add(btnImport);
        buttonPanel.add(btnCancel);
        
        panel.add(progressPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("📋 Nhật ký quá trình"));
        
        JScrollPane scrollPane = new JScrollPane(txtLog);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupEvents() {
        rbCSV.addActionListener(e -> updateComponentsState());
        rbDatabase.addActionListener(e -> updateComponentsState());
        
        btnBrowse.addActionListener(e -> browseForCSVFile());
        btnTestConnection.addActionListener(e -> testDatabaseConnection());
        btnImport.addActionListener(e -> performImport());
        btnCancel.addActionListener(e -> dispose());
    }
    
    private void updateComponentsState() {
        boolean isCsvSelected = rbCSV.isSelected();
        
        txtFilePath.setEnabled(isCsvSelected);
        btnBrowse.setEnabled(isCsvSelected);
        
        txtServer.setEnabled(!isCsvSelected);
        txtDatabase.setEnabled(!isCsvSelected);
        txtUsername.setEnabled(!isCsvSelected);
        txtPassword.setEnabled(!isCsvSelected);
        btnTestConnection.setEnabled(!isCsvSelected);
        
        // Update card layout
        Container parent = txtFilePath.getParent();
        while (parent != null && !(parent.getLayout() instanceof CardLayout)) {
            parent = parent.getParent();
        }
        if (parent != null) {
            CardLayout cl = (CardLayout) parent.getLayout();
            cl.show(parent, isCsvSelected ? "CSV" : "DATABASE");
        }
    }
    
    private void browseForCSVFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file CSV để nhập");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtFilePath.setText(fileChooser.getSelectedFile().getAbsolutePath());
            logMessage("Đã chọn file: " + fileChooser.getSelectedFile().getName());
        }
    }
    
    private void testDatabaseConnection() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                progressBar.setIndeterminate(true);
                progressBar.setString("Đang test kết nối...");
                
                // Simulate connection test
                Thread.sleep(1000);
                return studentDAO.testConnection();
            }
            
            @Override
            protected void done() {
                try {
                    boolean connected = get();
                    progressBar.setIndeterminate(false);
                    progressBar.setString("Sẵn sàng");
                    
                    if (connected) {
                        logMessage("✅ Kết nối database thành công!");
                        JOptionPane.showMessageDialog(ImportDataDialog.this,
                            "Kết nối database thành công!",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        logMessage("❌ Kết nối database thất bại!");
                        JOptionPane.showMessageDialog(ImportDataDialog.this,
                            "Không thể kết nối database!\nVui lòng kiểm tra thông tin kết nối.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    logMessage("❌ Lỗi test kết nối: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void performImport() {
        if (rbCSV.isSelected()) {
            importFromCSV();
        } else {
            importFromDatabase();
        }
    }
    
    private void importFromCSV() {
        String filePath = txtFilePath.getText().trim();
        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn file CSV!",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!new File(filePath).exists()) {
            JOptionPane.showMessageDialog(this,
                "File không tồn tại!",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        SwingWorker<ImportResult, String> worker = new SwingWorker<ImportResult, String>() {
            @Override
            protected ImportResult doInBackground() throws Exception {
                btnImport.setEnabled(false);
                progressBar.setIndeterminate(true);
                progressBar.setString("Đang nhập dữ liệu từ CSV...");
                
                publish("Bắt đầu nhập dữ liệu từ file: " + new File(filePath).getName());
                
                if (chkValidateData.isSelected()) {
                    publish("Đang kiểm tra tính hợp lệ dữ liệu...");
                }
                
                ImportResult result = studentDAO.importFromCSV(filePath);
                
                publish("Hoàn tất! Thành công: " + result.successCount + 
                       ", Thất bại: " + result.failureCount + 
                       ", Trùng lặp: " + result.duplicateCount);
                
                return result;
            }
            
            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    logMessage(message);
                }
            }
            
            @Override
            protected void done() {
                try {
                    lastResult = get();
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                    progressBar.setString("Hoàn tất");
                    
                    showImportResult(lastResult);
                    
                } catch (Exception e) {
                    logMessage("❌ Lỗi nhập dữ liệu: " + e.getMessage());
                    JOptionPane.showMessageDialog(ImportDataDialog.this,
                        "Lỗi nhập dữ liệu: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnImport.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
    
    private void importFromDatabase() {
        // Placeholder for database import
        logMessage("Chức năng nhập từ database khác đang được phát triển...");
        JOptionPane.showMessageDialog(this,
            "Chức năng nhập từ database khác đang được phát triển!\n" +
            "Hiện tại hỗ trợ nhập từ file CSV.",
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showImportResult(ImportResult result) {
        String message = String.format(
            "Kết quả nhập dữ liệu:\n\n" +
            "✅ Thành công: %d sinh viên\n" +
            "❌ Thất bại: %d sinh viên\n" +
            "🔄 Trùng lặp: %d sinh viên\n" +
            "📊 Tổng xử lý: %d dòng",
            result.successCount, result.failureCount, 
            result.duplicateCount, result.getTotalProcessed()
        );
        
        if (result.hasErrors()) {
            message += "\n\n⚠️ Có " + result.errors.size() + " lỗi chi tiết (xem nhật ký bên dưới)";
        }
        
        JOptionPane.showMessageDialog(this, message, 
            result.successCount > 0 ? "Nhập dữ liệu hoàn tất" : "Nhập dữ liệu thất bại", 
            result.successCount > 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        
        // Log detailed errors
        if (result.hasErrors()) {
            logMessage("\n=== CHI TIẾT LỖI ===");
            for (String error : result.errors) {
                logMessage("❌ " + error);
            }
        }
    }
    
    private JPanel createOptionsPanels() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("⚙️ Tùy chọn nhập"));
        
        panel.add(chkValidateData);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(chkReplaceExisting);
        
        JLabel noteLabel = new JLabel("<html><i>Kiểm tra dữ liệu sẽ làm chậm quá trình nhập nhưng đảm bảo chất lượng dữ liệu</i></html>");
        noteLabel.setForeground(Color.GRAY);
        
        JPanel container = new JPanel(new BorderLayout());
        container.add(panel, BorderLayout.CENTER);
        container.add(noteLabel, BorderLayout.SOUTH);
        
        return container;
    }
    
    private JPanel createControlPanels() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        JPanel progressPanel = new JPanel(new BorderLayout(5, 5));
        progressPanel.add(new JLabel("Tiến độ:"), BorderLayout.WEST);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        // Style buttons
        btnImport.setPreferredSize(new Dimension(130, 35));
        btnImport.setBackground(new Color(46, 125, 50));
        btnImport.setForeground(Color.WHITE);
        btnImport.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnImport.setBorderPainted(false);
        btnImport.setFocusPainted(false);
        
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        buttonPanel.add(btnImport);
        buttonPanel.add(btnCancel);
        
        panel.add(progressPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createLogPanels() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("📋 Nhật ký quá trình"));
        
        txtLog.setText("Sẵn sàng nhập dữ liệu...\n");
        JScrollPane scrollPane = new JScrollPane(txtLog);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(650, 200));
        
        // Clear log button
        JButton btnClearLog = new JButton("Xóa log");
        btnClearLog.setPreferredSize(new Dimension(80, 25));
        btnClearLog.addActionListener(e -> {
            txtLog.setText("Log đã được xóa...\n");
            progressBar.setValue(0);
            progressBar.setString("Sẵn sàng");
        });
        
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonContainer.add(btnClearLog);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonContainer, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setupEvent() {
        rbCSV.addActionListener(e -> updateComponentsState());
        rbDatabase.addActionListener(e -> updateComponentsState());
        
        btnBrowse.addActionListener(e -> browseForCSVFile());
        btnTestConnection.addActionListener(e -> testDatabaseConnection());
        btnImport.addActionListener(e -> performImport());
        btnCancel.addActionListener(e -> dispose());
        
        // Enter key support
        txtFilePath.addActionListener(e -> performImport());
        
        // Validation for database fields
        txtServer.addActionListener(e -> validateDatabaseInfo());
        txtDatabase.addActionListener(e -> validateDatabaseInfo());
    }
    
    private void validateDatabaseInfo() {
        boolean hasValidInfo = !txtServer.getText().trim().isEmpty() && 
                              !txtDatabase.getText().trim().isEmpty() &&
                              !txtUsername.getText().trim().isEmpty();
        btnTestConnection.setEnabled(hasValidInfo);
    }
    
    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            txtLog.append("[" + timestamp + "] " + message + "\n");
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
        });
    }
    
    public ImportResult getLastResult() {
        return lastResult;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            JFrame frame = new JFrame("Test Import Dialog");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 200);
            frame.setLocationRelativeTo(null);
            
            JButton btnTest = new JButton("Mở Import Dialog");
            btnTest.addActionListener(e -> {
                ImportDataDialog dialog = new ImportDataDialog(frame, new StudentDAO());
                dialog.setVisible(true);
            });
            
            frame.add(btnTest);
            frame.setVisible(true);
        });
    }
}