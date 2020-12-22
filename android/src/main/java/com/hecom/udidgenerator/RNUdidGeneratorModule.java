
package com.hecom.udidgenerator;

import android.Manifest;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableNativeMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RNUdidGeneratorModule extends ReactContextBaseJavaModule {
    private static final String FILENAME = ".qwdsavas";
    private final ReactApplicationContext reactContext;
    private String udid;

    public RNUdidGeneratorModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNUdidGenerator";
    }

    @ReactMethod
    public void getUdid(final String parentDir, final Promise promise) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... strings) {
                if (udid == null) {
                    udid = getUidByPath(reactContext, parentDir);
                }
                return udid;
            }

            @Override
            protected void onPostExecute(String udid) {
                promise.resolve(udid);
            }
        }.execute(parentDir);
    }

    private String getPath(Context context, String parentDir) throws IOException {

        boolean isExternalUsable = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable();
        boolean isExternalPermissionGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED;
        if (isExternalUsable && isExternalPermissionGranted) {
            File path = new File(Environment.getExternalStorageDirectory() + File.separator + parentDir);
            if (!path.exists()) {
                path.mkdirs();
            }
            File file = new File(path, FILENAME);
            if (!file.exists()) {
                file.createNewFile();
            }
            return file.getAbsolutePath();
        }
        return null;
    }


    private String getUidByPath(Context context, String parentDir) {
        String uid = null;
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            String path = getPath(context, parentDir);
            File uidFile = new File(path);
            fileReader = new FileReader(uidFile);
            bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            if (TextUtils.isEmpty(line)) {
                String generatorUid = UUID.randomUUID().toString();
                fileWriter = new FileWriter(uidFile);
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(generatorUid);
                bufferedWriter.flush();
                uid = generatorUid;
            } else {
                uid = line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }

                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }

                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return uid;
    }
}