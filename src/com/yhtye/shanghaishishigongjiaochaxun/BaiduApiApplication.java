package com.yhtye.shanghaishishigongjiaochaxun;

import com.baidu.apistore.sdk.ApiStoreSDK;

import android.app.Application;

public class BaiduApiApplication extends Application {
    public static final String baiduApikey = "e656c1cb433764b3fdc1df7310c64254"; 
    
    @Override
    public void onCreate() {
        // 您的其他初始化流程
        ApiStoreSDK.init(this, baiduApikey);
        super.onCreate();
    }
}
