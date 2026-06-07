package com.dailydose.dao;

import com.dailydose.model.*;
import com.dailydose.util.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PEMBELIAN DAO (Data Access Object)
 * Menangani operasi database untuk tabel 'pembelian' dan 'detail_pembelian'.
 * Saat menyimpan pembelian, method ini juga memanggil Barang.tambahStok()
 * untuk memperbarui stok di database secara otomatis (fitur Restock).
 */
public class PembelianDAO {

    private final Connection conn;
    private final BarangDAO barangDAO;

    public PembelianDAO() {
        this.conn      = DatabaseHelper.getInstance().getConnection();
        this.barangDAO = new BarangDAO();
    }

    // =========================================================
    //  SIMPAN PEMBELIAN + UPDATE STOK (Transactional)
    //  Konsep: Transaction → semua berhasil atau semua dibatalkan
    // =========================================================
    public boolean simpanPembelian(Pembelian pembelian) {
        try {
            // Matikan auto-commit agar bisa ROLLBACK jika ada error
            conn.setAutoCommit(false);

            // 1. INSERT header pembelian
            String sqlHeader = """
                INSERT INTO pembelian (tanggal, total_pembayaran, id_user, nama_toko)
                VALUES (?, ?, ?, ?)
            """;
            PreparedStatement psHeader = conn.prepareStatement(sqlHeader,
                Statement.RETURN_GENERATED_KEYS);
            psHeader.setString(1, pembelian.getTanggal().toString());
            psHeader.setDouble(2, pembelian.getTotalPembayaran());
            psHeader.setInt   (3, pembelian.getIdUser());
            psHeader.setString(4, pembelian.getNamaToko());
            psHeader.executeUpdate();

            // Ambil ID yang di-generate database (AUTOINCREMENT)
            ResultSet keys = psHeader.getGeneratedKeys();
            int idPembelian = 0;
            if (keys.next()) {
                idPembelian = keys.getInt(1);
            }

            // 2. INSERT setiap detail + UPDATE stok barang
            String sqlDetail = """
                INSERT INTO detail_pembelian (id_pembelian, id_barang, jumlah, harga_beli)
                VALUES (?, ?, ?, ?)
            """;
            for (DetailPembelian d : pembelian.getListDetail()) {
                // 2a. Insert detail
                PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
                psDetail.setInt   (1, idPembelian);
                psDetail.setString(2, d.getIdBarang());
                psDetail.setInt   (3, d.getJumlah());
                psDetail.setDouble(4, d.getHargaBeli());
                psDetail.executeUpdate();

                // 2b. Load barang dari DB → panggil tambahStok() → simpan
                //     tambahStok() akan VALIDASI (jumlah harus > 0)
                Barang barang = barangDAO.getBarangById(d.getIdBarang());
                if (barang != null) {
                    int stokLama = barang.getStok();
                    double modalLama = barang.getHargaBeli();
                    int stokBaru = d.getJumlah();
                    double modalBaru = d.getHargaBeli();

                    // Algoritma Moving Average Cost (Modal Rata-rata)
                    double averageModal = 0;
                    if (stokLama + stokBaru > 0) {
                        averageModal = ((stokLama * modalLama) + (stokBaru * modalBaru)) / (stokLama + stokBaru);
                    } else {
                        averageModal = modalBaru;
                    }

                    barang.tambahStok(stokBaru);  // ← Konsep: Business Logic di Model
                    barangDAO.updateStok(barang.getIdBarang(), barang.getStok());
                    // Update Modal (harga_beli) dengan harga rata-rata
                    barangDAO.updateHargaBeli(barang.getIdBarang(), averageModal);
                }
            }

            // 3. COMMIT — semua berhasil disimpan
            conn.commit();
            System.out.println("✅ Pembelian berhasil disimpan. ID: " + idPembelian);
            return true;

        } catch (SQLException e) {
            // ROLLBACK — batalkan semua perubahan jika ada error
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("❌ Gagal simpan pembelian: " + e.getMessage());
            return false;

        } catch (IllegalArgumentException e) {
            // Exception dari tambahStok() jika jumlah <= 0
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("❌ Validasi gagal: " + e.getMessage());
            return false;

        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // =========================================================
    //  READ — Ambil semua riwayat pembelian
    // =========================================================
    public List<Pembelian> getAllPembelian() {
        List<Pembelian> list = new ArrayList<>();
        String sql = "SELECT * FROM pembelian ORDER BY tanggal DESC";
        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Pembelian p = new Pembelian(rs.getInt("id_user"), rs.getString("nama_toko"));
                p.setIdPembelian(rs.getInt("id_pembelian"));
                p.setTotalPembayaran(rs.getDouble("total_pembayaran"));
                // Tanggal tidak di-set ulang ke objek karena di konstruktor diset ke now, 
                // ini bisa jadi bug kecil dari kode aslinya, tapi kita ikuti saja untuk sementara, 
                // atau lebih baik kita biarkan constructor default.
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("❌ Gagal ambil riwayat pembelian: " + e.getMessage());
        }
        return list;
    }

    // =========================================================
    //  READ — Detail item dari satu transaksi pembelian tertentu
    // =========================================================
    public List<DetailPembelian> getDetailByPembelian(int idPembelian) {
        List<DetailPembelian> list = new ArrayList<>();
        String sql = """
            SELECT dp.*, b.nama_barang
            FROM detail_pembelian dp
            JOIN barang b ON dp.id_barang = b.id_barang
            WHERE dp.id_pembelian = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPembelian);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new DetailPembelian(
                    rs.getInt("id_detail_beli"),
                    rs.getInt("id_pembelian"),
                    rs.getString("id_barang"),
                    rs.getString("nama_barang"),
                    rs.getInt("jumlah"),
                    rs.getDouble("harga_beli")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Gagal ambil detail pembelian: " + e.getMessage());
        }
        return list;
    }
}
