package com.yhtye.gongjiaochaxun;

import java.lang.ref.WeakReference;
import java.util.List;

import com.umeng.analytics.MobclickAgent;
import com.yhtye.beijingshishigongjiaochaxun.R;
import com.yhtye.gongjiao.entity.HistoryInfo;
import com.yhtye.gongjiao.entity.LineInfo;
import com.yhtye.gongjiao.entity.StationInfo;
import com.yhtye.gongjiao.service.HistoryService;
import com.yhtye.gongjiao.service.LineService;
import com.yhtye.gongjiao.tools.NetUtil;
import com.yhtye.gongjiao.tools.ThreadPoolManagerFactory;

import android.app.Activity;
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

public class ResultActivity extends Activity implements OnItemClickListener {
    // 定义消息标记
    private static final int NoLineMessage = 1;
    private static final int StationsMessage = 2;
    private static final int CarsMessage = 3;
    private static final int OtherStationsMessage = 4;
    
    // 路线名称
    private String lineName = null;
    // 线路信息 
    private List<LineInfo> lineList = null;
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
    
    // 正反方向初始化滚动位置
    private int truePosition = -1;
    private int falsePosition = -1;
    
    private HistoryService historyService = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        
        Intent intent = getIntent();
        lineName = intent.getStringExtra("lineName");
        direction = intent.getBooleanExtra("direction", true);
        
        init();
        
        // 启动新线程获取线路信息
        ThreadPoolManagerFactory.getInstance().execute(new SearchLineRunable(lineName));
    }
    
    /**
     * 查询公交线路和站点
     */
    private class SearchLineRunable implements Runnable {
        private String nowLineName;
        
        public SearchLineRunable(String lineName) {
            this.nowLineName = lineName;
        }
        
        @Override
        public void run() {
            if (TextUtils.isEmpty(nowLineName)) {
                return;
            }
            List<LineInfo> newlineList = lineService.getLineInfo(nowLineName, 1);
            if (lineName == null || !nowLineName.equals(lineName)) {
                // 如果用户已切换了路线，抛弃之前的结果不再继续处理
                return;
            }
            lineList = newlineList;
            
            if (lineList != null && lineList.size() > 0) {
                LineInfo lineInfo = getNowLineInfo();
                lineInfo = lineService.getLineStation(lineInfo, 1, true); 
                if (lineName == null || !nowLineName.equals(lineName)) {
                    // 如果用户已切换了路线，抛弃之前的结果不再继续处理
                    return;
                }
                if (lineInfo.getStations() != null) {
                    Message msg2=new Message();
                    msg2.what = StationsMessage;
                    handler.sendMessage(msg2);
                    return;
                }
            }
            // 没有线路信息
            Message msg = new Message();
            msg.what = NoLineMessage;
            handler.sendMessage(msg);
        }
    }
    
    /**
     * 查询线路停靠站点
     *
     */
    private class SearchLineStationRunable implements Runnable {
        
        @Override
        public void run() {
            if (lineList != null && lineList.size() > 0) {
                LineInfo lineInfo = getNowLineInfo();
                lineInfo = lineService.getLineStation(lineInfo, 1, true); 
                if (lineInfo.getStations() != null) {
                    Message msg2=new Message();
                    msg2.what = OtherStationsMessage;
                    handler.sendMessage(msg2);
                }
            }
        }
    }
    
    /**
     * 实时查询车辆信息
     *
     */
    private class SearchCarsSearchLineRunable  implements Runnable {
        private int position;
        
        public SearchCarsSearchLineRunable(int position) {
            this.position = position;
        }
        
        @Override
        public void run() {
            LineInfo lineInfo = getNowLineInfo();
            if (lineInfo.getStations() == null) {
                lineInfo = lineService.getLineStation(lineInfo, position + 1, true);
            } else {
                lineInfo = lineService.getLineStation(lineInfo, position + 1, false);
            }
            
            Message msg=new Message();  
            msg.what = CarsMessage;
            handler.sendMessage(msg);
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
            int messageFlag = msg.what;
            if (messageFlag == NoLineMessage) {
                // 没有该线路
                Toast.makeText(theActivity, R.string.no_line, Toast.LENGTH_LONG).show();
                theActivity.finish();
            } else if (messageFlag == StationsMessage) {
                // 初始化
                theActivity.checkListPosition();
                // 站点信息
                LineInfo lineInfo = theActivity.getNowLineInfo();
                theActivity.showLineInfo(lineInfo);
                theActivity.lineinfoLayout.setVisibility(View.VISIBLE);
                theActivity.showStations(theActivity, lineInfo);
                theActivity.lv_cards.setAdapter(theActivity.adapter);
                
                // 尝试滚动
                theActivity.lv_cards.setSelected(true);
                if (theActivity.truePosition >= 4 && theActivity.falsePosition >= 4) { 
                    theActivity.lv_cards.post(new Runnable() {
                        @Override
                        public void run() {
                            theActivity.setListViewPos(theActivity.direction ? theActivity.truePosition : theActivity.falsePosition);
                        }
                    });
                }
                if (theActivity.truePosition >=0 && theActivity.falsePosition >= 0) {
                    theActivity.onItemClick(null, null, theActivity.direction ? theActivity.truePosition : theActivity.falsePosition, 0);
                }
                
                theActivity.lv_cards.setOnItemClickListener(theActivity);
            } else if (messageFlag == CarsMessage) {
                // 即时刷新  
                theActivity.adapter.notifyDataSetChanged(); 
            } else if (messageFlag == OtherStationsMessage) {
                // 站点信息
                LineInfo lineInfo = theActivity.getNowLineInfo();
                theActivity.showLineInfo(lineInfo);
                theActivity.showStations(theActivity, lineInfo);
                // 即时刷新  
                theActivity.adapter.notifyDataSetChanged(); 
            }
        }
    }
    
    /**
     * 定位初始化滚动
     */
    private void checkListPosition() {
//        if (lineStation == null 
//                || MainActivity.stationNameList == null 
//                || MainActivity.stationNameList.size() < 1) {
//            return;
//        }
//        
//        for (String name : MainActivity.stationNameList) {
//            int i = 0;
//            if (falsePosition < 0) {
//                for (StationInfo station : lineStation.getFalseDirection()) {
//                    if (station.getZdmc().equals(name)) {
//                        falsePosition = i;
//                        break;
//                    }
//                    i++;
//                }
//            }
//            
//            i = 0;
//            if (truePosition < 0) { 
//                for (StationInfo station : lineStation.getTrueDirection()) {
//                    if (station.getZdmc().equals(name)) {
//                        truePosition = i;
//                        break;
//                    }
//                    i++;
//                }
//            }
//        }
    }
    
    /**
     * 交换方向
     * 
     * @param v
     */
    public void switchDirectionClick(View v) {
        if (lineList == null || lineList.size() < 2) {
            return;
        }
        direction = !direction;
        LineInfo lineInfo = getNowLineInfo();
        if (lineInfo.getStations() != null) {
            showLineInfo(lineInfo);
            showStations(this, lineInfo);
            // 即时刷新
            adapter.notifyDataSetChanged(); 
        } else {
            // 加载站点信息
            ThreadPoolManagerFactory.getInstance().execute(new SearchLineStationRunable());
        }
        
//        
//        // 尝试滚动
//        lv_cards.setSelected(true);
//        if (truePosition >= 4 && falsePosition >= 4) {
//            lv_cards.post(new Runnable() {
//                @Override
//                public void run() {
//                    setListViewPos(direction ? truePosition : falsePosition);
//                }
//            });
//        }
//        if (truePosition >=0 && falsePosition >= 0) {
//            onItemClick(null, null, direction ? truePosition : falsePosition, 0);
//        }
    }
    
    /**
     * 定位滚动位置
     * 
     * @param pos
     */
    private void setListViewPos(int pos) {
        if (android.os.Build.VERSION.SDK_INT >= 8) {
            lv_cards.smoothScrollToPosition(pos);
        } else {
            lv_cards.setSelection(pos);
        }
    }
    
    /**
     * 关闭当前页面
     * 
     * @param v
     */
    public void backPrePageClick(View v) {
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
            ThreadPoolManagerFactory.getInstance().execute(new SearchCarsSearchLineRunable(position));
        } else {
            // 即时刷新  
            adapter.notifyDataSetChanged(); 
        }
    }
    
    /**
     * 初始化
     */
    private void init() {
        handler = new ResultHandler(this);
        lv_cards = (ListView) findViewById(R.id.list_cards);
        
        linenameTextView = (TextView)findViewById(R.id.linename);
        linenameTextView.setText(lineName);
        
        lineinfoLayout = (RelativeLayout)findViewById(R.id.lineinfo);
        qidianTextView = (TextView)findViewById(R.id.qidian);
        zhongdianTextView = (TextView)findViewById(R.id.zhongdian);
        startimeTextView = (TextView)findViewById(R.id.startime);
        stoptimeTextView = (TextView)findViewById(R.id.stoptime);
        
        // 初始化
        historyService = new HistoryService(ResultActivity.this);
    }
    
    /**
     * 取出当前的LineInfo
     * 
     * @return
     */
    private LineInfo getNowLineInfo() {
        if (lineList == null || lineList.size() == 0) {
            return null;
        }
        LineInfo lineInfo = null;       
        if (lineList.size() == 1) {
            lineInfo = lineList.get(0);
        } else {
            if (direction) {
                lineInfo = lineList.get(0);
            } else {
                lineInfo = lineList.get(1);
            }
        }
        return lineInfo;
    }
    
    /**
     * 展示线路信息
     */
    private void showLineInfo(LineInfo lineInfo) {
        if (lineInfo == null) {
            return ;
        }
        
//        HistoryInfo history = new HistoryInfo(lineName, direction);
//        history.setTrueFangxiang(lineList.get(0).getFangxiang());
//        history.setTrueLineId(lineList.get(0).getLine_id());
        
        
//        if (lineList.size() == 1) {
//            lineInfo = lineList.get(0);
//            
//        } else {
//            history.setTrueFangxiang(lineList.get(1).getFangxiang());
//            history.setTrueLineId(lineList.get(1).getLine_id());
//            if (direction) {
//                lineInfo = lineList.get(0);
//            } else {
//                lineInfo = lineList.get(1);
//            }
//        }
        linenameTextView.setText(lineInfo.getFangxiang());
        if (!TextUtils.isEmpty(lineInfo.getStart_stop())) {
            qidianTextView.setText(lineInfo.getStart_stop());
            zhongdianTextView.setText(lineInfo.getEnd_stop());
            startimeTextView.setText(lineInfo.getStart_earlytime());
            stoptimeTextView.setText(lineInfo.getEnd_latetime());
        }
        
//        // 记录搜索历史
//        historyService.appendHistory(history);
    }
    
    private void showStations(ResultActivity activity, LineInfo lineInfo) {
        if (lineInfo == null || lineInfo.getStations() == null) {
            return;
        }
        if (adapter == null) {
            adapter = new FlexListAdapter(activity);
        }
        
        isCurrentItems = new boolean[lineInfo.getStations().size()];
        adapter.setStations(lineInfo.getStations());
        
        // 刚进入的时候全部条目显示闭合状态  
        for (int i = 0; i < isCurrentItems.length; i++) {  
            isCurrentItems[i] = false;  
        }
        
        adapter.setIsCurrentItems(isCurrentItems);
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