package com.yhtye.shanghaishishigongjiaochaxun;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baidu.mobads.AdSettings;
import com.baidu.mobads.AdView;
import com.umeng.analytics.MobclickAgent;
import com.yhtye.shanghaishishigongjiaochaxun.R;
import com.yhtye.shgongjiao.entity.HistoryInfo;
import com.yhtye.shgongjiao.entity.PositionInfo;
import com.yhtye.shgongjiao.myui.HistoryListAdapter;
import com.yhtye.shgongjiao.myui.IconListAdapter;
import com.yhtye.shgongjiao.service.BaiduApiService;
import com.yhtye.shgongjiao.service.HistoryService;
import com.yhtye.shgongjiao.tools.NetUtil;
import com.yhtye.shgongjiao.tools.RegularUtil;
import com.yhtye.shgongjiao.tools.ThreadPoolManagerFactory;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements OnItemClickListener {

    private EditText numberoneEditText = null;
    private EditText gongjiaokahaonumberoneEditText = null;
    
    private Intent intent=new Intent(); 
    
    // 顶部banner
    private TextView bannerrtitle = null;
    
    // 三个面板
    private LinearLayout huanchenglayout = null;
    private LinearLayout shishichaxunlayout = null;
    private LinearLayout yuechaxunlayout = null;
    
    // 换乘查询元素
    private EditText qidianEditText = null;
    private EditText zongdianEditText = null;
    
    private PositionInfo myPosition = null;
    
    private HistoryService historyService = null;
    
    // List 历史记录
    private HistoryListAdapter adapter;
    private ListView listHistoryView = null;
    
    // 广告
    private AdView adView;
    
    // 底部工具栏
    private GridView gview;
    private IconListAdapter simAdapter;
    private boolean[] isCurrentItems;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initBar();
        
        // 通过经纬度查询附件站点
        ThreadPoolManagerFactory.getInstance().execute(new SearchNearStationsRunable());
        
        // 初始化底部栏
        initIcons();
        
        // 查询历史记录
        showHistory();
        
        // 加载广告
        if (NetUtil.checkNet(this)) {
            initAd();
        }
    }
    
    /**
     * 初始化底部icon面板
     */
    private void initIcons() {
        isCurrentItems = new boolean[3];
        isCurrentItems[0] = true;
        isCurrentItems[1] = false;
        isCurrentItems[2] = false;
        gview = (GridView) findViewById(R.id.gview);
        simAdapter = new IconListAdapter(this, isCurrentItems);
        // 配置适配器
        gview.setAdapter(simAdapter);
        gview.setOnItemClickListener(new ItemClickListener());
    }
    
    /**
     * 初始化按钮和界面元素
     */
    private void initBar() {
        // 获取输入信息
        numberoneEditText = (EditText)findViewById(R.id.numberone);
        
        numberoneEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchLineClick(v);
                }
                return true;
            }
        });
        
        bannerrtitle = (TextView) findViewById(R.id.bannerrtitle);
        
        // 初始化2个布局
        shishichaxunlayout = (LinearLayout) findViewById(R.id.shishichaxunlayout);
        huanchenglayout = (LinearLayout) findViewById(R.id.huanchenglayout);
        yuechaxunlayout = (LinearLayout) findViewById(R.id.yuechaxunlayout);
        
        listHistoryView = (ListView) shishichaxunlayout.findViewById(R.id.list_history_line);
        historyService = new HistoryService(MainActivity.this);
    }
    
    private void initAd() {        
         // 人群属性
         AdSettings.setKey(new String[] { "baidu", "中国" });
         AdSettings.setCity("上海");
         
         // 创建广告View
         String adPlaceId = "2422749"; // 重要：不填写代码位id不能出广告
         adView = new AdView(this, adPlaceId);
         
         LinearLayout baiduguanggaolayout = (LinearLayout) findViewById(R.id.baiduguanggao);
         baiduguanggaolayout.addView(adView);
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
        // 初始化界面元素
        shishichaxunlayout.setVisibility(View.VISIBLE);
        huanchenglayout.setVisibility(View.GONE);
        yuechaxunlayout.setVisibility(View.GONE);
    }
    
    /**
     * 换乘查询按钮
     * 
     * @param v
     */
    public void huanshengchaxunClick(View v) {
        // 初始化界面元素
        huanchenglayout.setVisibility(View.VISIBLE);
        shishichaxunlayout.setVisibility(View.GONE);
        yuechaxunlayout.setVisibility(View.GONE);
        initHuansheng();
    }
    
    /**
     * 余额查询按钮
     * 
     * @param v
     */
    public void yuechaxunClick(View v) {
        // 初始化界面元素
        yuechaxunlayout.setVisibility(View.VISIBLE);
        huanchenglayout.setVisibility(View.GONE);
        shishichaxunlayout.setVisibility(View.GONE);
        
        // 初始化页面元素
        if (gongjiaokahaonumberoneEditText == null) {
            gongjiaokahaonumberoneEditText = (EditText) findViewById(R.id.gongjiaokahaonumberone);
            // 调用数字键盘
            gongjiaokahaonumberoneEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            // 软键盘设置
            gongjiaokahaonumberoneEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId,
                        KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {  
                        searchyueClick(v);
                    }
                    return true;
                }
            });
        }
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
        if (RegularUtil.isNumeric(lineName)) {
            lineName = lineName + "路";
        }
        
        // 统计
        Map<String,String> m = new HashMap<String,String>();
        m.put("lineName", lineName);
        MobclickAgent.onEventValue(MainActivity.this, "searchline", m, Integer.MAX_VALUE);
        
        // 切换Activity
        intent.setClass(MainActivity.this, ResultActivity.class);  
        intent.putExtra("lineName", lineName);
        startActivity(intent);
    }
    
    /**
     * 余额查询—余额查询
     * 
     * @param v
     */
    public void searchyueClick(View v) {
        // 检查网络
        if (!NetUtil.checkNet(MainActivity.this)) {
            Toast.makeText(MainActivity.this, R.string.network_tip, Toast.LENGTH_LONG).show();
            return;
        }
        
        String gongjiaokahao = gongjiaokahaonumberoneEditText.getText().toString();
        if (TextUtils.isEmpty(gongjiaokahao)) {
            Toast.makeText(MainActivity.this, "请输入要查询的公交卡号", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 上海公交卡号全部为数字
        String regEx="[^0-9]";   
        Pattern p = Pattern.compile(regEx);   
        Matcher m = p.matcher(gongjiaokahao);   
        gongjiaokahao = m.replaceAll("").trim();
        
        // 切换Activity
        intent.setClass(MainActivity.this, YueActivity.class);  
        intent.putExtra("carNumber", gongjiaokahao);
        startActivity(intent);
    }
    
    class  ItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            for (int i = 0; i < isCurrentItems.length; i++) {
                isCurrentItems[i] = (i == position);
            }
            if (position == 0) {
                bannerrtitle.setText("实时公交");
                shishichaxunClick(view);
            } else if (position == 1) {
                bannerrtitle.setText("换乘查询");
                huanshengchaxunClick(view);
            } else {
                bannerrtitle.setText("公交卡余额查询");
                yuechaxunClick(view);
            }
            
            simAdapter.notifyDataSetChanged();
        }
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
    
    public static  List<String> stationNameList;
    
    /**
     * 通过经纬度查询站点信息
     *
     */
    private class SearchNearStationsRunable  implements Runnable {
        
        @Override
        public void run() {
            try {
                myPosition = NetUtil.checkGps(MainActivity.this);
                if (myPosition == null) {
                    return;
                }
                
                stationNameList = BaiduApiService.getNearStations(myPosition);
                if (stationNameList == null || stationNameList.size() == 0) {
                    return;
                }
            } catch (Exception e) {
                
            }
        }
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        // 点击历史记录 
        HistoryInfo historyInfo = historyList.get(position);
        if (historyInfo == null) {
            return;
        }
        
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("historyInfo", historyInfo);
        bundle.putString("flag", "history");
        bundle.putString("lineName", historyInfo.getLineName());
        bundle.putBoolean("direction", historyInfo.isDirection());
        intent.putExtras(bundle);
        
        intent.setClass(MainActivity.this, ResultActivity.class);  
        
        Map<String,String> m = new HashMap<String,String>();
        m.put("lineName", historyInfo.getLineName());
        m.put("direction", historyInfo.isDirection() +"");
        MobclickAgent.onEventValue(MainActivity.this, "historyclick", m, Integer.MAX_VALUE);
        
        startActivity(intent);
    }
    
    /**
     * Activity销毁时，销毁adView
     */
    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
