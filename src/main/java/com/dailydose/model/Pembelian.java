package com.dailydose.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * CLASS PEMBELIAN (Header Transaksi Barang Masuk / Restock)
 * Konsep OOP: Composition — memiliki daftar DetailPembelian.
 */
public class Pembelian {

    private int       idPembelian;
    private LocalDate tanggal;
    private double    totalPembayaran;
    private int       idUser;
    private String    namaToko;

    // COMPOSITION
    private List<DetailPembelian> listDetail;

    // =========================================================
    //  CONSTRUCTOR
    // =========================================================
    public Pembelian(int idUser, String namaToko) {
        this.tanggal          = LocalDate.now();
        this.idUser           = idUser;
        this.namaToko         = namaToko;
        this.listDetail       = new ArrayList<>();
        this.totalPembayaran  = 0;
    }

    // =========================================================
    //  BUSINESS LOGIC
    // =========================================================
    public void tambahDetail(DetailPembelian detail) {
        listDetail.add(detail);
    }

    public double hitungTotalBeli() {
        totalPembayaran = listDetail.stream()
                                    .mapToDouble(DetailPembelian::getSubtotal)
                                    .sum();
        return totalPembayaran;
    }

    // =========================================================
    //  GETTER & SETTER
    // =========================================================
    public int       getIdPembelian()                          { return idPembelian; }
    public void      setIdPembelian(int idPembelian)           { this.idPembelian = idPembelian; }
    public LocalDate getTanggal()                              { return tanggal; }
    public double    getTotalPembayaran()                      { return totalPembayaran; }
    public void      setTotalPembayaran(double t)              { this.totalPembayaran = t; }
    public int       getIdUser()                               { return idUser; }
    public String    getNamaToko()                             { return namaToko; }
    public void      setNamaToko(String n)                     { this.namaToko = n; }
    public List<DetailPembelian> getListDetail()               { return listDetail; }
}
