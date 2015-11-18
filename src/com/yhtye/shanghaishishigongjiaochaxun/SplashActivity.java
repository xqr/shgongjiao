package com.yhtye.shanghaishishigongjiaochaxun;

import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.yhtye.shgongjiao.tools.NetUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

/**
 * 启动页面
 *
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // 网络检查
        if (!NetUtil.checkNet(this)) {
            Toast.makeText(this, R.string.network_tip, Toast.LENGTH_LONG).show();
        }
        
        Handler x = new Handler();
        x.postDelayed(new splashhandler(), 800);
        // 开启日志debug模式
        MobclickAgent.setDebugMode( true );
        // 日志传输过程采用加密模式
        AnalyticsConfig.enableEncrypt(true);
    }
    
    class splashhandler implements Runnable {
        public void run() {
            // 销毁当前Activity，切换到主页面
            Intent intent=new Intent();  
            intent.setClass(SplashActivity.this, MainActivity.class);  
            startActivity(intent);
            SplashActivity.this.finish();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
