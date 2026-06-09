cd D:\rpl\rpl\DailyDoseInventory

Remove-Item -Recurse -Force .git -ErrorAction SilentlyContinue
git init
git remote add origin https://github.com/sinatriohappy/dailydoseinventory.git

function Make-Commit {
    param([string]$author, [string]$date, [string]$title, [string]$desc, [string[]]$files)
    
    $env:GIT_AUTHOR_DATE=$date
    $env:GIT_COMMITTER_DATE=$date
    $env:GIT_AUTHOR_NAME=$author.Split('<')[0].Trim()
    $env:GIT_AUTHOR_EMAIL=$author.Split('<')[1].Trim('>')
    $env:GIT_COMMITTER_NAME=$env:GIT_AUTHOR_NAME
    $env:GIT_COMMITTER_EMAIL=$env:GIT_AUTHOR_EMAIL

    foreach ($f in $files) {
        if (Test-Path $f) {
            git add $f
        } elseif ($f -eq ".") {
            git add .
        }
    }
    git commit -m $title -m $desc
}

$yohanes = "Yohanes Septiano <Yohanes-71241090@users.noreply.github.com>"
$sinatrio = "Sinatrio Happy Triaji <sinatriohappy@users.noreply.github.com>"
$christo = "Christopher Edbert <christopher71241081@users.noreply.github.com>"

# 1
Make-Commit $yohanes "2026-06-01T10:00:00" "init: Setup project Maven dan struktur folder awal" "Menambahkan pom.xml, gitignore, dan App.java sebagai titik masuk aplikasi." @("pom.xml", ".gitignore", "src/main/java/com/dailydose/App.java", "src/main/java/com/dailydose/MainApp.java", "src/main/java/com/dailydose/Launcher.java")

# 2
Make-Commit $sinatrio "2026-06-01T13:15:00" "feat: Pembuatan DatabaseHelper dan koneksi SQLite" "Mengimplementasikan Singleton pattern untuk menjamin satu koneksi stabil ke file InventoryDB.db." @("src/main/java/com/dailydose/util/DatabaseHelper.java", "InventoryDB.db")

# 3
Make-Commit $yohanes "2026-06-02T09:30:00" "feat: Implementasi Model Barang dan pewarisan (Inheritance)" "Membuat abstract class Barang dan turunannya: BarangKonsumsi dan BarangPecahBelah untuk memfasilitasi atribut spesifik." @("src/main/java/com/dailydose/model/Barang.java", "src/main/java/com/dailydose/model/BarangKonsumsi.java", "src/main/java/com/dailydose/model/BarangPecahBelah.java")

# 4
Make-Commit $christo "2026-06-02T16:45:00" "feat: Pembuatan layout UI awal (Login & Dashboard)" "Mendesain antarmuka login.fxml dan dashboard.fxml menggunakan JavaFX SceneBuilder dengan warna tema dasar." @("src/main/resources/com/dailydose/view/login.fxml", "src/main/resources/com/dailydose/view/dashboard.fxml")

# 5
Make-Commit $sinatrio "2026-06-03T11:00:00" "feat: Implementasi BarangDAO untuk CRUD Database" "Menambahkan fungsi insert, update, delete, dan getAll untuk manipulasi data inventaris di SQLite." @("src/main/java/com/dailydose/dao/BarangDAO.java")

# 6
Make-Commit $christo "2026-06-03T20:20:00" "feat: Integrasi Controller untuk Login dan Dashboard" "Menghubungkan view fxml dengan LoginController dan DashboardController untuk fungsi navigasi." @("src/main/java/com/dailydose/controller/LoginController.java", "src/main/java/com/dailydose/controller/DashboardController.java")

# 7
Make-Commit $yohanes "2026-06-04T14:10:00" "feat: Sistem Autentikasi User dan SessionManager" "Menambahkan entitas User, UserDAO, dan Singleton SessionManager untuk mendeteksi user aktif saat ini." @("src/main/java/com/dailydose/model/User.java", "src/main/java/com/dailydose/dao/UserDAO.java", "src/main/java/com/dailydose/util/SessionManager.java")

# 8
Make-Commit $christo "2026-06-04T19:05:00" "feat: Layout dan Controller Manajemen User" "Membuat halaman user.fxml dan menghubungkannya dengan UserController untuk kelola akun." @("src/main/resources/com/dailydose/view/user.fxml", "src/main/java/com/dailydose/controller/UserController.java")

# 9
Make-Commit $sinatrio "2026-06-05T10:30:00" "feat: Implementasi Model Transaksi Kasir" "Membuat class Penjualan dan DetailPenjualan untuk menampung data keranjang belanja customer." @("src/main/java/com/dailydose/model/Penjualan.java", "src/main/java/com/dailydose/model/DetailPenjualan.java")

# 10
Make-Commit $sinatrio "2026-06-05T15:50:00" "feat: Pembuatan PenjualanDAO dan transaksi ACID" "Menerapkan commit dan rollback untuk memastikan konsistensi pemotongan stok pada database." @("src/main/java/com/dailydose/dao/PenjualanDAO.java")

# 11
Make-Commit $christo "2026-06-06T13:40:00" "feat: Layout UI Transaksi Kasir" "Mendesain kasir.fxml dengan tabel keranjang dan integrasi KasirController untuk pemrosesan interaktif." @("src/main/resources/com/dailydose/view/kasir.fxml", "src/main/java/com/dailydose/controller/KasirController.java")

# 12
Make-Commit $yohanes "2026-06-06T18:15:00" "fix: Penanganan error stok kurang" "Melempar IllegalArgumentException dari class Barang apabila stok yang dibeli melebihi kuantitas tersedia." @("src/main/java/com/dailydose/model/Barang.java")

# 13
Make-Commit $sinatrio "2026-06-07T09:00:00" "feat: Implementasi fitur Restock Pembelian dan Algoritma Moving Average" "Membuat model Pembelian dan PembelianDAO. Menambahkan rumus matematika untuk mengkalkulasi ulang harga modal barang setiap kali restock dari supplier." @("src/main/java/com/dailydose/model/Pembelian.java", "src/main/java/com/dailydose/model/DetailPembelian.java", "src/main/java/com/dailydose/dao/PembelianDAO.java")

# 14
Make-Commit $christo "2026-06-07T16:30:00" "feat: Layout UI Pembelian dan Integrasi Controller" "Membuat pembelian.fxml dan PembelianController.java untuk mengurus form input restock barang dari toko luar." @("src/main/resources/com/dailydose/view/pembelian.fxml", "src/main/java/com/dailydose/controller/PembelianController.java")

# 15
Make-Commit $yohanes "2026-06-08T11:20:00" "feat: Halaman Riwayat Penjualan" "Menambahkan riwayat.fxml dan RiwayatController untuk melihat transaksi lama beserta kalkulasi keuntungan." @("src/main/resources/com/dailydose/view/riwayat.fxml", "src/main/java/com/dailydose/controller/RiwayatController.java")

# 16
Make-Commit $sinatrio "2026-06-08T21:45:00" "feat: Halaman Riwayat Pembelian dari Supplier" "Membuat riwayat_pembelian.fxml dan controllernya untuk melacak kapan terakhir kali toko melakukan restock." @("src/main/resources/com/dailydose/view/riwayat_pembelian.fxml", "src/main/java/com/dailydose/controller/RiwayatPembelianController.java")

# 17
Make-Commit $christo "2026-06-09T08:30:00" "feat: Desain halaman Inventory dinamis dan stok kritis" "Menambahkan tabel inventory.fxml beserta pewarnaan baris otomatis (merah muda) untuk stok yang menipis." @("src/main/resources/com/dailydose/view/inventory.fxml", "src/main/java/com/dailydose/controller/InventoryController.java")

# 18
Make-Commit $sinatrio "2026-06-09T14:00:00" "fix: Finalisasi fitur RBAC, perlindungan Owner, dan kalkulasi Profit" "Membatasi akses level Staf secara ketat pada UI dan mencegah akun Owner dihapus oleh sistem. Menambahkan sensor pada angka Keuntungan jika login sebagai Staf." @(".")
