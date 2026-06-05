package com.dailydose.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * CLASS PENJUALAN (Header Transaksi Barang Keluar)
 * Konsep OOP: Composition — Penjualan "memiliki" daftar DetailPenjualan.
 *   Jika Penjualan dihapus, semua DetailPenjualan-nya ikut hilang.
 */
public class Penjualan {

    private String     idTransaksi;
    private LocalDate  tanggal;
    private double     totalBayar;
    private double     totalKeuntungan; // Tambahan untuk fitur keuntungan
    private int        idUser;    // FK → User (kasir yang melayani)

    // COMPOSITION: Penjualan memiliki banyak DetailPenjualan
    private List<DetailPenjualan> listDetail;

    // =========================================================
    //  CONSTRUCTOR
    // =========================================================
    public Penjualan(String idTransaksi, int idUser) {
        this.idTransaksi = idTransaksi;
        this.tanggal     = LocalDate.now();
        this.idUser      = idUser;
        this.listDetail  = new ArrayList<>();
        this.totalBayar  = 0;
        this.totalKeuntungan = 0;
    }

    // =========================================================
    //  BUSINESS LOGIC
    // =========================================================

    /** Tambahkan item ke keranjang belanja */
    public void tambahDetail(DetailPenjualan detail) {
        listDetail.add(detail);
    }

    /** Hitung total dari semua subtotal item */
    public double hitungTotal() {
        totalBayar = listDetail.stream()
                               .mapToDouble(DetailPenjualan::getSubtotal)
                               .sum();
        totalKeuntungan = listDetail.stream()
                                    .mapToDouble(DetailPenjualan::getKeuntungan)
                                    .sum();
        return totalBayar;
    }

    // =========================================================
    //  GETTER & SETTER
    // =========================================================
    public String getIdTransaksi()                    { return idTransaksi; }
    public LocalDate getTanggal()                     { return tanggal; }
    public double getTotalBayar()                     { return totalBayar; }
    public void setTotalBayar(double totalBayar)      { this.totalBayar = totalBayar; }
    public double getTotalKeuntungan()                { return totalKeuntungan; }
    public void setTotalKeuntungan(double totalKeuntungan) { this.totalKeuntungan = totalKeuntungan; }
    public int getIdUser()                            { return idUser; }
    public List<DetailPenjualan> getListDetail()      { return listDetail; }
}
