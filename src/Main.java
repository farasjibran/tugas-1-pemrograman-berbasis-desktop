
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    static final String MENU_FILE = "menu.txt";
    static final String STRUK_FILE = "struk.txt";
    static final List<Menu> menu = new ArrayList<>();

    // Memuat data menu dari file
    static {
        if (!loadMenuFromFile()) {
            // Tambahkan menu default jika file tidak ditemukan
            menu.add(new Menu("Nasi Padang", 25000, "Makanan"));
            menu.add(new Menu("Sate Ayam", 20000, "Makanan"));
            menu.add(new Menu("Ayam Goreng", 22000, "Makanan"));
            menu.add(new Menu("Bakso", 15000, "Makanan"));
            menu.add(new Menu("Es Teh", 5000, "Minuman"));
            menu.add(new Menu("Es Jeruk", 7000, "Minuman"));
            menu.add(new Menu("Kopi", 8000, "Minuman"));
            menu.add(new Menu("Jus Jeruk", 15000, "Minuman"));
            System.out.println("File menu tidak ditemukan. Menggunakan menu default.");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== Menu Utama ===");
            System.out.println("1. Pemesanan");
            System.out.println("2. Kelola Menu");
            System.out.println("3. Keluar");
            System.out.print("Pilih menu: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" ->
                    orderProcess(scanner);
                case "2" ->
                    manageMenu(scanner);
                case "3" -> {
                    saveMenuToFile(); // Simpan menu saat keluar
                    running = false;
                    System.out.println("Terima kasih!");
                }
                default ->
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    // Memuat menu dari file
    static boolean loadMenuFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(MENU_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    String name = parts[0].trim();
                    double price = Double.parseDouble(parts[1].trim());
                    String category = parts[2].trim();
                    menu.add(new Menu(name, price, category));
                }
            }
            return true; // File berhasil dibaca
        } catch (FileNotFoundException e) {
            return false; // File tidak ditemukan
        } catch (IOException e) {
            System.out.println("Terjadi kesalahan saat membaca file menu.");
            return false;
        }
    }

    // Menyimpan menu ke file
    static void saveMenuToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MENU_FILE))) {
            for (Menu m : menu) {
                writer.write(m.getNama() + ";" + m.getHarga() + ";" + m.getKategori());
                writer.newLine();
            }
            System.out.println("Menu berhasil disimpan ke file.");
        } catch (IOException e) {
            System.out.println("Gagal menyimpan menu ke file.");
        }
    }

    // Menyimpan struk ke file
    static void saveStrukToFile(String struk) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STRUK_FILE, true))) {
            writer.write(struk);
            writer.newLine();
            writer.newLine();
            System.out.println("Struk berhasil disimpan ke file.");
        } catch (IOException e) {
            System.out.println("Gagal menyimpan struk ke file.");
        }
    }

    // Menampilkan menu
    static void showMenu() {
        for (int i = 0; i < menu.size(); i++) {
            Menu m = menu.get(i);
            System.out.printf("%d. %s - Rp %.2f (%s)\n", i + 1, m.getNama(), m.getHarga(), m.getKategori());
        }
    }

    // Proses pemesanan
    static void orderProcess(Scanner scanner) {
        List<Order> listOrder = new ArrayList<>();
        double subTotal = 0;

        System.out.println("\n=== Daftar Menu ===");
        showMenu();

        while (true) {
            System.out.print("Masukkan pesanan (format: NamaMenu=Jumlah, atau ketik 'selesai'): ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("selesai")) {
                break;
            }

            String[] parts = input.split("=");
            if (parts.length != 2) {
                System.out.println("Format tidak valid. Silakan coba lagi.");
                continue;
            }

            String menuName = parts[0].trim();
            int quantity;
            try {
                quantity = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                System.out.println("Jumlah tidak valid. Silakan coba lagi.");
                continue;
            }

            Menu orderedMenu = menu.stream()
                    .filter(m -> m.getNama().equalsIgnoreCase(menuName))
                    .findFirst()
                    .orElse(null);

            if (orderedMenu == null) {
                System.out.println("Menu tidak ditemukan. Silakan coba lagi.");
                continue;
            }

            listOrder.add(new Order(orderedMenu, quantity));
            subTotal += orderedMenu.getHarga() * quantity;
        }

        double minumanDiscount = applyBuyOneGetOneFree(listOrder, subTotal);
        double total = countTotal(subTotal, minumanDiscount);
        cetakStruk(listOrder, subTotal, total, minumanDiscount);
    }

    // Menghitung total harga
    static double countTotal(double subTotal, double minumanDiscount) {
        double tax = subTotal * 0.1;
        double serviceFees = 20000;
        double discount = subTotal > 100000 ? subTotal * 0.1 : 0;

        return subTotal - discount - minumanDiscount + tax + serviceFees;
    }

    // Cetak struk
    static void cetakStruk(List<Order> listOrder, double subTotal, double total, double minumanDiscount) {
        StringBuilder struk = new StringBuilder();
        struk.append("\n--- STRUK PEMESANAN ---\n");
        struk.append(String.format("%-20s %-10s %-10s %-10s\n", "Nama Menu", "Jumlah", "Harga", "Total"));

        for (Order order : listOrder) {
            struk.append(String.format("%-20s %-10d Rp %-10.2f Rp %-10.2f\n",
                    order.menu.getNama(), order.amount, order.menu.getHarga(), order.getTotalPrice()));
        }

        double tax = subTotal * 0.1;
        double serviceFees = 20000;
        double discount = subTotal > 100000 ? subTotal * 0.1 : 0;

        struk.append("\nSubtotal:         Rp ").append(String.format("%.2f", subTotal)).append("\n");
        struk.append("Pajak (10%):      Rp ").append(String.format("%.2f", tax)).append("\n");
        struk.append("Biaya Pelayanan:  Rp ").append(String.format("%.2f", serviceFees)).append("\n");
        struk.append("Diskon:           Rp -").append(String.format("%.2f", discount)).append("\n");
        if (minumanDiscount > 0) {
            struk.append("Beli 1 Gratis 1:  Rp -").append(String.format("%.2f", minumanDiscount)).append("\n");
        }
        struk.append("Total:            Rp ").append(String.format("%.2f", total)).append("\n");

        System.out.println(struk.toString());
        saveStrukToFile(struk.toString());
    }

    // Logika beli 1 gratis 1
    static double applyBuyOneGetOneFree(List<Order> listOrder, double subTotal) {
        if (subTotal <= 50000) {
            return 0;
        }

        double minumanDiscount = 0;
        Menu termurah = null;

        for (Order order : listOrder) {
            if (order.menu.getKategori().equalsIgnoreCase("Minuman")) {
                if (termurah == null || order.menu.getHarga() < termurah.getHarga()) {
                    termurah = order.menu;
                }
            }
        }

        if (termurah != null) {
            minumanDiscount = termurah.getHarga();
        }

        return minumanDiscount;
    }

    // Kelola menu
    static void manageMenu(Scanner scanner) {
        boolean managing = true;

        while (managing) {
            System.out.println("\n=== Kelola Menu ===");
            System.out.println("1. Tambah Menu");
            System.out.println("2. Ubah Harga Menu");
            System.out.println("3. Hapus Menu");
            System.out.println("4. Kembali ke Menu Utama");
            System.out.print("Pilih menu: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" ->
                    addMenu(scanner);
                case "2" ->
                    updateMenuPrice(scanner);
                case "3" ->
                    deleteMenu(scanner);
                case "4" ->
                    managing = false;
                default ->
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    // Tambah menu
    static void addMenu(Scanner scanner) {
        System.out.print("Masukkan nama menu: ");
        String name = scanner.nextLine();
        System.out.print("Masukkan harga menu: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        System.out.print("Masukkan kategori menu (Makanan/Minuman): ");
        String category = scanner.nextLine();

        menu.add(new Menu(name, price, category));
        System.out.println("Menu berhasil ditambahkan.");
    }

    // Ubah harga menu
    static void updateMenuPrice(Scanner scanner) {
        showMenu();
        System.out.print("Masukkan nama menu yang ingin diubah: ");
        String name = scanner.nextLine();

        Menu selectedMenu = menu.stream()
                .filter(m -> m.getNama().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);

        if (selectedMenu == null) {
            System.out.println("Menu tidak ditemukan.");
            return;
        }

        System.out.print("Masukkan harga baru: ");
        double newPrice = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        selectedMenu.setHarga(newPrice);
        System.out.println("Harga menu berhasil diperbarui.");
    }

    // Hapus menu
    static void deleteMenu(Scanner scanner) {
        showMenu();
        System.out.print("Masukkan nama menu yang ingin dihapus: ");
        String name = scanner.nextLine();

        Menu selectedMenu = menu.stream()
                .filter(m -> m.getNama().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);

        if (selectedMenu == null) {
            System.out.println("Menu tidak ditemukan.");
            return;
        }

        menu.remove(selectedMenu);
        System.out.println("Menu berhasil dihapus.");
    }
}

// Kelas Menu
class Menu {

    private String nama;
    private double harga;
    private String kategori;

    public Menu(String nama, double harga, String kategori) {
        this.nama = nama;
        this.harga = harga;
        this.kategori = kategori;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }
}

// Kelas Order
class Order {

    Menu menu;
    int amount;

    public Order(Menu menu, int amount) {
        this.menu = menu;
        this.amount = amount;
    }

    public double getTotalPrice() {
        return menu.getHarga() * amount;
    }
}
