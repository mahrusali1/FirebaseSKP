package com.mahrus.firebaseskp;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.util.AsynchUtil;
import android.app.Activity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@DesignerComponent(version = 1,
    description = "Flexible Firebase Extension for SKP Click - Created by Mahrus Ali",
    category = ComponentCategory.EXTENSION,
    nonVisible = true,
    iconName = "images/extension.png")
@SimpleObject(external = true)
public class FirebaseSKP extends AndroidNonvisibleComponent {

    private String firebaseURL = "https://skp-pkh-default-rtdb.firebaseio.com/";
    private final Activity activity;

    public FirebaseSKP(ComponentContainer container) {
        super(container.$form());
        this.activity = container.$context();
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING, 
                     defaultValue = "https://skp-pkh-default-rtdb.firebaseio.com/")
    @SimpleProperty(description = "Alamat URL Firebase Realtime Database")
    public void FirebaseURL(String url) {
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        this.firebaseURL = url;
    }

    @SimpleProperty
    public String FirebaseURL() {
        return this.firebaseURL;
    }

    @SimpleFunction(description = "Simpan nilai ke tag tertentu")
    public void StoreValue(final String tag, final String valueToStore) {
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(firebaseURL + tag + ".json");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("PUT");
                    conn.setDoOutput(true);
                    
                    String jsonValue = "\"" + valueToStore + "\"";
                    byte[] input = jsonValue.getBytes("utf-8");
                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(input, 0, input.length);
                    }

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        DataChanged(tag, valueToStore);
                    } else {
                        FirebaseError("Error " + responseCode + " saat menyimpan data");
                    }
                } catch (Exception e) {
                    FirebaseError(e.getMessage());
                }
            }
        });
    }

    @SimpleFunction(description = "Ambil nilai dari tag tertentu")
    public void GetValue(final String tag) {
        AsynchUtil.runAsynchronously(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(firebaseURL + tag + ".json");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                        in.close();
                        
                        String cleanValue = response.toString().replace("\"", "");
                        GotValue(tag, cleanValue);
                    } else {
                        FirebaseError("Error " + responseCode + " saat mengambil data");
                    }
                } catch (Exception e) {
                    FirebaseError(e.getMessage());
                }
            }
        });
    }

    @SimpleEvent(description = "Dipicu setelah GetValue berhasil")
    public void GotValue(final String tag, final String value) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EventDispatcher.dispatchEvent(FirebaseSKP.this, "GotValue", tag, value);
            }
        });
    }

    @SimpleEvent(description = "Dipicu setelah StoreValue berhasil")
    public void DataChanged(final String tag, final String value) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EventDispatcher.dispatchEvent(FirebaseSKP.this, "DataChanged", tag, value);
            }
        });
    }

    @SimpleEvent(description = "Dipicu jika terjadi error")
    public void FirebaseError(final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EventDispatcher.dispatchEvent(FirebaseSKP.this, "FirebaseError", message);
            }
        });
    }
}
