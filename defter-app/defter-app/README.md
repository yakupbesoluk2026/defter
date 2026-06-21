# Defter — Kişisel Ekonomi Takvimi (Android APK)

Bu klasör, `www/index.html` içindeki web uygulamasını **Capacitor** ile
sarmalayarak gerçek bir Android APK'sına dönüştürmek için hazırlanmıştır.

## Klasör yapısı

```
defter-app/
├── www/index.html          ← Uygulamanın kendisi (HTML/CSS/JS)
├── package.json            ← Capacitor bağımlılıkları
├── capacitor.config.json   ← Capacitor ayarları (appId, appName, webDir)
└── .github/workflows/
    └── build-apk.yml       ← APK'yı otomatik derleyen GitHub Actions iş akışı
```

## Yöntem 1 — GitHub Actions ile otomatik derleme (önerilen)

Codespace içinde Android SDK kurmaya gerek yok; derleme GitHub'ın
sunucularında otomatik yapılır.

1. Bu klasörü bir GitHub reposuna gönderin (push):
   ```bash
   git init
   git add .
   git commit -m "Defter ilk sürüm"
   git branch -M main
   git remote add origin <REPO_URL>
   git push -u origin main
   ```
2. GitHub'da reponuzun **Actions** sekmesine gidin.
3. "Build APK" iş akışının çalıştığını göreceksiniz (push sonrası otomatik
   tetiklenir, ya da "Run workflow" ile elle de başlatabilirsiniz).
4. İş akışı bittiğinde, çalışmanın sayfasında **Artifacts** bölümünden
   `defter-debug-apk` dosyasını indirin — içinde `app-debug.apk` olacak.
5. APK'yı telefonunuza aktarıp kurabilirsiniz (Android'de "bilinmeyen
   kaynaklardan yükleme" iznine ihtiyaç olabilir).

## Yöntem 2 — Codespace içinde manuel derleme

GitHub Codespaces içinde doğrudan derlemek isterseniz Android SDK'yı
kurmanız gerekir (ilk seferde birkaç dakika sürer):

```bash
# 1) Bağımlılıkları kur
npm install

# 2) Android platformunu projeye ekle (bir kere yapılır)
npx cap add android

# 3) Java 17 kurulu olduğundan emin ol
sdk install java 17.0.10-tem   # sdkman varsa
# veya: sudo apt-get install -y openjdk-17-jdk

# 4) Android komut satırı araçlarını kur (cmdline-tools + platform-tools)
#    En kolay yol: android-actions kullanmadığınız için sdkmanager'ı
#    manuel kurmanız gerekir. Bu adım Codespace'te zaman alabilir;
#    bu yüzden Yöntem 1 (GitHub Actions) önerilir.

# 5) Web dosyalarını Android projesine kopyala
npx cap sync android

# 6) APK'yı derle
cd android
chmod +x gradlew
./gradlew assembleDebug

# APK şurada oluşur:
# android/app/build/outputs/apk/debug/app-debug.apk
```

> Not: Codespace'in standart imajında Android SDK önceden kurulu
> değildir. Manuel kurulum uzun sürebileceğinden **Yöntem 1**
> (GitHub Actions) APK almanın en hızlı ve güvenilir yoludur.

## Reklam (AdMob) entegrasyonu

`www/index.html` içinde `#ad-banner` adında bir yer tutucu alan
bulunur. Gerçek banner reklam göstermek için:

1. Bir [AdMob](https://admob.google.com) hesabı açıp uygulama ve
   reklam birimi (banner) kimliklerini alın.
2. `android/app/src/main/AndroidManifest.xml` içine uygulama
   kimliğinizi ekleyin:
   ```xml
   <meta-data
       android:name="com.google.android.gms.ads.APPLICATION_ID"
       android:value="ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY"/>
   ```
3. `www/index.html` dosyasının sonundaki yorum bloğunda gösterilen
   `@capacitor-community/admob` kod örneğini kullanarak banner'ı
   başlatın (paket `package.json`'da zaten tanımlı).

Gerçek AdMob kimlikleri girilmeden reklamlar görüntülenmez; bu
nedenle uygulama şu an sadece tasarıma uygun bir yer tutucu içerir.

## Ana ekran widget'ı

`native-widget/` klasöründe, telefonun ana ekranına eklenebilen
native bir Android widget'ı (bugünün ve bu ayın özetini gösteren)
için gerekli tüm Kotlin/XML dosyaları ve adım adım kurulum
talimatları bulunuyor. Detaylar için `native-widget/README-WIDGET.md`
dosyasına bakın.

## Uygulamayı değiştirmek

Tüm uygulama mantığı tek bir dosyada: `www/index.html`. Değişiklik
yaptıktan sonra `npx cap sync android` komutunu çalıştırıp tekrar
derlemeniz yeterli.
