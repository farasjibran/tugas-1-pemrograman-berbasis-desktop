public class Order {
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