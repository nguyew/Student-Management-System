# 📚 Hệ thống Quản lý Sinh viên (Student Management System)

Một ứng dụng Java Swing hoàn chỉnh để quản lý thông tin sinh viên với giao diện thân thiện và nhiều tính năng mạnh mẽ.

## ✨ Tính năng chính

### 👥 Quản lý Sinh viên
- ➕ **Thêm sinh viên mới** với đầy đủ thông tin
- ✏️ **Sửa thông tin** sinh viên hiện có
- 🗑️ **Xóa sinh viên** với xác nhận an toàn
- 📋 **Hiển thị danh sách** trong bảng với giao diện đẹp

### 🔍 Tìm kiếm & Lọc
- 🔎 **Tìm kiếm nhanh** theo mã SV, tên, lớp, ngành
- 🎯 **Tìm kiếm nâng cao** với nhiều tiêu chí:
  - Mã sinh viên
  - Họ và tên
  - Lớp học
  - Ngành học  
  - Giới tính
  - Khoảng điểm GPA

### 📊 Sắp xếp
- 🔤 **Sắp xếp theo mã SV** (A→Z, Z→A)
- 👤 **Sắp xếp theo tên** (A→Z, Z→A)
- 📈 **Sắp xếp theo GPA** (Cao→Thấp, Thấp→Cao)
- 🏫 **Sắp xếp theo lớp và ngành**

### 📈 Thống kê
- 📊 **Thống kê tổng quan**: Tổng số sinh viên
- 👫 **Thống kê giới tính**: Số lượng nam/nữ
- 🏆 **Thống kê xếp loại**: Xuất sắc, Giỏi, Khá, Trung bình, Yếu
- 🎓 **Thống kê theo lớp và ngành**
- 📊 **Thống kê điểm GPA**: Trung bình, cao nhất, thấp nhất
- 🥇 **Top 5 sinh viên xuất sắc**

### 💾 Import/Export & Backup
- 📥 **Import từ CSV** với xử lý địa chỉ phức tạp
- 📤 **Export ra CSV** với format chuẩn
- 💾 **Sao lưu dữ liệu** tự động
- 🔄 **Khôi phục dữ liệu** từ backup

## 🛠️ Công nghệ sử dụng

- **Java 8+** - Ngôn ngữ lập trình chính
- **Java Swing** - Giao diện người dùng
- **Java Serialization** - Lưu trữ dữ liệu
- **CSV Processing** - Import/Export dữ liệu
- **Maven/Gradle** - Quản lý dependencies (tùy chọn)

## 🏗️ Kiến trúc dự án

```
StudentManagementSystem/
├── src/
│   └── com/
│       └── studentmanagement/
│           ├── model/
│           │   └── Student.java              # Model sinh viên
│           ├── dao/
│           │   ├── CrudRepository.java       # Interface CRUD cơ bản
│           │   ├── StudentRepository.java    # Interface repository mở rộng
│           │   └── StudentDAO.java           # Data Access Object
│           └── ui/
│               ├── MainForm.java             # Giao diện chính
│               ├── StudentDialog.java        # Dialog thêm/sửa SV
│               ├── AdvancedSearchDialog.java # Dialog tìm kiếm nâng cao
│               └── StudentTableModel.java    # Table model cho JTable
├── students.dat                              # File dữ liệu chính
└── README.md
```

## 🚀 Cài đặt và Chạy

### Yêu cầu hệ thống
- Java Development Kit (JDK) 8 trở lên
- IDE: NetBeans, IntelliJ IDEA, Eclipse, hoặc VS Code
- Hệ điều hành: Windows, macOS, Linux

### Các bước chạy

1. **Clone hoặc tải về dự án**
```bash
git clone [repository-url]
cd StudentManagementSystem
```

2. **Mở dự án trong IDE**
   - NetBeans: File → Open Project
   - IntelliJ: File → Open → Chọn thư mục dự án
   - Eclipse: Import → Existing Projects into Workspace

3. **Chạy ứng dụng**
   - Mở file `MainForm.java`
   - Chạy method `main()` hoặc nhấn F6 (NetBeans)

## 📝 Cách sử dụng

### 1. Thêm sinh viên mới
- Nhấn nút **"Thêm"**
- Điền đầy đủ thông tin trong form
- Nhấn **"Lưu"** để xác nhận

### 2. Sửa thông tin sinh viên  
- Chọn sinh viên trong bảng
- Nhấn nút **"Sửa"** hoặc double-click
- Chỉnh sửa thông tin và nhấn **"Lưu"**

### 3. Tìm kiếm sinh viên
- **Tìm kiếm nhanh**: Gõ từ khóa vào ô tìm kiếm
- **Tìm kiếm nâng cao**: Nhấn nút "Tìm kiếm nâng cao"

### 4. Import dữ liệu từ CSV
- Nhấn nút **"Nhập CSV"**  
- Chọn file CSV có format:
```csv
Mã SV,Họ và tên,Ngày sinh,Giới tính,Địa chỉ,Điện thoại,Email,Lớp,Ngành,GPA,Xếp loại
SE182753,Nguyễn Bảo Quân,05/10/2004,Nam,87 Huỳnh Khương An phường 5 quận Gò Vấp TP.Hồ Chí Minh,0903851360,nguyenbaoquan159@gmail.com,JS.NET,Công nghệ thông tin,3.20,Giỏi
```

### 5. Xem thống kê
- Nhấn nút **"Thống kê"**
- Xem báo cáo chi tiết về sinh viên

## 🎨 Giao diện

### Màn hình chính
- **Bảng danh sách sinh viên** với đầy đủ thông tin
- **Thanh công cụ** với các nút chức năng
- **Ô tìm kiếm** và **ComboBox sắp xếp**  
- **Thanh trạng thái** hiển thị tổng số sinh viên

### Các dialog
- **StudentDialog**: Form thêm/sửa sinh viên
- **AdvancedSearchDialog**: Tìm kiếm với nhiều tiêu chí
- **StatisticsDialog**: Hiển thị thống kê chi tiết

## 🔧 Tính năng kỹ thuật

### Xử lý dữ liệu
- **Serialization**: Lưu trữ dữ liệu dạng binary (.dat)
- **CSV Processing**: Xử lý địa chỉ có dấu phẩy phức tạp
- **Data Validation**: Kiểm tra tính hợp lệ của dữ liệu
- **Error Handling**: Xử lý lỗi graceful

### Giao diện
- **Custom Table Renderer**: Hiển thị GPA với 2 chữ số thập phân
- **Responsive Design**: Giao diện thích ứng
- **User-friendly**: Tooltips, confirmations, status messages

### Performance
- **Lazy Loading**: Load dữ liệu khi cần
- **Efficient Search**: Tìm kiếm nhanh với Stream API
- **Memory Management**: Quản lý bộ nhớ tối ưu

## 🧪 Testing

### Test cases đã kiểm tra
- ✅ Thêm/sửa/xóa sinh viên
- ✅ Tìm kiếm và sắp xếp  
- ✅ Import/Export CSV với địa chỉ phức tạp
- ✅ Backup và restore dữ liệu
- ✅ Validation dữ liệu đầu vào
- ✅ Xử lý lỗi và exception

### Dữ liệu test
File CSV mẫu có sẵn với dữ liệu sinh viên thực tế để test import.

## 🚨 Xử lý lỗi phổ biến

### Lỗi "Cannot format given Object as a Number"
**Nguyên nhân**: TableModel trả về String cho cột GPA  
**Giải pháp**: Đã fix trong `StudentTableModel.getValueAt()` - trả về `Double` thay vì `String`

### Lỗi import CSV với địa chỉ có dấu phẩy
**Nguyên nhân**: Địa chỉ Việt Nam có nhiều dấu phẩy làm lệch cột  
**Giải pháp**: Đã implement parser thông minh tự động ghép lại địa chỉ

### Lỗi parse ngày tháng
**Nguyên nhân**: Format ngày không đúng  
**Giải pháp**: Sử dụng `DateTimeFormatter` với pattern `"dd/MM/yyyy"`

## 📊 Metrics

- **~1,500 lines of code** (LOC)
- **8 classes chính** với architecture rõ ràng  
- **20+ features** được implement
- **Cross-platform** compatibility
- **Robust error handling**

## 🤝 Đóng góp

Mọi đóng góp đều được hoan nghênh! Hãy:

1. Fork dự án
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)  
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request

## 📄 License

Dự án này được phân phối dưới MIT License. Xem file `LICENSE` để biết thêm chi tiết.

## 👨‍💻 Tác giả

**[Tên của bạn]**
- GitHub: [@nguyew]
- Email: nguyenbaoquan159@gmail.com

## 🙏 Acknowledgments

- Cảm ơn cộng đồng Java/Swing community
- Inspired by các hệ thống quản lý sinh viên hiện đại
- Icons và design patterns từ Material Design

---
⭐ **Nếu project hữu ích, hãy cho 1 star nhé!** ⭐
