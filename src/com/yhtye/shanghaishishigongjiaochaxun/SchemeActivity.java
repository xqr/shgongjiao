package com.yhtye.shanghaishishigongjiaochaxun;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import com.baidu.apistore.sdk.ApiCallBack;
import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.apistore.sdk.network.Parameters;
import com.yhtye.shgongjiao.entity.RoutesScheme;
import com.yhtye.shgongjiao.service.BaiduApiService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class SchemeActivity extends Activity {

    private String qidian = null;
    private String zongdian = null;
    
    private RelativeLayout wuschemeLayout = null;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheme);
        
        // 初始化界面或元素
        init();
        
        Intent intent = getIntent();
        qidian = intent.getStringExtra("qidian");
        zongdian = intent.getStringExtra("zongdian");
        
        Parameters params = new Parameters();
        try {
            params.put("origin", URLEncoder.encode(qidian, "UTF-8"));
            params.put("destination", URLEncoder.encode(zongdian, "UTF-8"));
            params.put("mode", "transit");
            params.put("region", URLEncoder.encode("上海", "UTF-8"));
        } catch(Exception e) {
            e.printStackTrace();
        }
        ApiStoreSDK.execute(
                "http://apis.baidu.com/apistore/lbswebapi/direction",                         // 接口地址
                ApiStoreSDK.GET,                  // 接口方法
                params,          // 接口参数
                
                new ApiCallBack() {
                    @Override
                    public void onSuccess(int status, String responseString) {
                        try {
                            Log.i("sdkdemoonSuccess: ", URLDecoder.decode(responseString, "UTF-8"));
                            List<RoutesScheme> routesList = BaiduApiService.parseDirectionRoutes(responseString);
                            if (routesList == null || routesList.isEmpty()) {
                                wuschemeLayout.setVisibility(View.VISIBLE);
                                return;
                            }
                            // TODO 如果正确的话如何处理
                            
                        } catch (UnsupportedEncodingException e) {
                            Log.e("paseScheme", e.getMessage());
                        }
                    }
                    
                    @Override
                    public void onError(int status, String responseString, Exception e) {
                        wuschemeLayout.setVisibility(View.VISIBLE);
                    }
            });
    }
    
    private void init() {
        wuschemeLayout = (RelativeLayout) findViewById(R.id.wuscheme);
    }
    
    
    /**
     * 关闭当前页面
     * 
     * @param v
     */
    public void backPrePageClick(View v) {
        SchemeActivity.this.finish();
    }
    
}
