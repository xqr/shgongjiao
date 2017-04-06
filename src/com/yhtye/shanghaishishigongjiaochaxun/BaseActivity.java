package com.yhtye.shanghaishishigongjiaochaxun;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;

/**
 * 基础Activity
 */
public class BaseActivity extends Activity {
    
    @Override
    public void onResume() {
        super.onResume();
        // umeng统计
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // umeng统计
        MobclickAgent.onPause(this);
    }
}
