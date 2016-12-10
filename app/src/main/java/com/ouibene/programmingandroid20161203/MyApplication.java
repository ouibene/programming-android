package com.ouibene.programmingandroid20161203;

import android.app.Application;

import com.facebook.FacebookSdk;

/**
 * Created by Youngeun-Lee on 2016. 12. 3..
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
