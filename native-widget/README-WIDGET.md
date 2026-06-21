# Defter — Ana Ekran Widget'ı Kurulumu

Bu klasördeki dosyalar, telefonun ana ekranına eklenebilen native bir
Android widget'ı sağlar. Widget; **bugünün neti** ile **bu ayın
gelir/gider/net** özetini gösterir ve dokununca uygulamayı açar.

Web tarafındaki `www/index.html` zaten her kayıt eklenip
silindiğinde bu widget'a veri gönderecek şekilde güncellendi
(`pushWidgetData()` fonksiyonu). Burada yapman gereken, native
Android tarafını projeye eklemek.

## Ön koşul

Ana `defter-app` klasöründe önce Android platformunu bir kez
oluşturmuş olman gerekir:

```bash
npm install
npx cap add android
```

Bu komut `android/` klasörünü oluşturur. Aşağıdaki adımlar bu
klasörün İÇİNE dosya kopyalamak ve iki dosyayı düzenlemek
içindir.

## 1) Kotlin ve kaynak dosyalarını kopyala

`native-widget/app/src/main/` altındaki her şeyi, oluşan
`android/app/src/main/` klasörünün üzerine kopyala (aynı klasör
yapısını koru):

```bash
cp -r native-widget/app/src/main/java/com/defter/app/*.kt \
      android/app/src/main/java/com/defter/app/

cp native-widget/app/src/main/res/layout/widget_defter.xml \
   android/app/src/main/res/layout/

cp native-widget/app/src/main/res/xml/widget_defter_info.xml \
   android/app/src/main/res/xml/

cp native-widget/app/src/main/res/drawable/widget_background.xml \
   android/app/src/main/res/drawable/
```

> Not: `android/app/src/main/res/xml` ve `.../drawable` klasörleri
> yoksa önce `mkdir -p` ile oluştur.

## 2) AndroidManifest.xml içine widget alıcısını ekle

`native-widget/AndroidManifest.snippet.xml` içindeki `<receiver>`
bloğunu, `android/app/src/main/AndroidManifest.xml` dosyasındaki
`<application> ... </application>` etiketinin içine, mevcut
`<activity>` bloğundan hemen sonra yapıştır.

## 3) MainActivity.java içinde eklentiyi kaydet

`android/app/src/main/java/com/defter/app/MainActivity.java`
dosyasının içeriğini `native-widget/MainActivity.snippet.java`
dosyasındaki haliyle değiştir (eklentiyi `registerPlugin(...)` ile
elle kaydetmek gerekiyor, çünkü bu eklenti ayrı bir npm paketi değil,
doğrudan proje içinde tanımlı).

## 4) Senkronize et ve derle

```bash
npx cap sync android
cd android
chmod +x gradlew
./gradlew assembleDebug
```

APK: `android/app/build/outputs/apk/debug/app-debug.apk`

## 5) `android/` klasörünü repoya commit et

GitHub Actions iş akışı (`build-apk.yml`), `android/` klasörü zaten
varsa onu yeniden oluşturmaz — bu yüzden yukarıdaki native widget
dosyalarının CI derlemelerinde de yer alması için **`android/`
klasörünü reponuza commit etmeniz gerekir**:

```bash
git add android native-widget
git commit -m "Ana ekran widget'ı eklendi"
git push
```

## 6) Widget'ı telefona ekleme

APK kurulduktan sonra: ana ekranda boş bir alana uzun bas → **Widget'lar**
→ **Defter** uygulamasını bul → widget'ı sürükleyip ana ekrana bırak.

## Bilinen sınırlamalar

- Widget verisi, uygulama açılıp bir kayıt eklendiğinde/silindiğinde
  güncellenir. Android sistemi ayrıca en az 30 dakikada bir yeniden
  çizim tetikler, ama uygulama hiç açılmadan günün değişmesi gibi
  durumlarda veri otomatik tazelenmez — bu, arka plan servisi
  kullanmayan basit widget'ların standart davranışıdır.
- Widget'ın kendi içinde işlem ekleme/silme yapılamaz; sadece özet
  gösterir ve dokununca uygulamayı açar.
