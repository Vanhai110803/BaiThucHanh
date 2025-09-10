package quanlysinhvien;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

public class QuanLySinhVien {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/quanlysinhvien";
    private static final String DB_USER = "root"; 
    private static final String DB_PASSWORD = ""; 

    public static class SinhVien {
        private String maSv;
        private String hoTen;
        private Date ngaySinh;
        private String nganhDaoTao;
        private double diemTb;
        private String lopSh;

        public SinhVien(String maSv, String hoTen, Date ngaySinh, String nganhDaoTao, double diemTb, String lopSh) {
            this.maSv = maSv;
            this.hoTen = hoTen;
            this.ngaySinh = ngaySinh;
            this.nganhDaoTao = nganhDaoTao;
            this.diemTb = diemTb;
            this.lopSh = lopSh;
        }

        // Getters và Setters
        public String getMaSv() { return maSv; }
        public void setMaSv(String maSv) { this.maSv = maSv; }
        public String getHoTen() { return hoTen; }
        public void setHoTen(String hoTen) { this.hoTen = hoTen; }
        public Date getNgaySinh() { return ngaySinh; }
        public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }
        public String getNganhDaoTao() { return nganhDaoTao; }
        public void setNganhDaoTao(String nganhDaoTao) { this.nganhDaoTao = nganhDaoTao; }
        public double getDiemTb() { return diemTb; }
        public void setDiemTb(double diemTb) { this.diemTb = diemTb; }
        public String getLopSh() { return lopSh; }
        public void setLopSh(String lopSh) { this.lopSh = lopSh; }

        @Override
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return "Mã SV: " + maSv + ", Họ tên: " + hoTen + ", Ngày sinh: " + sdf.format(ngaySinh) +
                    ", Ngành: " + nganhDaoTao + ", Điểm TB: " + diemTb + ", Lớp SH: " + lopSh;
        }
    }

    public static void main(String[] args) {
        createDatabaseAndTableIfNotExists();
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- Menu Quản Lý Sinh Viên ---");
            System.out.println("1. Thêm sinh viên");
            System.out.println("2. Xóa sinh viên");
            System.out.println("3. Sửa sinh viên");
            System.out.println("4. In danh sách sinh viên cùng lớp sinh hoạt");
            System.out.println("5. In danh sách tất cả sinh viên");
            System.out.println("6. In danh sách sinh viên theo ngành");
            System.out.println("7. In danh sách sinh viên sắp xếp theo điểm trung bình");
            System.out.println("8. In danh sách sinh viên sinh vào một tháng cụ thể");
            System.out.println("9. Thoát");
            System.out.print("Chọn chức năng: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: themSinhVien(scanner); break;
                case 2: xoaSinhVien(scanner); break;
                case 3: suaSinhVien(scanner); break;
                case 4: inSinhVienCungLop(scanner); break;
                case 5: inTatCaSinhVien(); break;
                case 6: inSinhVienTheoNganh(scanner); break;
                case 7: inSinhVienSapXepTheoDiem(); break;
                case 8: inSinhVienTheoThangSinh(scanner); break;
                case 9: System.out.println("Thoát chương trình."); break;
                default: System.out.println("Lựa chọn không hợp lệ.");
            }
        } while (choice != 9);

        scanner.close();
    }

    private static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void createDatabaseAndTableIfNotExists() {
        String createDbQuery = "CREATE DATABASE IF NOT EXISTS quanlysinhvien";
        String useDbQuery = "USE quanlysinhvien";
        String createTableQuery = "CREATE TABLE IF NOT EXISTS sinhvien (" +
                "ma_sv VARCHAR(10) PRIMARY KEY, " +
                "ho_ten VARCHAR(100), " +
                "ngay_sinh DATE, " +
                "nganh_dao_tao VARCHAR(4), " +
                "diem_tb DOUBLE, " +
                "lop_sh VARCHAR(50)" +
                ")";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createDbQuery);
            stmt.executeUpdate(useDbQuery);
            stmt.executeUpdate(createTableQuery);
            System.out.println("Database và table đã được tạo nếu chưa tồn tại.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void themSinhVien(Scanner scanner) {
        System.out.print("Nhập mã sinh viên (10 chữ số, bắt đầu 455105xxxx hoặc 455109xxxx): ");
        String maSv = scanner.nextLine();
        if (!validateMaSv(maSv)) {
            System.out.println("Mã sinh viên không hợp lệ.");
            return;
        }

        System.out.print("Nhập họ tên: ");
        String hoTen = chuanHoaHoTen(scanner.nextLine());

        System.out.print("Nhập ngày sinh (dd/MM/yyyy): ");
        String ngaySinhStr = scanner.nextLine();
        Date ngaySinh = validateNgaySinh(ngaySinhStr);
        if (ngaySinh == null || !validateTuoi(ngaySinh)) {
            System.out.println("Ngày sinh không hợp lệ hoặc tuổi không hợp lý.");
            return;
        }

        System.out.print("Nhập ngành đào tạo (CNTT hoặc KTPM): ");
        String nganh = scanner.nextLine().toUpperCase();
        if (!nganh.equals("CNTT") && !nganh.equals("KTPM")) {
            System.out.println("Ngành đào tạo không hợp lệ.");
            return;
        }
        if ((nganh.equals("CNTT") && !maSv.startsWith("455105")) || (nganh.equals("KTPM") && !maSv.startsWith("455109"))) {
            System.out.println("Mã sinh viên không khớp với ngành.");
            return;
        }

        System.out.print("Nhập điểm trung bình (0.0 - 10.0): ");
        double diemTb = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        if (diemTb < 0.0 || diemTb > 10.0) {
            System.out.println("Điểm trung bình không hợp lệ.");
            return;
        }

        System.out.print("Nhập lớp sinh hoạt: ");
        String lopSh = scanner.nextLine();

        String query = "INSERT INTO sinhvien (ma_sv, ho_ten, ngay_sinh, nganh_dao_tao, diem_tb, lop_sh) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, maSv);
            pstmt.setString(2, hoTen);
            pstmt.setDate(3, new java.sql.Date(ngaySinh.getTime()));
            pstmt.setString(4, nganh);
            pstmt.setDouble(5, diemTb);
            pstmt.setString(6, lopSh);
            pstmt.executeUpdate();
            System.out.println("Thêm sinh viên thành công.");
        } catch (SQLException e) {
            System.out.println("Lỗi khi thêm sinh viên: " + e.getMessage());
        }
    }

    private static void xoaSinhVien(Scanner scanner) {
        System.out.print("Nhập mã sinh viên cần xóa: ");
        String maSv = scanner.nextLine();

        String query = "DELETE FROM sinhvien WHERE ma_sv = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, maSv);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Xóa sinh viên thành công.");
            } else {
                System.out.println("Không tìm thấy sinh viên.");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi xóa: " + e.getMessage());
        }
    }

    private static void suaSinhVien(Scanner scanner) {
        System.out.print("Nhập mã sinh viên cần sửa: ");
        String maSv = scanner.nextLine();

        SinhVien sv = getSinhVienByMa(maSv);
        if (sv == null) {
            System.out.println("Không tìm thấy sinh viên.");
            return;
        }

        System.out.println("Thông tin hiện tại: " + sv);
        System.out.println("Nhập thông tin mới (nhấn Enter để giữ nguyên)");

        System.out.print("Họ tên mới: ");
        String hoTen = scanner.nextLine();
        if (!hoTen.isEmpty()) sv.setHoTen(chuanHoaHoTen(hoTen));

        System.out.print("Ngày sinh mới (dd/MM/yyyy): ");
        String ngaySinhStr = scanner.nextLine();
        if (!ngaySinhStr.isEmpty()) {
            Date ngaySinh = validateNgaySinh(ngaySinhStr);
            if (ngaySinh != null && validateTuoi(ngaySinh)) {
                sv.setNgaySinh(ngaySinh);
            } else {
                System.out.println("Ngày sinh không hợp lệ, giữ nguyên.");
            }
        }

        System.out.print("Ngành đào tạo mới (CNTT hoặc KTPM): ");
        String nganh = scanner.nextLine().toUpperCase();
        if (!nganh.isEmpty()) {
            if (nganh.equals("CNTT") || nganh.equals("KTPM")) {
                sv.setNganhDaoTao(nganh);
            } else {
                System.out.println("Ngành không hợp lệ, giữ nguyên.");
            }
        }

        System.out.print("Điểm TB mới: ");
        String diemStr = scanner.nextLine();
        if (!diemStr.isEmpty()) {
            try {
                double diemTb = Double.parseDouble(diemStr);
                if (diemTb >= 0.0 && diemTb <= 10.0) {
                    sv.setDiemTb(diemTb);
                } else {
                    System.out.println("Điểm không hợp lệ, giữ nguyên.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Điểm không hợp lệ, giữ nguyên.");
            }
        }

        System.out.print("Lớp SH mới: ");
        String lopSh = scanner.nextLine();
        if (!lopSh.isEmpty()) sv.setLopSh(lopSh);

        String query = "UPDATE sinhvien SET ho_ten = ?, ngay_sinh = ?, nganh_dao_tao = ?, diem_tb = ?, lop_sh = ? WHERE ma_sv = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, sv.getHoTen());
            pstmt.setDate(2, new java.sql.Date(sv.getNgaySinh().getTime()));
            pstmt.setString(3, sv.getNganhDaoTao());
            pstmt.setDouble(4, sv.getDiemTb());
            pstmt.setString(5, sv.getLopSh());
            pstmt.setString(6, maSv);
            pstmt.executeUpdate();
            System.out.println("Sửa sinh viên thành công.");
        } catch (SQLException e) {
            System.out.println("Lỗi khi sửa: " + e.getMessage());
        }
    }

    private static void inSinhVienCungLop(Scanner scanner) {
        System.out.print("Nhập lớp sinh hoạt: ");
        String lopSh = scanner.nextLine();

        String query = "SELECT * FROM sinhvien WHERE lop_sh = ?";
        inDanhSach(query, lopSh);
    }

    private static void inTatCaSinhVien() {
        String query = "SELECT * FROM sinhvien";
        inDanhSach(query);
    }

    private static void inSinhVienTheoNganh(Scanner scanner) {
        System.out.print("Nhập ngành (CNTT hoặc KTPM): ");
        String nganh = scanner.nextLine().toUpperCase();

        String query = "SELECT * FROM sinhvien WHERE nganh_dao_tao = ?";
        inDanhSach(query, nganh);
    }

    private static void inSinhVienSapXepTheoDiem() {
        String query = "SELECT * FROM sinhvien ORDER BY diem_tb DESC";
        inDanhSach(query);
    }

    private static void inSinhVienTheoThangSinh(Scanner scanner) {
        System.out.print("Nhập tháng sinh (1-12): ");
        int thang = scanner.nextInt();
        scanner.nextLine();

        if (thang < 1 || thang > 12) {
            System.out.println("Tháng không hợp lệ.");
            return;
        }

        String query = "SELECT * FROM sinhvien WHERE MONTH(ngay_sinh) = ?";
        inDanhSach(query, String.valueOf(thang));
    }

    private static void inDanhSach(String query, String... params) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setString(i + 1, params[i]);
            }
            ResultSet rs = pstmt.executeQuery();
            System.out.println("\nDanh sách sinh viên:");
            while (rs.next()) {
                System.out.println("Mã SV: " + rs.getString("ma_sv") +
                        ", Họ tên: " + rs.getString("ho_ten") +
                        ", Ngày sinh: " + rs.getDate("ngay_sinh") +
                        ", Ngành: " + rs.getString("nganh_dao_tao") +
                        ", Điểm TB: " + rs.getDouble("diem_tb") +
                        ", Lớp SH: " + rs.getString("lop_sh"));
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi in danh sách: " + e.getMessage());
        }
    }

    private static boolean validateMaSv(String maSv) {
        return Pattern.matches("45510(5|9)\\d{4}", maSv) && maSv.length() == 10;
    }

    private static String chuanHoaHoTen(String hoTen) {
        hoTen = hoTen.trim().replaceAll("\\s+", " ");
        String[] words = hoTen.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }

    private static Date validateNgaySinh(String ngaySinhStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try {
            return sdf.parse(ngaySinhStr);
        } catch (ParseException e) {
            System.out.println("Định dạng ngày sinh không hợp lệ. Vui lòng nhập theo dạng dd/MM/yyyy.");
            return null;
        }
    }

    private static boolean validateTuoi(Date ngaySinh) {
        if (ngaySinh == null) return false;
        LocalDate birthDate = ngaySinh.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now();
        int tuoi = Period.between(birthDate, now).getYears();
        return tuoi >= 15 && tuoi <= 110;
    }

    private static SinhVien getSinhVienByMa(String maSv) {
        String query = "SELECT * FROM sinhvien WHERE ma_sv = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, maSv);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date ngaySinh = sdf.parse(sdf.format(rs.getDate("ngay_sinh")));
                return new SinhVien(
                    rs.getString("ma_sv"),
                    rs.getString("ho_ten"),
                    ngaySinh,
                    rs.getString("nganh_dao_tao"),
                    rs.getDouble("diem_tb"),
                    rs.getString("lop_sh")
                );
            }
            return null;
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}