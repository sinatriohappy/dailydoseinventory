package com.dailydose.model;

/**
 * CLASS DETAIL PEMBELIAN (Line Item Barang Masuk dari Supplier)
 */
public class DetailPembelian {

    private int    idDetailBeli;
    private int    idPembelian;  // FK → Pembelian
    private String idBarang;     // FK → Barang
    private String namaBarang;
    private int    jumlah;
    private double hargaBeli;    // harga modal dari supplier
    private double subtotal;     // jumlah × hargaBeli

    // =========================================================
    //  CONSTRUCTOR (baru, belum ada ID)
    // =========================================================
    public DetailPembelian(int idPembelian, String idBarang,
                            String namaBarang, int jumlah, double hargaBeli) {
        this.idPembelian = idPembelian;
        this.idBarang    = idBarang;
        this.namaBarang  = namaBarang;
        this.jumlah      = jumlah;
        this.hargaBeli   = hargaBeli;
        this.subtotal    = jumlah * hargaBeli;
    }

    /** Constructor untuk load dari database */
    public DetailPembelian(int idDetailBeli, int idPembelian, String idBarang,
                            String namaBarang, int jumlah, double hargaBeli) {
        this.idDetailBeli = idDetailBeli;
        this.idPembelian  = idPembelian;
        this.idBarang     = idBarang;
        this.namaBarang   = namaBarang;
        this.jumlah       = jumlah;
        this.hargaBeli    = hargaBeli;
        this.subtotal     = jumlah * hargaBeli;
    }

    // =========================================================
    //  GETTER
    // =========================================================
    public int    getIdDetailBeli()   { return idDetailBeli; }
    public int    getIdPembelian()    { return idPembelian; }
    public String getIdBarang()       { return idBarang; }
    public String getNamaBarang()     { return namaBarang; }
    public int    getJumlah()         { return jumlah; }
    public double getHargaBeli()      { return hargaBeli; }
    public double getSubtotal()       { return subtotal; }
}
