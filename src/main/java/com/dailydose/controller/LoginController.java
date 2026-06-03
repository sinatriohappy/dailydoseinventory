package com.dailydose.controller;

import com.dailydose.dao.UserDAO;
import com.dailydose.model.User;
import com.dailydose.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * LOGIN CONTROLLER
 * Konsep MVC:
 *   - VIEW   : login.fxml (antarmuka)
 *   - MODEL  : UserDAO (akses database)
 *   - CONTROLLER (ini): menghubungkan keduanya + validasi
 *
 * Anotasi @FXML menghubungkan variabel Java ke komponen di file FXML.
 */
public class LoginController {

    // =========================================================
    //  INJECT komponen dari login.fxml
    // =========================================================
    @FXML private TextField     txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button        btnLogin;
    @FXML private Label         lblPesan;   // untuk tampilkan pesan error

    private final UserDAO userDAO = new UserDAO();

    // =========================================================
    //  EVENT HANDLER — dipanggil saat tombol LOGIN diklik
    //  (dihubungkan lewat onAction="#handleLogin" di FXML)
    // =========================================================
    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        // --- EXCEPTION HANDLING / VALIDASI INPUT ---
        if (username.isEmpty() || password.isEmpty()) {
            tampilkanPesan("Username dan Password tidak boleh kosong!", true);
            return;
        }

        // Panggil DAO untuk cek kredensial di database
        User user = userDAO.login(username, password);

        if (user == null) {
            // Login gagal — tampilkan pesan error (fitur Exception Handling no.8)
            tampilkanPesan("Username atau Password salah!", true);
            txtPassword.clear();
        } else {
            // Login berhasil — simpan user ke SessionManager (siapa yang login)
            SessionManager.getInstance().setCurrentUser(user);
            tampilkanPesan("Login berhasil! Selamat datang, " + user.getUsername(), false);

            // Pindah ke halaman Dashboard
            pindahKeDashboard();
        }
    }

    // =========================================================
    //  NAVIGATE — Buka halaman Dashboard
    // =========================================================
    private void pindahKeDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/dailydose/view/dashboard.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("DailyDose Inventory — Dashboard");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            tampilkanPesan("Gagal memuat halaman dashboard: " + e.getMessage(), true);
        }
    }

    // =========================================================
    //  HELPER
    // =========================================================
    private void tampilkanPesan(String pesan, boolean isError) {
        lblPesan.setText(pesan);
        lblPesan.setStyle(isError
            ? "-fx-text-fill: red;"
            : "-fx-text-fill: green;");
    }
}
