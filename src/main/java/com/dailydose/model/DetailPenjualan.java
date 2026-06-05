package com.dailydose.model;

/**
 * CLASS DETAIL PENJUALAN (Line Item Barang Keluar)
 * Merepresentasikan satu baris item dalam sebuah transaksi penjualan.
 * Konsep OOP: Composition — bagian dari Penjualan.
 */
public class DetailPenjualan {

    private int    idDetail;
    private String idTransaksi;  // FK → Penjualan
    private String idBarang;     // FK → Barang
    private String namaBarang;   // disimpan untuk kemudahan tampil di tabel
    private int    jumlah;
    private double hargaBeli;    // modal saat penjualan terjadi
    private double subtotal;     // jumlah × harga
    private double keuntungan;   // (hargaJual - hargaBeli) * jumlah

    // =========================================================
    //  CONSTRUCTOR
    // =========================================================
    public DetailPenjualan(String idTransaksi, String idBarang,
                            String namaBarang, int jumlah, double hargaJual, double hargaBeli) {
        this.idTransaksi = idTransaksi;
        this.idBarang    = idBarang;
        this.namaBarang  = namaBarang;
        this.jumlah      = jumlah;
        this.hargaBeli   = hargaBeli;
        this.subtotal    = jumlah * hargaJual;
        this.keuntungan  = (hargaJual - hargaBeli) * jumlah;
    }

    /** Constructor untuk load dari database (sudah ada idDetail) */
    public DetailPenjualan(int idDetail, String idTransaksi, String idBarang,
                            String namaBarang, int jumlah, double subtotal, double hargaBeli, double keuntungan) {
        this.idDetail    = idDetail;
        this.idTransaksi = idTransaksi;
        this.idBarang    = idBarang;
        this.namaBarang  = namaBarang;
        this.jumlah      = jumlah;
        this.subtotal    = subtotal;
        this.hargaBeli   = hargaBeli;
        this.keuntungan  = keuntungan;
    }

    // =========================================================
    //  GETTER & SETTER
    // =========================================================
    public int    getIdDetail()       { return idDetail; }
    public String getIdTransaksi()    { return idTransaksi; }
    public String getIdBarang()       { return idBarang; }
    public String getNamaBarang()     { return namaBarang; }
    public int    getJumlah()         { return jumlah; }
    public double getSubtotal()       { return subtotal; }
    public double getHargaBeli()      { return hargaBeli; }
    public double getKeuntungan()     { return keuntungan; }
}
