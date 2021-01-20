
package com.hecom.udidgenerator;

import android.Manifest;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class RNUdidGeneratorModule extends ReactContextBaseJavaModule {
    private static final String FILENAME = ".qwdsavas";
    private final ReactApplicationContext reactContext;
    private String udid;
    private CountDownLatch oaidLatch = new CountDownLatch(1);
    private volatile boolean manualDisabled;
    private volatile boolean isOaidSupported;

    public RNUdidGeneratorModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        //oaid默认支持到21
        if (Build.VERSION.SDK_INT >= 21) {
            MdidSdkHelper.InitSdk(reactContext, true, new IIdentifierListener() {

                @Override
                public void OnSupport(boolean b, IdSupplier idSupplier) {
                    Log.d("RNUdidGeneratorModule", "OnSupport " + Thread.currentThread().getName());
                    try {
                        if (idSupplier == null) {
                            return;
                        }
                        if (idSupplier.isSupported()) {
                            isOaidSupported = true;
                            String oaid = idSupplier.getOAID();
                            Log.d("RNUdidGeneratorModule", "OnSupport oaid = " + oaid);
                            boolean isValid = isOAIDValid(oaid);
                            if (isValid) {
                                udid = idSupplier.getOAID();
                            } else {
                                manualDisabled = isAllZero(oaid);
                            }
                        }
                    } finally {
                        oaidLatch.countDown();
                    }
                }
            });
        } else {
            oaidLatch.countDown();
        }
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
                try {
                    oaidLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isOaidSupported && manualDisabled) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getReactApplicationContext(), "请允许隐私设置中的广告跟踪", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return null;
                }


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

    public static boolean isOAIDValid(String oaid) {
        if (TextUtils.isEmpty(oaid)) {
            return false;
        }
        if (isAllZero(oaid)) {
            return false;
        }
        return true;
    }

    /**
     * if user disabled the oaid,then something like 00000000-0000-0000-0000-000000000000 will be returned
     *
     * @param oaid
     * @return
     */
    public static boolean isAllZero(String oaid) {
        if (TextUtils.isEmpty(oaid)) {
            return false;
        }
        String id = oaid.replaceAll("-", "");
        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            if (c != '0') {
                return false;
            }
        }
        return true;
    }
}