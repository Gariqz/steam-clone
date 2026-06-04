🎮 Steam Clone - JavaFX Project Setup Guide

Selamat datang di repository Steam Clone! Biar kita berempat bisa coding bareng dengan lancar tanpa error di VS Code masing-masing, wajib ikuti langkah-langkah setup di bawah ini sebelum mulai ngoding.
1. Persiapan Senjata (Instalasi Wajib)

Kita menggunakan Java JDK 17 dan Apache Maven sebagai pondasi utama.

🍎 Untuk Pengguna macOS:
Buka Terminal dan jalankan perintah Homebrew ini:
Bash

brew install openjdk@17
brew install maven

🪟 Untuk Pengguna Windows:
Buka Command Prompt (Run as Administrator) dan jalankan perintah Winget ini:
DOS

winget install Microsoft.OpenJDK.17
winget install Apache.Maven

(Alternatif Windows: Kalian bisa download manual installer JDK 17 dan Maven dari situs resminya, lalu set Environment Variables / System Path).

Cek Keberhasilan Instalasi:
Buka terminal baru, pastikan dua perintah ini memunculkan versi aplikasi (tidak error):
Bash

java -version
mvn -v

2. Setup VS Code

Biar VS Code kita sekuat IDE berat, wajib install ekstensi berikut:

    Extension Pack for Java (oleh Microsoft)

    JavaFX Support (oleh redhat / vscjava)

    Scene Builder (Aplikasi terpisah dari Gluonhq.com, instal di OS kalian masing-masing untuk drag-and-drop UI FXML).

Setelah instal ekstensi, tekan Ctrl + Shift + P (atau Cmd + Shift + P), ketik Java: Clean Workspace, dan pilih Restart and Delete biar VS Code ngebaca struktur folder kita dengan benar.
3. Cara Menjalankan Project

Jangan gunakan tombol "Run" atau ikon Play biasa di VS Code! Proyek kita diikat menggunakan Maven.

Buka terminal terintegrasi di VS Code (`Ctrl + ``), lalu ketik perintah sakti ini:
Bash

mvn clean javafx:run