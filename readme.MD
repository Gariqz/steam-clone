# 🎮 Steam Clone Project - Setup Guide

Halo tim! Biar pengerjaan tugas besar kita aman sentosa, ga pada pusing *setting* environment, dan kodenya ga bentrok pas digabung, tolong ikutin panduan *setup* ini dulu ya sebelum mulai ngoding.

## 1. Persiapan Install (Wajib)

Kita butuh **Java JDK 17** sama **Maven** sebagai pondasi utamanya.

🍎 **Buat yang pake Mac:**

Buka Terminal terus jalanin command ini:

```bash
brew install openjdk@17
brew install maven
```

🪟 Buat yang pake Windows:

Buka Command Prompt (wajib Run as Administrator), terus jalanin:
```bash
winget install Microsoft.OpenJDK.17
winget install Apache.Maven
```

(Kalo winget error, download manual aja installer JDK 17 sama Maven dari web resminya, terus jangan lupa masukin ke System Path / Environment Variables).


✅ Cara Cek Kalo Udah Berhasil:
Buka terminal baru, ketik ini buat mastiin ga ada error:

```bash
java -version
mvn -v
```

## 2. Setup VS Code

Biar VS Code kita fiturnya lengkap buat ngerjain Java, install extension ini dulu:

```bash
    Extension Pack for Java (dari Microsoft)

    JavaFX Support (dari redhat / vscjava)

    Scene Builder (Ini aplikasi misah ya, wajib download di web Gluonhq.com buat drag-and-drop UI-nya).
```

Kalo udah pada di-install, tekan Ctrl + Shift + P (di Mac Cmd + Shift + P), ketik Java: Clean Workspace, terus pilih Restart and Delete. Ini biar VS Code ngebaca struktur folder kita dengan bener dari awal.

## 3. Cara Nge-Run Project

PENTING: Jangan pencet tombol "Run" atau icon Play biasa yang ada di VS Code! Project kita diikat pake Maven, jadi run-nya wajib lewat terminal.

Buka terminal bawaan VS Code (tekan Ctrl + `), terus ketik perintah sakti ini:

```bash
mvn clean javafx:run
```

## 4. Rules Pengerjaan Tim

    Testing UI: Kalo lagi bikin page baru dan mau ngetes tampilannya, ganti rutenya di file Main.java ya.

    Edit Desain: Semua file desain (.fxml) usahain diedit lewat Scene Builder aja, atau pake struktur VBox/HBox biar otomatis rata tengah (jangan pake kordinat absolut).

    Kalo Error: Misal pas nge-run nemu error Exit Value 1, tambahin flag -e di belakang command (jadi mvn clean javafx:run -e) buat liat log error aslinya.