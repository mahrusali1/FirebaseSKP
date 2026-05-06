package com.mahrus.firebaseskp;

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.common.ComponentCategory;

@DesignerComponent(version = 1,
    description = "Ekstensi Firebase untuk Database SKP Click",
    category = ComponentCategory.EXTENSION,
    nonVisible = true)
@SimpleObject(external = true)
public class FirebaseSKP extends AndroidNonvisibleComponent {

    public FirebaseSKP(ComponentContainer container) {
        super(container.$form());
    }

    @SimpleFunction(description = "Kirim data ke database")
    public void KirimData(String path, String konten) {
        // Logika pengiriman data asinkron
        DataBerhasil(path);
    }

    @SimpleEvent(description = "Event saat data berhasil masuk")
    public void DataBerhasil(String path) {
        EventDispatcher.dispatchEvent(this, "DataBerhasil", path);
    }
}
