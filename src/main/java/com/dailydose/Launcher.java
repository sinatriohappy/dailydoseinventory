package com.dailydose;

/**
 * LAUNCHER — Workaround agar bisa di-run langsung dari IntelliJ.
 * JavaFX tidak bisa di-run langsung kalau main class extends Application.
 * Solusi: buat class terpisah yang memanggil MainApp.main().
 *
 * Set class INI sebagai "Main class" di Run Configuration IntelliJ!
 */
public class Launcher {
    public static void main(String[] args) {
        MainApp.main(args);
    }
}
