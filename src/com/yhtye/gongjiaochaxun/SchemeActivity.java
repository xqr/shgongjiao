package com.yhtye.gongjiaochaxun;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import com.everpod.beijing.R;
import com.yhtye.gongjiao.entity.RoutesScheme;
import com.yhtye.gongjiao.service.BaiduApiService;
import com.yhtye.gongjiao.tools.ThreadPoolManagerFactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

public class SchemeActivity extends BaseActivity implements OnItemClickListener {
    
    private RelativeLayout wuschemeLayout = null;
    private ListView listSchemeView = null;
    
    private SchemeListAdapter adapter;
    private boolean[] isCurrentItems; 
    private int[] isOpendItems;
    
    private Handler handler = null;
    
    private String qidian;
    private String zhongdian;
    private List<RoutesScheme> routesList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheme);
        
        // 初始化界面或元素
        init();
        
        Intent intent = getIntent();
        
        qidian = intent.getStringExtra("qidian");
        zhongdian = intent.getStringExtra("zongdian");
        
        // 启动线程
        ThreadPoolManagerFactory.getInstance().execute(new SearchSchemeRoutesRunable(false));
    }
    
    private static class ResultHandler extends Handler {
        private WeakReference<SchemeActivity> mActivity;
        
        public ResultHandler(SchemeActivity activity) {
            this.mActivity = new WeakReference<SchemeActivity>(activity); 
        }
        
        @Override  
        public void handleMessage(Message msg) {  
            final SchemeActivity  theActivity =  mActivity.get();
            
            int messageFlag = msg.what;
            if (messageFlag == 1) {
                theActivity.wuschemeLayout.setVisibility(View.VISIBLE);
            } else if (messageFlag == 2) {
                theActivity.showSchemes(theActivity.routesList, theActivity.qidian, theActivity.zhongdian);
            }
        }
    }
    
    /**
     * 查询线路
     */
    public class SearchSchemeRoutesRunable implements Runnable {
        private boolean retry;
        
        public SearchSchemeRoutesRunable(boolean retry) {
            this.retry = retry;
        }
        
        @Override
        public void run() {
            try {
            String responseString = BaiduApiService.getDirectionRoutesResponse(qidian, zhongdian);
            if (TextUtils.isEmpty(responseString)) {
                // 发出message = 1
                Message msg=new Message();  
                msg.what = 1;
                handler.sendMessage(msg);
                return;
            }
            if (!retry 
                    && !responseString.contains("routes")) {
                // 地点模糊
                Map<String, List<String>> resultMap = BaiduApiService.parseAccuratePosition(responseString);
                if (resultMap == null || resultMap.size() == 0) {
                    // 发出message = 1
                    Message msg=new Message();  
                    msg.what = 1;
                    handler.sendMessage(msg);
                    return;
                }
                
                if (resultMap.containsKey("origin")) {
                    for (String tempqidian : resultMap.get("origin")) {
                        if (!TextUtils.isEmpty(tempqidian) && !tempqidian.equals(qidian)) {
                            qidian = tempqidian;
                            break;
                        }
                    }
                }

                if (resultMap.containsKey("destination")) {
                    for (String tempzongdian : resultMap.get("destination")) {
                        if (!TextUtils.isEmpty(tempzongdian) && !tempzongdian.equals(zhongdian)) {
                            zhongdian = tempzongdian;
                            break;
                        }
                    }
                }
                // 重新提交一次
                ThreadPoolManagerFactory.getInstance().execute(new SearchSchemeRoutesRunable(true));
                return;
            }
            routesList = BaiduApiService.parseDirectionRoutes(responseString);
            if (routesList == null || routesList.isEmpty()) {
                Message msg=new Message();  
                msg.what = 1;
                handler.sendMessage(msg);
                return;
            }
            // 发出message = 2，成功处理
            Message msg=new Message();  
            msg.what = 2;
            handler.sendMessage(msg);
        } catch (Exception e) {
            Log.e("paseScheme", e.getMessage());
            Message msg=new Message();  
            msg.what = 1;
            handler.sendMessage(msg);
        }
        }
    }
    
    private void init() {
        handler = new ResultHandler(this);
        
        wuschemeLayout = (RelativeLayout) findViewById(R.id.wuscheme);
        listSchemeView = (ListView) findViewById(R.id.list_scheme);
    }
    
    private void showSchemes(List<RoutesScheme> routesSchemeList, String qidian, String zongdian) {
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
