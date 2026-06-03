package com.dailydose.controller;

import com.dailydose.dao.BarangDAO;
import com.dailydose.model.Barang;
import com.dailydose.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * DASHBOARD CONTROLLER
 * Halaman utama setelah login. Menampilkan ringkasan stok kritis
 * dan menyediakan navigasi ke semua fitur aplikasi.
 */
public class DashboardController implements Initializable {

    @FXML private Label lblSambutan;
    @FXML private Label lblStokKritis;
    @FXML private Label lblKeuntungan;

    private final BarangDAO barangDAO = new BarangDAO();
    private final com.dailydose.dao.PenjualanDAO penjualanDAO = new com.dailydose.dao.PenjualanDAO();

    // =========================================================
    //  INITIALIZE — dipanggil otomatis setelah FXML di-load
    // =========================================================
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Tampilkan nama user yang sedang login
        String namaUser = SessionManager.getInstance().getCurrentUser().getUsername();
        String role     = SessionManager.getInstance().getCurrentUser().getRole();
        if (namaUser.equalsIgnoreCase("admin")) {
            role = "Owner";
        }
        lblSambutan.setText("Selamat Datang, " + namaUser + "! (" + role + ")");

        // Hitung barang dengan stok ≤ 5 (stok kritis)
        muatRingkasanStok();
        
        // Muat total keuntungan (Hanya Admin yang boleh lihat omset/keuntungan)
        if (SessionManager.getInstance().isAdmin()) {
            muatTotalKeuntungan();
        } else {
            lblKeuntungan.setText("🔒 Rahasia Perusahaan (Hanya Admin/Owner)");
            lblKeuntungan.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 14px; -fx-font-style: italic;");
        }
    }

    private void muatRingkasanStok() {
        List<Barang> semua = barangDAO.getAllBarang();
        List<Barang> kritis = semua.stream()
                                   .filter(b -> b.getStok() <= 5)
                                   .collect(Collectors.toList());

        if (kritis.isEmpty()) {
            lblStokKritis.setText("✅ Semua stok aman.");
            lblStokKritis.setStyle("-fx-text-fill: green;");
        } else {
            String namaKritis = kritis.stream()
                                      .map(Barang::getNamaBarang)
                                      .collect(Collectors.joining(", "));
            lblStokKritis.setText("⚠️ " + kritis.size() + " barang stok kritis: " + namaKritis);
        }
    }

    private void muatTotalKeuntungan() {
        double total = penjualanDAO.getAllPenjualan().stream()
                                   .mapToDouble(com.dailydose.model.Penjualan::getTotalKeuntungan)
                                   .sum();
        lblKeuntungan.setText("Total Keuntungan: Rp " + String.format("%,.0f", total));
    }

    // =========================================================
    //  NAVIGATION — Setiap method membuka halaman berbeda
    //  TODO: Implementasikan sesuai FXML yang kamu buat
    // =========================================================

    @FXML
    private void bukaInventory() {
        if (!SessionManager.getInstance().isAdmin()) {
            lblStokKritis.setText("❌ Akses ditolak! Staf tidak boleh mengubah data Master Barang.");
            return;
        }
        bukaHalaman("/com/dailydose/view/inventory.fxml", "Manajemen Barang");
    }

    @FXML
    private void bukaManajemenUser() {
        // Hanya Admin yang boleh akses
        if (!SessionManager.getInstance().isAdmin()) {
            lblStokKritis.setText("❌ Akses ditolak! Hanya Admin/Owner yang dapat mengelola User.");
            return;
        }
        bukaHalaman("/com/dailydose/view/user.fxml", "Manajemen User");
    }

    @FXML
    private void bukaKasir() {
        bukaHalaman("/com/dailydose/view/kasir.fxml", "Transaksi Kasir");
    }

    @FXML
    private void bukaPembelian() {
        if (!SessionManager.getInstance().isAdmin()) {
            lblStokKritis.setText("❌ Akses ditolak! Staf tidak boleh melakukan Restock/Pembelian.");
            return;
        }
        bukaHalaman("/com/dailydose/view/pembelian.fxml", "Transaksi Pembelian (Restock)");
    }

    @FXML
    private void bukaRiwayat() {
        bukaHalaman("/com/dailydose/view/riwayat.fxml", "Riwayat Penjualan");
    }

    @FXML
    private void bukaRiwayatPembelian() {
        if (!SessionManager.getInstance().isAdmin()) {
            lblStokKritis.setText("❌ Akses ditolak! Staf tidak diizinkan melihat data pembelian dari Supplier.");
            return;
        }
        bukaHalaman("/com/dailydose/view/riwayat_pembelian.fxml", "Riwayat Pembelian");
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        bukaHalaman("/com/dailydose/view/login.fxml", "DailyDose Inventory — Login");
    }

    @FXML
    private void handleKeluar() {
        System.exit(0);
    }

    // =========================================================
    //  HELPER — Fungsi generik untuk pindah halaman
    // =========================================================
    private void bukaHalaman(String fxmlPath, String judulWindow) {
        try {
            java.net.URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                lblStokKritis.setText("⚠️ Halaman \"" + judulWindow + "\" belum tersedia.");
                lblStokKritis.setStyle("-fx-text-fill: #e67e22;");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Ambil Stage dari komponen yang sudah ada
            Stage stage = (Stage) lblSambutan.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("DailyDose Inventory — " + judulWindow);

        } catch (IOException e) {
            lblStokKritis.setText("❌ Gagal buka halaman: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
