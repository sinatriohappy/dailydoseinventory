package com.dailydose.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DATABASE HELPER
 * Konsep Design Pattern: Singleton — hanya ada SATU koneksi database
 * selama aplikasi berjalan. Ini menghemat resource.
 *
 * File database (InventoryDB.db) akan otomatis dibuat di folder project
 * saat pertama kali aplikasi dijalankan.
 */
public class DatabaseHelper {

    // Path file SQLite — simpan di folder yang sama dengan JAR
    private static final String DB_URL = "jdbc:sqlite:D:\\Prak RPL-BO\\Project Prak\\dailydoseinventory\\InventoryDB.db";

    // Instance tunggal (Singleton Pattern)
    private static DatabaseHelper instance;
    private Connection connection;

    // =========================================================
    //  SINGLETON — constructor private agar tidak bisa new DatabaseHelper()
    // =========================================================
    private DatabaseHelper() {
        try {
            // Buka / buat koneksi ke file SQLite
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("✅ Koneksi database berhasil.");

            // Aktifkan Foreign Key agar relasi antar tabel bekerja
            connection.createStatement().execute("PRAGMA foreign_keys = ON;");

            // Buat semua tabel jika belum ada
            initDatabase();

        } catch (SQLException e) {
            System.err.println("❌ Gagal koneksi database: " + e.getMessage());
        }
    }

    /** Ambil instance tunggal DatabaseHelper */
    public static DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    /** Ambil objek Connection untuk dipakai DAO */
    public Connection getConnection() {
        return connection;
    }

    // =========================================================
    //  INISIALISASI TABEL
    //  Semua CREATE TABLE IF NOT EXISTS → aman dijalankan berulang kali
    // =========================================================
    private void initDatabase() {
        try (Statement stmt = connection.createStatement()) {

            // --- Tabel 1: users ---
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id_user  INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT    NOT NULL UNIQUE,
                    password TEXT    NOT NULL,
                    role     TEXT    NOT NULL CHECK(role IN ('Admin','Staf'))
                );
            """);

            // --- Tabel 2: barang ---
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS barang (
                    id_barang    TEXT    PRIMARY KEY,
                    nama_barang  TEXT    NOT NULL,
                    kategori     TEXT    NOT NULL CHECK(kategori IN ('PecahBelah','Konsumsi')),
                    harga        REAL    NOT NULL,
                    stok         INTEGER NOT NULL DEFAULT 0,
                    -- Kolom khusus BarangPecahBelah
                    material          TEXT,
                    -- Kolom khusus BarangKonsumsi
                    tgl_kadaluarsa    TEXT
                );
            """);

            // --- Tabel 3: penjualan ---
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS penjualan (
                    id_transaksi TEXT    PRIMARY KEY,
                    tanggal      TEXT    NOT NULL,
                    total_bayar  REAL    NOT NULL,
                    id_user      INTEGER NOT NULL,
                    FOREIGN KEY (id_user) REFERENCES users(id_user)
                );
            """);

            // --- Tabel 4: detail_penjualan ---
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS detail_penjualan (
                    id_detail    INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_transaksi TEXT    NOT NULL,
                    id_barang    TEXT    NOT NULL,
                    jumlah       INTEGER NOT NULL,
                    subtotal     REAL    NOT NULL,
                    FOREIGN KEY (id_transaksi) REFERENCES penjualan(id_transaksi),
                    FOREIGN KEY (id_barang)    REFERENCES barang(id_barang)
                );
            """);

            // --- Tabel 5: pembelian ---
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS pembelian (
                    id_pembelian      INTEGER PRIMARY KEY AUTOINCREMENT,
                    tanggal           TEXT    NOT NULL,
                    total_pembayaran  REAL    NOT NULL,
                    id_user           INTEGER NOT NULL,
                    FOREIGN KEY (id_user) REFERENCES users(id_user)
                );
            """);

            // --- Tabel 6: detail_pembelian ---
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS detail_pembelian (
                    id_detail_beli INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_pembelian   INTEGER NOT NULL,
                    id_barang      TEXT    NOT NULL,
                    jumlah         INTEGER NOT NULL,
                    harga_beli     REAL    NOT NULL,
                    FOREIGN KEY (id_pembelian) REFERENCES pembelian(id_pembelian),
                    FOREIGN KEY (id_barang)    REFERENCES barang(id_barang)
                );
            """);

            // --- MIGRASI KOLOM BARU (Fitur Keuntungan, Modal & Nama Toko) ---
            // Menggunakan TRY-CATCH agar tidak error jika kolom sudah ada
            try { stmt.execute("ALTER TABLE barang ADD COLUMN harga_beli REAL DEFAULT 0"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE penjualan ADD COLUMN total_keuntungan REAL DEFAULT 0"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE detail_penjualan ADD COLUMN keuntungan REAL DEFAULT 0"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE pembelian ADD COLUMN nama_toko TEXT"); } catch (Exception ignored) {}

            // Seed user Admin default jika tabel masih kosong
            seedDefaultAdmin(stmt);

            System.out.println("✅ Semua tabel & migrasi siap.");

        } catch (SQLException e) {
            System.err.println("❌ Gagal inisialisasi tabel: " + e.getMessage());
        }
    }

    /**
     * Buat 1 akun Admin default agar bisa langsung login.
     * username: admin | password: admin123
     */
    private void seedDefaultAdmin(Statement stmt) throws SQLException {
        String check = "SELECT COUNT(*) FROM users";
        var rs = stmt.executeQuery(check);
        if (rs.getInt(1) == 0) {
            stmt.execute("""
                INSERT INTO users (username, password, role)
                VALUES ('admin', 'admin123', 'Admin');
            """);
            System.out.println("ℹ️ Akun admin default dibuat (admin / admin123)");
        }
    }
}
