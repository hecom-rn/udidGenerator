
package com.reactlibrary;

import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class RNUdidGeneratorModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public RNUdidGeneratorModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNUdidGenerator";
    }

    @ReactMethod
    public void testToast() {
        Toast.makeText(reactContext, "udidGenerator", Toast.LENGTH_SHORT).show();
    }
}