import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// Lớp sản phẩm
class Product {
    private int id;
    private String name;
    private String category;
    private double price;
    private String description;
    private int quantity;

    public Product(int id, String name, String category, double price, String description, int quantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.quantity = quantity;
    }

    // Getter - Setter
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }

    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // Hàm giảm giá
    public void applyDiscount(double percent) {
        if (percent > 0 && percent <= 100) {
            this.price = this.price - (this.price * percent / 100);
        }
    }

    // Giá trị tồn kho
    public double getInventoryValue() {
        return price * quantity;
    }

    @Override
    public String toString() {
        return String.format("ID:%d | %s | Danh mục:%s | Giá:%.0f | SL:%d | %s",
                id, name, category, price, quantity, description);
    }
}

// Lớp quản lý cửa hàng
class Store {
    private List<Product> products = new ArrayList<>();
    private Scanner sc = new Scanner(System.in);

    // Thêm dữ liệu mẫu
    public void addSampleProducts() {
        products.add(new Product(1, "Áo thun", "Thời trang", 100000, "Áo cotton thoáng mát", 50));
        products.add(new Product(2, "Quần jean", "Thời trang", 250000, "Quần jean nam xanh", 30));
        products.add(new Product(3, "Áo sơ mi", "Thời trang", 180000, "Áo sơ mi trắng công sở", 40));
        products.add(new Product(4, "Laptop", "Điện tử", 15000000, "Laptop gaming RTX", 10));
        products.add(new Product(5, "Tai nghe", "Điện tử", 500000, "Tai nghe Bluetooth", 25));
        products.add(new Product(6, "Điện thoại", "Điện tử", 12000000, "Điện thoại Android mới", 15));
        products.add(new Product(7, "Bánh quy", "Thực phẩm", 30000, "Bánh quy bơ", 100));
        products.add(new Product(8, "Sữa tươi", "Thực phẩm", 25000, "Sữa tươi hộp 1L", 60));
        products.add(new Product(9, "Nồi cơm điện", "Gia dụng", 800000, "Nồi cơm điện Sharp", 20));
        products.add(new Product(10, "Quạt máy", "Gia dụng", 450000, "Quạt đứng 3 tốc độ", 35));
    }

    // Cập nhật sản phẩm
    public void updateProduct() {
        System.out.print("Nhập ID sản phẩm cần cập nhật: ");
        int id = sc.nextInt();
        sc.nextLine();
        for (Product p : products) {
            if (p.getId() == id) {
                System.out.print("Nhập giá mới: ");
                double price = sc.nextDouble();
                sc.nextLine();
                System.out.print("Nhập mô tả mới: ");
                String desc = sc.nextLine();
                System.out.print("Nhập số lượng mới: ");
                int qty = sc.nextInt();

                p.setPrice(price);
                p.setDescription(desc);
                p.setQuantity(qty);
                System.out.println(" Cập nhật thành công!");
                return;
            }
        }
        System.out.println(" Không tìm thấy sản phẩm!");
    }

    // Hiển thị theo giá
    public void showProductsByPrice() {
        System.out.print("Nhập mức giá tối thiểu: ");
        double minPrice = sc.nextDouble();
        sc.nextLine();

        System.out.println(" Danh sách sản phẩm có giá >= " + minPrice + ":");
        products.stream()
                .filter(p -> p.getPrice() >= minPrice)
                .sorted(Comparator.comparing(Product::getPrice))
                .forEach(System.out::println);
    }

    // Hiển thị theo danh mục
    public void showProductsByCategory() {
        sc.nextLine(); // bỏ ký tự Enter còn lại
        System.out.print("Nhập tên danh mục cần xem: ");
        String cate = sc.nextLine();

        System.out.println(" Danh sách sản phẩm thuộc danh mục \"" + cate + "\":");
        products.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(cate))
                .forEach(System.out::println);
    }
 // Tính tổng giá trị tồn kho
    public void calculateInventoryValue() {
        Map<String, Long> map = new HashMap<>();
        for (Product p : products) {
            long value = (long) (p.getInventoryValue());
            map.put(p.getCategory(), map.getOrDefault(p.getCategory(), 0L) + value);
        }

        System.out.println(" Tổng giá trị tồn kho theo danh mục:");
        for (String c : map.keySet()) {
            System.out.printf("- %s: %,d%n", c, map.get(c)); 
            // %,d để có dấu phẩy phân cách hàng nghìn
        }
    }
    
    
    
    // Giảm giá
    public void applyDiscount() {
        System.out.print("Nhập ID sản phẩm cần giảm giá: ");
        int id = sc.nextInt();
        System.out.print("Nhập phần trăm giảm giá (%): ");
        double percent = sc.nextDouble();

        for (Product p : products) {
            if (p.getId() == id) {
                double oldPrice = p.getPrice();
                p.applyDiscount(percent);  // cập nhật giá mới
                System.out.println(" Giảm giá thành công!");
                System.out.println("Giá cũ: " + oldPrice + " -> Giá mới: " + p.getPrice());
                return;
            }
        }
        System.out.println(" Không tìm thấy sản phẩm!");
    }

    // Đặt hàng
    public void placeOrder() {
        System.out.print("Nhập ID sản phẩm cần mua: ");
        int id = sc.nextInt();
        System.out.print("Nhập số lượng: ");
        int qty = sc.nextInt();
        for (Product p : products) {
            if (p.getId() == id) {
                if (p.getQuantity() >= qty) {
                    double total = p.getPrice() * qty;
                    p.setQuantity(p.getQuantity() - qty);
                    System.out.println(" Đặt hàng thành công! Tổng tiền: " + total);
                } else {
                    System.out.println(" Không đủ hàng trong kho!");
                }
                return;
            }
        }
        System.out.println(" Không tìm thấy sản phẩm!");
    }

    // Menu
    public void menu() {
        int choice;
        do {
            System.out.println("\n==== QUẢN LÝ CỬA HÀNG ====");
            System.out.println("1. Cập nhật sản phẩm");
            System.out.println("2. Hiển thị sản phẩm theo giá");
            System.out.println("3. Hiển thị sản phẩm theo danh mục");
            System.out.println("4. Tính tổng giá trị tồn kho");
            System.out.println("5. Giảm giá sản phẩm");
            System.out.println("6. Đặt hàng");
            System.out.println("7. Xem tất cả sản phẩm");
            System.out.println("0. Thoát");
            System.out.print(" Chọn: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1: updateProduct(); break;
                case 2: showProductsByPrice(); break;
                case 3: showProductsByCategory(); break;
                case 4: calculateInventoryValue(); break;
                case 5: applyDiscount(); break;
                case 6: placeOrder(); break;
                case 7: products.forEach(System.out::println); break;
                case 0: System.out.println(" Thoát chương trình."); break;
                default: System.out.println(" Lựa chọn không hợp lệ!");
            }
        } while (choice != 0);
    }
}

// Class chạy chính
public class Main {
    public static void main(String[] args) {
        Store store = new Store();
        store.addSampleProducts(); // Đổ dữ liệu mẫu
        store.menu();              // Hiển thị menu
    }
}
