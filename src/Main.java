import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Jibran
 */
public class Main {
    static Menu[] menu = {
        new Menu("Nasi Padang", 25000, "Makanan"),
        new Menu("Sate Ayam", 20000, "Makanan"),
        new Menu("Ayam Goreng", 22000, "Makanan"),
        new Menu("Bakso", 15000, "Makanan"),
        new Menu("Es Teh", 5000, "Minuman"),
        new Menu("Es Jeruk", 7000, "Minuman"),
        new Menu("Kopi", 8000, "Minuman"),
        new Menu("Jus Jeruk", 15000, "Minuman")
    };

    /**
     * Run the Program
     * 
     * @author Jibran
     *
     * @return  void
     */
    public static void main(String[] args) {
        showMenu();
        orderProcess();
    }

    /**
     * Show List Menu
     *
     * @author Jibran
     * 
     * @return  Menu[]
     */
    static void showMenu() {
        System.out.println("=== Daftar Menu ===");
        for (Menu m : menu) {
            System.out.println(m.getKategori() + " - " + m.getNama() + ": Rp " + m.getHarga());
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
    static double countTotal(double subTotal) {
        double tax = subTotal * 0.1;
        double serviceFees = 20000;
        double discount = 0;

        if (subTotal > 100000) {
            discount = subTotal * 0.1;
        }

        return subTotal - discount + tax + serviceFees;
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
    static void cetakStruk(List<Order> listOrder, double subTotal, double total) {
        System.out.println("\n--- STRUK PEMESANAN ---");
        System.out.printf("%-15s %-10s %-10s %-10s\n", "Nama Menu", "Jumlah", "Harga", "Total");

        double minumanDiscount = 0;
        
        // Check if eligible for buy one get one free for drinks
        if (subTotal > 50000) {
            minumanDiscount = applyBuyOneGetOneFree(listOrder);
        }

        for (Order order : listOrder) {
            System.out.printf("%-15s %-10d Rp %-9.2f Rp %-9.2f\n",
                              order.menu.getNama(),
                              order.amount,
                              order.menu.getHarga(),
                              order.getTotalPrice());
        }

        double tax = subTotal * 0.1;
        double serviceFees = 20000;
        double discount = subTotal > 100000 ? subTotal * 0.1 : 0;

        System.out.println("\nTotal Biaya:     Rp " + String.format("%.2f", subTotal));
        System.out.println("Pajak (10%):     Rp " + String.format("%.2f", tax));
        System.out.println("Biaya Pelayanan: Rp " + String.format("%.2f", serviceFees));
        System.out.println("Diskon:          Rp " + String.format("%.2f", discount));
        if (minumanDiscount > 0) {
            System.out.println("Penawaran Beli 1 Gratis 1 untuk Minuman: Rp -" + String.format("%.2f", minumanDiscount));
        }
        System.out.println("Total Setelah Diskon: Rp " + String.format("%.2f", total - minumanDiscount));
    }

    /**
     * Apply The Logic Buy One Get One Free
     *
     * @author Jibran
     * 
     * @return  double
     */
    static double applyBuyOneGetOneFree(List<Order> listOrder) {
        double minumanDiscount = 0;
        Order minumanTermurah = null;

        // Find the cheapest drink and make it free
        for (Order order : listOrder) {
            if (order.menu.getKategori().equalsIgnoreCase("Minuman")) {
                if (minumanTermurah == null || order.menu.getHarga() < minumanTermurah.menu.getHarga()) {
                    minumanTermurah = order;
                }
            }
        }

        if (minumanTermurah != null) {
            minumanDiscount = minumanTermurah.menu.getHarga();
        }

        return minumanDiscount;
    }

    /**
     * The Function to Process the Order
     *
     * @author Jibran
     * 
     * @return  void    [return description]
     */
    static void orderProcess() {
        try (Scanner scanner = new Scanner(System.in)) {
            List<Order> listOrder = new ArrayList<>();
            double subTotal = 0;
            boolean addMoreOrders = true;

            while (addMoreOrders) {
                System.out.print("Masukkan pesanan (format: NamaMenu = Jumlah): ");
                String input = scanner.nextLine();

                // Parse the input for "MenuName = Quantity" format
                String[] parts = input.split("=");
                if (parts.length != 2) {
                    System.out.println("Format tidak valid. Silakan coba lagi.");
                    continue;
                }

                String menuName = parts[0].trim();
                int totalOrder;
                try {
                    totalOrder = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    System.out.println("Jumlah tidak valid. Silakan masukkan angka.");
                    continue;
                }

                // Check if the menu item exists in the listOrder
                boolean found = false;
                for (Order order : listOrder) {
                    if (order.menu.getNama().equalsIgnoreCase(menuName)) {
                        order.amount += totalOrder; // Update the quantity if the menu is already in the order
                        found = true;
                        break;
                    }
                }

                // If menu item is not found in the existing orders, add a new order
                if (!found) {
                    boolean menuExists = false;
                    for (Menu m : menu) {
                        if (m.getNama().equalsIgnoreCase(menuName)) {
                            Order order = new Order(m, totalOrder);
                            listOrder.add(order);
                            menuExists = true;
                            break;
                        }
                    }
                    if (!menuExists) {
                        System.out.println("Menu tidak ditemukan. Silakan coba lagi.");
                        continue;
                    }
                }

                // Ask if the user wants to add another order
                System.out.print("Apakah Anda ingin menambah pesanan lain? (yes/no): ");
                String response = scanner.nextLine().trim().toLowerCase();
                if (!response.equals("yes")) {
                    addMoreOrders = false;
                }
            }

            // Calculate subtotal
            for (Order order : listOrder) {
                subTotal += order.getTotalPrice();
            }

            double total = countTotal(subTotal);
            cetakStruk(listOrder, subTotal, total);
        }
    }
}
