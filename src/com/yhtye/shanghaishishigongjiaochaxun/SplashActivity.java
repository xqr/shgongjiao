package com.yhtye.shanghaishishigongjiaochaxun;

import com.umeng.analytics.MobclickAgent;
import com.yhtye.shanghaishishigongjiaochaxun.R;
import com.yhtye.shgongjiao.tools.NetUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

/**
 * 启动页面
 *
 */
public class SplashActivity extends BaseActivity {
    
    private final static Handler mHandler = new Handler();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_splash);
        
        // 网络检查
        if (!NetUtil.checkNet(this)) {
            Toast.makeText(this, R.string.network_tip, Toast.LENGTH_LONG).show();         
        }
        
        mHandler.postDelayed(new splashhandler(), 800);
        
        // 日志传输过程采用加密模式
        MobclickAgent.enableEncrypt(true);
    }
    
    private class splashhandler implements Runnable {
        public void run() {
            // 销毁当前Activity，切换到主页面
            Intent intent=new Intent();  
            intent.setClass(SplashActivity.this, MainActivity.class);  
            startActivity(intent);
            SplashActivity.this.finish();
        }
    }
}
