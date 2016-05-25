package com.yhtye.shanghaishishigongjiaochaxun;

import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.yhtye.shanghaishishigongjiaochaxun.R;
import com.yhtye.shgongjiao.tools.NetUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 启动页面
 *
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 网络检查
        if (!NetUtil.checkNet(this)) {
            setContentView(R.layout.activity_default_splash);
            
            Toast.makeText(this, R.string.network_tip, Toast.LENGTH_LONG).show();         
            
            Handler x = new Handler();
            x.postDelayed(new splashhandler(), 800);
        } else {
            setContentView(R.layout.activity_ads_splash);
            // 加载广告
            loadAds();
        }
        
        // 日志传输过程采用加密模式
        AnalyticsConfig.enableEncrypt(true);
    }
    
    /**
     * 加载广告
     */
    private void loadAds() {
        RelativeLayout adsParent = (RelativeLayout) this.findViewById(R.id.adsRl);
        // the observer of AD
        SplashAdListener listener = new SplashAdListener() {
            @Override
            public void onAdDismissed() {
                jumpWhenCanClick(); //  跳转至您的应用主界面
            }

            @Override
            public void onAdFailed(String arg0) {
                jump();
            }

            @Override
            public void onAdPresent() {
            }

            @Override
            public void onAdClick() {
            }
        };
        String adPlaceId = "2529770"; 
        new SplashAd(this, adsParent, listener, adPlaceId, true);
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
    
    /**
     * 当设置开屏可点击时，需要等待跳转页面关闭后，再切换至您的主窗口。故此时需要增加canJumpImmediately判断。 另外，点击开屏还需要在onResume中调用jumpWhenCanClick接口。
     */
    public boolean canJumpImmediately = false;

    private void jumpWhenCanClick() {
        if (canJumpImmediately) {
            this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
            this.finish();
        } else {
            canJumpImmediately = true;
        }
    }
    
    /**
     * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
     */
    private void jump() {
        this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
        this.finish();
    }
    
    /**
     * 跳过广告
     * 
     * @param v
     */
    public void skipAdsClick(View v) {
        jump();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        
        if (canJumpImmediately) {
            jumpWhenCanClick();
        }
        canJumpImmediately = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        
        canJumpImmediately = false;
    }
}
