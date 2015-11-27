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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

public class SchemeActivity extends Activity implements OnItemClickListener {

    private String qidian = null;
    private String zongdian = null;
    
    private RelativeLayout wuschemeLayout = null;
    private ListView listSchemeView = null;
    
    private SchemeListAdapter adapter;
    private boolean[] isCurrentItems; 
    private int[] isOpendItems;
    private List<RoutesScheme> routesSchemeList;
    
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
                            showSchemes(routesList);
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
        listSchemeView = (ListView) findViewById(R.id.list_scheme);
    }
    
    private void showSchemes(List<RoutesScheme> routesSchemeList) {
        isCurrentItems = new boolean[routesSchemeList.size()];
        isOpendItems = new int[routesSchemeList.size()];
        
        // 刚进入的时候第一条打开
        isCurrentItems[0] = true;
        isOpendItems[0] = 1;
        for (int i = 1; i < isCurrentItems.length; i++) {  
            isCurrentItems[i] = false;
            isOpendItems[i] = 0;
        }
        
        if (adapter == null) {
            adapter = new SchemeListAdapter(this, routesSchemeList, 
                    isCurrentItems, isOpendItems, qidian, zongdian);
        }
        listSchemeView.setAdapter(adapter);
        listSchemeView.setOnItemClickListener(this);
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        /* 
         * 只打开一个 
         */  
        for (int i = 0; i < isCurrentItems.length; i++) {  
            if (i != position) {  
                isCurrentItems[i] = false;  
            }
        } 
        // 打开或者合上  
        isCurrentItems[position] = !isCurrentItems[position];
        isOpendItems[position] = isOpendItems[position] + 1;
        // 即时刷新  
        adapter.notifyDataSetChanged(); 
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
