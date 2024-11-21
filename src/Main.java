import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Jibran
 */
public class Main {
    static final List<Menu> menu = new ArrayList<>();

    static {
        menu.add(new Menu("Nasi Padang", 25000, "Makanan"));
        menu.add(new Menu("Sate Ayam", 20000, "Makanan"));
        menu.add(new Menu("Ayam Goreng", 22000, "Makanan"));
        menu.add(new Menu("Bakso", 15000, "Makanan"));
        menu.add(new Menu("Es Teh", 5000, "Minuman"));
        menu.add(new Menu("Es Jeruk", 7000, "Minuman"));
        menu.add(new Menu("Kopi", 8000, "Minuman"));
        menu.add(new Menu("Jus Jeruk", 15000, "Minuman"));
    }

    /**
     * Run the Program
     * 
     * @author Jibran
     *
     * @return  void
     */
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
                case "1" -> orderProcess(scanner);
                case "2" -> manageMenu(scanner);
                case "3" -> {
                    running = false;
                    System.out.println("Terima kasih!");
                }
                default -> System.out.println("Pilihan tidak valid.");
            }
        }
    }

    /**
     * Show List Menu
     *
     * @author Jibran
     * 
     * @return  Menu[]
     */
    static void showMenu() {
        for (int i = 0; i < menu.size(); i++) {
            Menu m = menu.get(i);
            System.out.printf("%d. %s - Rp %.2f (%s)\n", i + 1, m.getNama(), m.getHarga(), m.getKategori());
        }
    }

    /**
     * Count The Total Of Order 
     *
     * @author Jibran
     * 
     * @param   double  subTotal
     *
     * @return  double
     */
    static double countTotal(double subTotal, double minumanDiscount) {
        double tax = subTotal * 0.1;
        double serviceFees = 20000;
        double discount = subTotal > 100000 ? subTotal * 0.1 : 0;

        return subTotal - discount - minumanDiscount + tax + serviceFees;
    }

    /**
     * Print The Struk After Complete The Order
     *
     * @author Jibran
     * 
     * @param   double  subTotal
     * @param   double  total
     *
     * @return  void
     */
    static void cetakStruk(List<Order> listOrder, double subTotal, double total, double minumanDiscount) {
        System.out.println("\n--- STRUK PEMESANAN ---");
        System.out.printf("%-20s %-10s %-10s %-10s\n", "Nama Menu", "Jumlah", "Harga", "Total");

        for (Order order : listOrder) {
            System.out.printf("%-20s %-10d Rp %-10.2f Rp %-10.2f\n",
                    order.menu.getNama(), order.amount, order.menu.getHarga(), order.getTotalPrice());
        }

        double tax = subTotal * 0.1;
        double serviceFees = 20000;
        double discount = subTotal > 100000 ? subTotal * 0.1 : 0;

        System.out.println("\nSubtotal:         Rp " + String.format("%.2f", subTotal));
        System.out.println("Pajak (10%):      Rp " + String.format("%.2f", tax));
        System.out.println("Biaya Pelayanan:  Rp " + String.format("%.2f", serviceFees));
        System.out.println("Diskon:           Rp -" + String.format("%.2f", discount));
        if (minumanDiscount > 0) {
            System.out.println("Beli 1 Gratis 1:  Rp -" + String.format("%.2f", minumanDiscount));
        }
        System.out.println("Total:            Rp " + String.format("%.2f", total));
    }

    /**
     * Apply The Logic Buy One Get One Free
     *
     * @author Jibran
     * 
     * @return  double
     */
    static double applyBuyOneGetOneFree(List<Order> listOrder, double subTotal) {
        if (subTotal <= 50000) return 0;

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


    /**
     * The Function to Process the Order
     *
     * @author Jibran
     * 
     * @return  void
     */
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

    /**
     * Manage the menu first
     *
     * @author Jibran
     * 
     * @return  void
     */
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
                case "1" -> addMenu(scanner);
                case "2" -> updateMenuPrice(scanner);
                case "3" -> deleteMenu(scanner);
                case "4" -> managing = false;
                default -> System.out.println("Pilihan tidak valid.");
            }
        }
    }

    /**
     * Add menu to array
     *
     * @author Jibran
     * 
     * @return  void
     */
    static void addMenu(Scanner scanner) {
        System.out.print("Masukkan nama menu: ");
        String name = scanner.nextLine();
        System.out.print("Masukkan harga menu: ");
        double price;
        try {
            price = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Harga tidak valid.");
            return;
        }
        System.out.print("Masukkan kategori menu (Makanan/Minuman): ");
        String category = scanner.nextLine();

        menu.add(new Menu(name, price, category));
        System.out.println("Menu berhasil ditambahkan.");
    }

    /**
     * Update the menu price function
     *
     * @author Jibran
     * 
     * @return  void
     */
    static void updateMenuPrice(Scanner scanner) {
        showMenu();
        System.out.print("Pilih nomor menu yang ingin diubah: ");
        int index;
        try {
            index = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Pilihan tidak valid.");
            return;
        }

        if (index < 0 || index >= menu.size()) {
            System.out.println("Menu tidak ditemukan.");
            return;
        }

        System.out.print("Masukkan harga baru: ");
        double newPrice;
        try {
            newPrice = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Harga tidak valid.");
            return;
        }

        menu.get(index).setHarga(newPrice);
        System.out.println("Harga menu berhasil diubah.");
    }

    /**
     * Function to delete menu in array
     *
     * @author Jibran
     * 
     * @return  void
     */
    static void deleteMenu(Scanner scanner) {
        showMenu();
        System.out.print("Pilih nomor menu yang ingin dihapus: ");
        int index;
        try {
            index = Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Pilihan tidak valid.");
            return;
        }

        if (index < 0 || index >= menu.size()) {
            System.out.println("Menu tidak ditemukan.");
            return;
        }

        System.out.print("Apakah Anda yakin ingin menghapus menu ini? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (confirmation.equals("yes")) {
            menu.remove(index);
            System.out.println("Menu berhasil dihapus.");
        } else {
            System.out.println("Penghapusan dibatalkan.");
        }
    }
}
