package com.yhtye.gongjiaochaxun;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.BannerView;
import com.umeng.analytics.MobclickAgent;
import com.everpod.beijing.R;
import com.yhtye.gongjiao.entity.HistoryInfo;
import com.yhtye.gongjiao.entity.LineInfo;
import com.yhtye.gongjiao.service.HistoryService;
import com.yhtye.gongjiao.tools.NetUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class MainActivity extends BaseActivity implements OnItemClickListener {

    private EditText numberoneEditText = null;
    
    private Intent intent=new Intent(); 
    
    // 顶部banner
    private TextView bannerrtitle = null;
    
    // 面板
    private LinearLayout huanchenglayout = null;
    private LinearLayout shishichaxunlayout = null;
    
    private EditText qidianEditText = null;
    private EditText zongdianEditText = null;
    
    private HistoryService historyService = null;
    
    // List 历史记录
    private HistoryListAdapter adapter;
    private ListView listHistoryView = null;
    
    // 广告
    private BannerView adView;
    
    // 底部工具栏
    private GridView gview;
    private IconListAdapter simAdapter;
    private boolean[] isCurrentItems;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initBar();
        
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
        
        listHistoryView = (ListView) shishichaxunlayout.findViewById(R.id.list_history_line);
        historyService = new HistoryService(MainActivity.this);
    }
    
    private void initAd() { 
        // 创建广告View
        String appId = "1104971925";
        String adPlaceId = "1020422257558567"; // 重要：不填写代码位id不能出广告
        adView = new BannerView(this, ADSize.BANNER,  appId, adPlaceId);
        adView.setRefresh(30);
        
        LinearLayout baiduguanggaolayout = (LinearLayout) findViewById(R.id.baiduguanggao);
        baiduguanggaolayout.addView(adView);
        
        adView.loadAD();
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
    }
    
    /**
     * 换乘查询按钮
     * 
     * @param v
     */
    public void huanshengchaxunClick(View v) {
        // 初始化界面元素
        shishichaxunlayout.setVisibility(View.GONE);
        huanchenglayout.setVisibility(View.VISIBLE);
        initHuansheng();
    }
    
    /**
     * 小视频按钮
     * 
     * @param v
     */
    public void shipinClick(View v) {
        // TODO
        // 切换Activity
        intent.setClass(MainActivity.this, ContentActivity.class);  
        startActivity(intent);
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
        
        if (lineName.endsWith("路")) {
            lineName = lineName.substring(0, lineName.length() - 1);
        }
        
        if (TextUtils.isEmpty(lineName)) {
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
                bannerrtitle.setText("小视频精选");
                shipinClick(view);
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
