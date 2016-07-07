package com.yhtye.shanghaishishigongjiaochaxun;

import java.lang.ref.WeakReference;
import com.umeng.analytics.MobclickAgent;
import com.yhtye.shgongjiao.entity.CardInfo;
import com.yhtye.shgongjiao.service.YueService;
import com.yhtye.shgongjiao.tools.ThreadPoolManagerFactory;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class YueActivity extends Activity {
    
    private String cardNumber = null;
    private String cardYue = null;
    
    private TextView cardNumberTv = null;
    private TextView cardYueTv = null;
    
    private Handler handler = null;
    private YueService yueService = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yue);
        
        // 获取卡号参数
        Intent intent = getIntent();
        cardNumber = intent.getStringExtra("carNumber");
        
        // 初始化
        init();
        
        // 启动新线程获取线路信息
        ThreadPoolManagerFactory.getInstance().execute(new SearchYueRunable(cardNumber));
    }
    
    private void init () {
        cardNumberTv = (TextView) findViewById(R.id.cardnumber);
        cardYueTv = (TextView) findViewById(R.id.cardyue);
        
        handler = new ResultHandler(this);
        yueService = new YueService(YueActivity.this);
    }
    
    private class SearchYueRunable implements Runnable {
        
        private String number;
        
        public SearchYueRunable(String number) {
            this.number = number;
        }
        
        @Override
        public void run() {
            if (number == null) {
                return;
            }
            CardInfo cardInfo = yueService.searchYue(number);
            if (!number.equals(cardNumber)) {
                return;
            }
            if (cardInfo != null) {
                cardYue = cardInfo.getYue();
                // 保存卡号
                yueService.appendHistory(cardInfo);
            }
            // 发送消息
            Message msg=new Message();  
            handler.sendMessage(msg);
        }
    }
    
    private static class ResultHandler extends Handler {
        private WeakReference<YueActivity> mActivity;
        
        public ResultHandler(YueActivity activity) {
            this.mActivity = new WeakReference<YueActivity>(activity); 
        }
        
        @Override  
        public void handleMessage(Message msg) {  
            final YueActivity  theActivity =  mActivity.get();
            if (theActivity == null) {
                return;
            }
            theActivity.showYue();
        }
    }
    
    /**
     * 展示余额
     */
    private void showYue() {
        if (cardYue == null) {
            // 卡号错误
            Toast.makeText(this, R.string.card_error, Toast.LENGTH_LONG).show();
            finish();
        } else {
            cardNumberTv.setText("卡号:" + cardNumber);
            cardYueTv.setText("余额:" + cardYue + "元");
        }
    }
    
    /**
     * 关闭当前页面
     * 
     * @param v
     */
    public void backPrePageClick(View v) {
        YueActivity.this.finish();
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
