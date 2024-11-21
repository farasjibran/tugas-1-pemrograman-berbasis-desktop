public class Menu {
  private final String nama;
  private double harga;
  private final String kategori;

  public Menu(String nama, double harga, String kategori) {
    this.nama = nama;
    this.harga = harga;
    this.kategori = kategori;
  }

  public String getNama() {
    return nama;
  }

  public double getHarga() {
    return harga;
  }

  public String getKategori() {
    return kategori;
  }

  public void setHarga(double harga) {
    this.harga = harga;
}
}
