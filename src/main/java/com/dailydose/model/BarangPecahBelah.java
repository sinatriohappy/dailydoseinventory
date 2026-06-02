package com.dailydose.model;

/**
 * CLASS BARANG PECAH BELAH
 * Konsep OOP: Inheritance — mewarisi semua atribut & method dari Barang.
 * Menambahkan atribut spesifik: material (misal: "Kaca", "Keramik")
 */
public class BarangPecahBelah extends Barang {

    // Atribut KHUSUS subclass ini (tidak ada di class Barang)
    private String material;

    // =========================================================
    //  CONSTRUCTOR
    // =========================================================
    public BarangPecahBelah(String idBarang, String namaBarang,
                             double harga, double hargaBeli, int stok, String material) {
        // Panggil constructor parent (Barang) dengan super()
        super(idBarang, namaBarang, "PecahBelah", harga, hargaBeli, stok);
        this.material = material;
    }

    // =========================================================
    //  OVERRIDE abstract method dari parent
    // =========================================================
    @Override
    public String getInfoKategori() {
        return "Kategori: Barang Pecah Belah | Material: " + material;
    }

    // =========================================================
    //  GETTER & SETTER khusus subclass
    // =========================================================
    public String getMaterial()                { return material; }
    public void setMaterial(String material)   { this.material = material; }
}
