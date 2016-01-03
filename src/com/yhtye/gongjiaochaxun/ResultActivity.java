package com.yhtye.gongjiaochaxun;

import java.lang.ref.WeakReference;

import org.codehaus.jackson.map.ObjectMapper;

import com.yhtye.wuhan.R;
import com.yhtye.gongjiao.entity.BusLineInfo;
import com.yhtye.gongjiao.entity.HistoryInfo;
import com.yhtye.gongjiao.entity.LineInfo;
import com.yhtye.gongjiao.service.HistoryService;
import com.yhtye.gongjiao.service.LineService;
import com.yhtye.gongjiao.tools.NetUtil;
import com.yhtye.gongjiao.tools.ThreadPoolManagerFactory;

import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ResultActivity extends BaseActivity implements OnItemClickListener {
    // 定义消息标记
    private static final int NoLineMessage = 1;
    private static final int StationsMessage = 2;
    private static final int CarsMessage = 3;
    
    // 路线名称
    private String lineName = null;
    // 线路信息
    private BusLineInfo trueBusLine = null;
    private BusLineInfo falseBusLine = null;
    // item的状态
    private boolean[] isCurrentItems;
    // 方向
    private boolean direction = true;
    private LineService lineService = new LineService();
    
    private TextView linenameTextView = null;
    private RelativeLayout lineinfoLayout = null;
    private TextView qidianTextView = null;
    private TextView zhongdianTextView = null;
    private TextView startimeTextView = null;
    private TextView stoptimeTextView = null;
    
    private ListView lv_cards; 
    private FlexListAdapter adapter;
    private Handler handler = null;
    
    private HistoryService historyService = null;
    private ProgressDialog progressDialog = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        
        Intent intent = getIntent();
        lineName = intent.getStringExtra("lineName");
        direction = intent.getBooleanExtra("direction", true);
        String linelist = intent.getStringExtra("linelist");
        
        initView();
        if (TextUtils.isEmpty(linelist)) {
            // 启动新线程获取线路信息
            progressDialog.show();
            ThreadPoolManagerFactory.getInstance().execute(new SearchLineRunable(lineName, direction, StationsMessage));
        } else {
            boolean success = parseLineList(linelist, direction);
            Message msg2=new Message();
            if (success) {
                msg2.what = StationsMessage;
            } else {
                msg2.what = NoLineMessage;
            }
            handler.sendMessage(msg2);
        }
    }
    
    /**
     * 解析lineList
     * 
     * @param content
     */
    private boolean parseLineList(String content, boolean direction) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            BusLineInfo busLine = mapper.readValue(content, BusLineInfo.class);
            if (busLine != null) {
                if (direction) {
                    trueBusLine = busLine;
                } else {
                    falseBusLine = busLine;
                }
                return true;
            }
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
        return false;
    }
    
    /**
     * 查询公交线路和站点
     */
    private class SearchLineRunable implements Runnable {
        private String nowLineName;
        private boolean direction;
        private int messageFlag;
        
        public SearchLineRunable(String lineName, boolean direction, int messageFlag) {
            this.nowLineName = lineName;
            this.direction = direction;
            this.messageFlag = messageFlag;
        }
        
        @Override
        public void run() {
            if (TextUtils.isEmpty(nowLineName)) {
                return;
            }
  
            BusLineInfo busLine = lineService.getLineInfo(nowLineName, direction, 1);
            if (lineName == null || !nowLineName.equals(lineName)) {
                // 如果用户已切换了路线，抛弃之前的结果不再继续处理
                return;
            }

           if (direction) {
               trueBusLine = busLine;
           } else {
               falseBusLine = busLine;
           }
           if (busLine == null) {
                // 没有线路信息
                Message msg = new Message();
                msg.what = NoLineMessage;
                handler.sendMessage(msg);
           } else {
               // 没有线路信息
               Message msg = new Message();
               msg.what = messageFlag;
               handler.sendMessage(msg);
           }
        }
    }
    
    private static class ResultHandler extends Handler {
        private WeakReference<ResultActivity> mActivity;
        
        public ResultHandler(ResultActivity activity) {
            this.mActivity = new WeakReference<ResultActivity>(activity); 
        }
        
        @Override  
        public void handleMessage(Message msg) {  
            final ResultActivity  theActivity =  mActivity.get();
            theActivity.progressDialog.dismiss();
            
            int messageFlag = msg.what;
            if (messageFlag == NoLineMessage) {
                // 没有该线路
                Toast.makeText(theActivity, R.string.no_line, Toast.LENGTH_LONG).show();
                theActivity.finish();
            } else if (messageFlag == StationsMessage) {
                // 站点信息
                BusLineInfo lineInfo = theActivity.getNowLineInfo();
                theActivity.showLineInfo(lineInfo);
                theActivity.lineinfoLayout.setVisibility(View.VISIBLE);
                theActivity.showStations(theActivity, lineInfo);
                theActivity.lv_cards.setAdapter(theActivity.adapter);
                
                theActivity.lv_cards.setOnItemClickListener(theActivity);
            } else if (messageFlag == CarsMessage) {
                // 即时刷新  
                theActivity.adapter.notifyDataSetChanged(); 
            }
        }
    }
    
    /**
     * 交换方向
     * 
     * @param v
     */
    public void switchDirectionClick(View v) {
        direction = !direction;
        BusLineInfo lineInfo = getNowLineInfo();
        if (lineInfo != null && lineInfo.getStops() != null) {
            showLineInfo(lineInfo);
            showStations(this, lineInfo);
            // 即时刷新
            adapter.notifyDataSetChanged(); 
        } else {
            // 加载站点信息
            progressDialog.show();
            ThreadPoolManagerFactory.getInstance().execute(new SearchLineRunable(lineName, direction, StationsMessage));
        }
    }
    
    /**
     * 关闭当前页面
     * 
     * @param v
     */
    public void backPrePageClick(View v) {
        progressDialog.dismiss();
        ResultActivity.this.finish();
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        if (position < 0 || position >= isCurrentItems.length) {
            return;
        }
        
        // 检查网络
        if (!NetUtil.checkNet(ResultActivity.this)) {
            Toast.makeText(ResultActivity.this, R.string.network_tip, Toast.LENGTH_SHORT).show();
            return;
        }
        
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
        if (isCurrentItems[position]) {
            // 启动线程
            progressDialog.show();
            ThreadPoolManagerFactory.getInstance().execute(new SearchLineRunable(lineName, direction, CarsMessage));
        } else {
            // 即时刷新  
            adapter.notifyDataSetChanged(); 
        }
    }
    
    /**
     * 初始化
     */
    private void initView() {
        handler = new ResultHandler(this);
        lv_cards = (ListView) findViewById(R.id.list_cards);
        
        linenameTextView = (TextView)findViewById(R.id.linename);
        setLineName(lineName);
        
        lineinfoLayout = (RelativeLayout)findViewById(R.id.lineinfo);
        qidianTextView = (TextView)findViewById(R.id.qidian);
        zhongdianTextView = (TextView)findViewById(R.id.zhongdian);
        startimeTextView = (TextView)findViewById(R.id.startime);
        stoptimeTextView = (TextView)findViewById(R.id.stoptime);
        
        // 初始化
        historyService = new HistoryService(ResultActivity.this);
        progressDialog = new ProgressDialog(ResultActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("数据加载中，请耐心等待……");
    }
    
    /**
     * 取出当前的LineInfo
     * 
     * @return
     */
    private BusLineInfo getNowLineInfo() {
        if (direction) {
            return trueBusLine;
        }
        return falseBusLine;
    }
    
    /**
     * 展示线路信息
     */
    private void showLineInfo(BusLineInfo busLine) {
        if (busLine == null || busLine.getLine() == null) {
            return ;
        }
        LineInfo lineInfo = busLine.getLine();
        
        if (!TextUtils.isEmpty(lineInfo.getStartStopName())) {
            qidianTextView.setText(lineInfo.getStartStopName());
            zhongdianTextView.setText(lineInfo.getEndStopName());
            startimeTextView.setText(lineInfo.getFirstTime());
            stoptimeTextView.setText(lineInfo.getLastTime());
        }
        
        // 记录搜索历史
        String fangxiang = String.format("%s路(%s -> %s)", lineInfo.getLineName(), 
                lineInfo.getStartStopName(), lineInfo.getEndStopName());
        historyService.appendHistory(new HistoryInfo(fangxiang, 
                lineInfo.getDirection() == 0 ? false : true, busLine));
    }
    
    private void setLineName(String name) {
        linenameTextView.setText(lineName + "路");
    }
    
    private void showStations(ResultActivity activity, BusLineInfo busLine) {
        if (busLine == null || busLine.getStops() == null) {
            return;
        }
        if (adapter == null) {
            adapter = new FlexListAdapter(activity);
        }
        
        isCurrentItems = new boolean[busLine.getStops().size()];
        adapter.setStations(busLine.getStops());
        adapter.setBus(busLine.getBus());
        
        // 刚进入的时候全部条目显示闭合状态  
        for (int i = 0; i < isCurrentItems.length; i++) {  
            isCurrentItems[i] = false;  
        }
        
        adapter.setIsCurrentItems(isCurrentItems);
    }
}
