package com.yhtye.gongjiaochaxun;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.yhtye.beijingshishigongjiaochaxun.R;
import com.yhtye.gongjiao.entity.HistoryInfo;
import com.yhtye.gongjiao.entity.LineInfo;
import com.yhtye.gongjiao.entity.PositionInfo;
import com.yhtye.gongjiao.service.BaiduApiService;
import com.yhtye.gongjiao.service.HistoryService;
import com.yhtye.gongjiao.tools.NetUtil;
import com.yhtye.gongjiao.tools.RegularUtil;
import com.yhtye.gongjiao.tools.ThreadPoolManagerFactory;

import android.content.Intent;
import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends BaseActivity implements OnItemClickListener {

    private EditText numberoneEditText = null;
    
    private Intent intent=new Intent(); 
    
    private Button shishichaxunButton = null;
    private Button huanshengchaxunButton = null;
    
    private LinearLayout huanchenglayout = null;
    private LinearLayout shishichaxunlayout = null;
    
    private EditText qidianEditText = null;
    private EditText zongdianEditText = null;
    
    private PositionInfo myPosition = null;
    
//    private Handler handler = null;
    
    private HistoryService historyService = null;
    
    // List 历史记录
    private HistoryListAdapter adapter;
    private ListView listHistoryView = null;
    
    // 附近站点
//    private NearListParentsAdapter adapter;
//    private boolean[] isCurrentItems; 
//    private ListView listSchemeView = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 自动更新检查(wifi环境下触发)
        UmengUpdateAgent.update(this);
        // 全量更新
        UmengUpdateAgent.setDeltaUpdate(false);
        
        initBar();
        
//        // 通过经纬度查询附件站点
//        ThreadPoolManagerFactory.getInstance().execute(new SearchNearStationsRunable());
        
        // 查询历史记录
        showHistory();
    }
    
    /**
     * 初始化按钮和界面元素
     */
    private void initBar() {
//        handler = new ResultHandler(this);
        
        shishichaxunButton = (Button) findViewById(R.id.shishichaxun);
        huanshengchaxunButton = (Button) findViewById(R.id.huanshengchaxun);
        // 获取输入信息
        numberoneEditText = (EditText)findViewById(R.id.numberone);
        
        // 初始化
        shishichaxunButton.setSelected(true);
        huanshengchaxunButton.setSelected(false);
        
        // 初始化2个布局
        shishichaxunlayout = (LinearLayout) findViewById(R.id.shishichaxunlayout);
        huanchenglayout = (LinearLayout) findViewById(R.id.huanchenglayout);
        
//        listSchemeView = (ListView) shishichaxunlayout.findViewById(R.id.list_near_station);
        listHistoryView = (ListView) shishichaxunlayout.findViewById(R.id.list_history_line);
        historyService = new HistoryService(MainActivity.this);
    }
    
    /**
     * 初始化换乘界面元素
     */
    private void initHuansheng() {
        if (qidianEditText == null) {
            qidianEditText = (EditText) findViewById(R.id.qiidianEdit);
        }
        if (zongdianEditText == null) {
            zongdianEditText = (EditText) findViewById(R.id.zongdianEdit);
        }
    }
    
    /**
     * 实时查询按钮
     * 
     * @param v
     */
    public void shishichaxunClick(View v) {
        // 按钮变化
        shishichaxunButton.setTextColor(getResources().getColor(R.color.blue));
        shishichaxunButton.setSelected(true);
        huanshengchaxunButton.setTextColor(getResources().getColor(R.color.white));
        huanshengchaxunButton.setSelected(false);
        
        // 初始化界面元素
        shishichaxunlayout.setVisibility(View.VISIBLE);
        huanchenglayout.setVisibility(View.GONE);
    }
    
    /**
     * 换乘查询按钮
     * 
     * @param v
     */
    public void huanshengchaxunClick(View v) {
        // 按钮变化
        shishichaxunButton.setTextColor(getResources().getColor(R.color.white));
        shishichaxunButton.setSelected(false);
        huanshengchaxunButton.setTextColor(getResources().getColor(R.color.blue));
        huanshengchaxunButton.setSelected(true);
        // 初始化界面元素
        shishichaxunlayout.setVisibility(View.GONE);
        huanchenglayout.setVisibility(View.VISIBLE);
        initHuansheng();
    }
    
    /**
     * 换乘查询—交换起点和终点
     * 
     * @param v
     */
    public void switchStationsClick(View v) {
        String qidianText = qidianEditText.getText().toString();
        String zongdianText = zongdianEditText.getText().toString();
        qidianEditText.setText(zongdianText);
        zongdianEditText.setText(qidianText);
    }
    
    /**
     * 换乘查询—换乘路线查询
     * 
     * @param v
     */
    public void searchRoutesClick(View v) {
        // 检查网络
        if (!NetUtil.checkNet(MainActivity.this)) {
            Toast.makeText(MainActivity.this, R.string.network_tip, Toast.LENGTH_LONG).show();
            return;
        }
        
        String qidianText = qidianEditText.getText().toString();
        String zongdianText = zongdianEditText.getText().toString();
        if (TextUtils.isEmpty(qidianText)) {
            Toast.makeText(MainActivity.this, "请输入要查询的线路起点", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(zongdianText)) {
            Toast.makeText(MainActivity.this, "请输入要查询的线路终点", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 切换Activity
        intent.setClass(MainActivity.this, SchemeActivity.class);  
        intent.putExtra("qidian", qidianText);
        intent.putExtra("zongdian", zongdianText);
        startActivity(intent);
    }
    
    /**
     * 实时公交—线路查询按钮点击事件响应
     * 
     * @param v
     */
    public void searchLineClick(View v) {
        // 检查网络
        if (!NetUtil.checkNet(MainActivity.this)) {
            Toast.makeText(MainActivity.this, R.string.network_tip, Toast.LENGTH_LONG).show();
            return;
        }
        
        String lineName = numberoneEditText.getText().toString();
        if (TextUtils.isEmpty(lineName)) {
            Toast.makeText(MainActivity.this, "请输入要查询的公交路线", Toast.LENGTH_SHORT).show();
            return;
        }
        // 校验输入的完整性
        if (!RegularUtil.isNumeric(lineName)) {
            Toast.makeText(MainActivity.this, "请输入要查询的公交路线", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 统计
        Map<String,String> m = new HashMap<String,String>();
        m.put("lineName", lineName);
        MobclickAgent.onEventValue(MainActivity.this, "searchline", m, Integer.MAX_VALUE);
        
        // 切换Activity
        intent.setClass(MainActivity.this, ResultActivity.class);  
        intent.putExtra("lineName", lineName);
        intent.putExtra("direction", true);
        intent.putExtra("linelist", "");
        startActivity(intent);
    }
    
    private List<HistoryInfo> historyList = null;
    private void showHistory() {
        historyList = historyService.getHistory();
        if (historyList == null || historyList.size() == 0) {
            return;
        }
        if (adapter == null) {
            adapter = new HistoryListAdapter(MainActivity.this, historyList);
        }
        listHistoryView.setAdapter(adapter);
        listHistoryView.setOnItemClickListener(this);
    }
    
    
//    private  Map<String, List<StopStation>> stationMap = new HashMap<String, List<StopStation>>();
//    public static  List<String> stationNameList;
//    
//    /**
//     * 通过经纬度查询站点信息
//     *
//     */
//    private class SearchNearStationsRunable  implements Runnable {
//        
//        @Override
//        public void run() {
//            try {
//                myPosition = NetUtil.checkGps(MainActivity.this);
//                if (myPosition == null) {
//                    return;
//                }
//                
////                myPosition = new PositionInfo(31.256361, 121.58719);
//                
//                stationNameList = BaiduApiService.getNearStations(myPosition);
//                if (stationNameList == null || stationNameList.size() == 0) {
//                    return;
//                }
//            } catch (Exception e) {
//                
//            }
////            String name = stationNameList.get(0);
////            List<StopStation> list = SprznyService.searchStationLines(name);
////            if (list != null && list.size() > 0) {
////                stationMap.put(name, list);
////            }
////            
////            // 数据展开
////            Message msg=new Message();
////            msg.what = 1;
////            handler.sendMessage(msg);
//        }
//    }
    
//    private class SearchNearStationCarRunable  implements Runnable {
//        private String stationName;
//        
//        public SearchNearStationCarRunable(String stationName) {
//            this.stationName = stationName;
//        }
//        
//        @Override
//        public void run() {
//            List<StopStation> list = SprznyService.searchStationLines(stationName);
//            if (list != null && list.size() > 0) {
//                stationMap.put(stationName, list);
//            }
//            Message msg=new Message();
//            msg.what = 2;
//            handler.sendMessage(msg);
//        }
//    }
    
//    private static class ResultHandler extends Handler {
//        private WeakReference<MainActivity> mActivity;
//        
//        public ResultHandler(MainActivity activity) {
//            this.mActivity = new WeakReference<MainActivity>(activity); 
//        }
//        
//        @Override  
//        public void handleMessage(Message msg) {  
//            MainActivity  theActivity =  mActivity.get();
//            
//            int messageFlag = msg.what;
//            if (messageFlag == 1) {
//                theActivity.showNearStationList(theActivity.stationNameList, theActivity.stationMap);
//            } else if (messageFlag == 2) {
//                // 即时刷新  
//                theActivity.adapter.notifyDataSetChanged(); 
//            }
//        }
//    }
    
//    private void showNearStationList(List<String> stations, Map<String, List<StopStation>> stationMap) {
//        isCurrentItems = new boolean[stations.size()];
//        
//        // 刚进入的时候第一条打开
//        isCurrentItems[0] = true;
//        for (int i = 1; i < isCurrentItems.length; i++) {  
//            isCurrentItems[i] = false;
//        }
//        
//        if (adapter == null) {
//            adapter = new NearListParentsAdapter(this, isCurrentItems,
//                    stations, stationMap);
//        }
//        listSchemeView.setAdapter(adapter);
//        listSchemeView.setOnItemClickListener(this);
//    }
    
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position,
//            long id) {
//        // 检查网络
//        if (!NetUtil.checkNet(MainActivity.this)) {
//            Toast.makeText(MainActivity.this, R.string.network_tip, Toast.LENGTH_SHORT).show();
//            return;
//        }
//        
//        /* 
//         * 只打开一个 
//         */  
//        for (int i = 0; i < isCurrentItems.length; i++) {  
//            if (i != position) {  
//                isCurrentItems[i] = false;  
//            }
//        } 
//        // 打开或者合上  
//        isCurrentItems[position] = !isCurrentItems[position];
//        if (isCurrentItems[position]) {
//            // 启动线程
//            ThreadPoolManagerFactory.getInstance()
//                .execute(new SearchNearStationCarRunable(stationNameList.get(position)));
//        } else {
//            // 即时刷新  
//            adapter.notifyDataSetChanged(); 
//        }
//    }
    
    public String getLinestoString(List<LineInfo> lineList) {
        if (lineList == null) {
            return null;
        }
        StringWriter str=new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(str, lineList);
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
        return str.toString();
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        // 点击历史记录 
        HistoryInfo historyInfo = historyList.get(position);
        if (historyInfo == null) {
            return;
        }
        
        intent.setClass(MainActivity.this, ResultActivity.class);  
        intent.putExtra("lineName", historyInfo.getLineFangxiang());
        intent.putExtra("direction", historyInfo.isDirection());
        intent.putExtra("linelist", getLinestoString(historyInfo.getLineList()));
        
        Map<String,String> m = new HashMap<String,String>();
        m.put("lineName", historyInfo.getLineFangxiang());
        m.put("direction", historyInfo.isDirection() +"");
        MobclickAgent.onEventValue(MainActivity.this, "historyclick", m, Integer.MAX_VALUE);
        
        startActivity(intent);
    }
}
