/*
  Bu kod, android/app/src/main/java/com/defter/app/MainActivity.java
  dosyasındaki MainActivity sınıfının İÇİNE eklenmelidir.

  DefterWidgetPlugin, ayrı bir npm paketi olarak değil, doğrudan
  Android proje modülünün içinde tanımlı olduğu için Capacitor'ün
  otomatik eklenti keşfi onu bulamaz; bu yüzden elle kaydedilmesi
  gerekir.

  MainActivity.java'nın TAMAMI şu şekilde görünmelidir:
*/

package com.defter.app;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(DefterWidgetPlugin.class);
        super.onCreate(savedInstanceState);
    }
}
