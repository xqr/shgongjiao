package com.yhtye.shanghaishishigongjiaochaxun;

import java.lang.ref.WeakReference;
import java.util.List;

import com.everpod.shanghai.R;
import com.umeng.analytics.MobclickAgent;
import com.yhtye.shgongjiao.entity.CarInfo;
import com.yhtye.shgongjiao.entity.LineInfo;
import com.yhtye.shgongjiao.entity.LineStationInfo;
import com.yhtye.shgongjiao.entity.StationInfo;
import com.yhtye.shgongjiao.service.LineService;
import com.yhtye.shgongjiao.tools.NetUtil;
import com.yhtye.shgongjiao.tools.ThreadPoolManagerFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ResultActivity extends Activity implements OnItemClickListener {
    // 定义消息标记
    private static final int LineMessage = 1;
    private static final int StationsMessage = 2;
    private static final int CarsMessage = 3;
    
    // 路线名称
    private String lineName = null;
    // 线路信息 
    private LineInfo lineInfo = null;
    // 站点信息
    private LineStationInfo lineStation = null;
    // 车辆信息
    private List<CarInfo> cars = null;
    // item的状态
    private boolean[] isCurrentItems;
    // 方向
    private boolean direction = true;
    private LineService lineService = new LineService();
    
    private RelativeLayout lineinfoLayout = null;
    private TextView qidianTextView = null;
    private TextView zhongdianTextView = null;
    private TextView startimeTextView = null;
    private TextView stoptimeTextView = null;
    
    private ListView lv_cards; 
    private FlexListAdapter adapter;
    private Handler handler = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        
        Intent intent = getIntent();
        lineName = intent.getStringExtra("lineName");
        
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
            LineInfo newlineInfo = lineService.getLineInfo(nowLineName, 2);
            if (lineName == null || !nowLineName.equals(lineName)) {
                // 如果用户已切换了路线，抛弃之前的结果不再继续处理
                return;
            }
            lineInfo = newlineInfo;
            Message msg=new Message();  
            msg.what = LineMessage;
            handler.sendMessage(msg);
            if (lineInfo != null) {
                LineStationInfo newlineStation = lineService.getLineStation(nowLineName, 
                        lineInfo.getLine_id());
                if (lineName == null || !nowLineName.equals(lineName)) {
                    // 如果用户已切换了路线，抛弃之前的结果不再继续处理
                    return;
                }
                lineStation = newlineStation;
                if (lineStation != null) {
                    Message msg2=new Message();
                    msg2.what = StationsMessage;
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
            StationInfo stationInfo = null;
            if (direction) {
                stationInfo = lineStation.getTrueDirection().get(position);
            } else {
                stationInfo = lineStation.getFalseDirection().get(position);
            }
            List<CarInfo> newCars = lineService.getStationCars(lineName, lineInfo.getLine_id(), 
                    stationInfo.getId(), direction);
            // TODO 已经不是这个站点的信息
            cars = newCars;
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
            ResultActivity  theActivity =  mActivity.get();
            int messageFlag = msg.what;
            if (messageFlag == LineMessage) {
                // 线路信息
                if (theActivity.lineInfo == null) {
                    // 没有该线路
                    Toast.makeText(theActivity, R.string.no_line, Toast.LENGTH_LONG).show();
                    theActivity.finish();
                } else {
                    theActivity.showLineInfo();
                    theActivity.lineinfoLayout.setVisibility(View.VISIBLE);
                }
            } else if (messageFlag == StationsMessage) {
                // 站点信息
                theActivity.showStations(theActivity);
                theActivity.lv_cards.setAdapter(theActivity.adapter);
                theActivity.lv_cards.setOnItemClickListener(theActivity);
            } else if (messageFlag == CarsMessage) {
                // 车辆信息
                theActivity.adapter.setCars(theActivity.cars);
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
        showLineInfo();
        
        showStations(this);
        // 即时刷新  
        if (adapter != null) {
            adapter.notifyDataSetChanged(); 
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
        
        TextView linenameTextView = (TextView)findViewById(R.id.linename);
        linenameTextView.setText(lineName);
        
        lineinfoLayout = (RelativeLayout)findViewById(R.id.lineinfo);
        qidianTextView = (TextView)findViewById(R.id.qidian);
        zhongdianTextView = (TextView)findViewById(R.id.zhongdian);
        startimeTextView = (TextView)findViewById(R.id.startime);
        stoptimeTextView = (TextView)findViewById(R.id.stoptime);
    }
    
    /**
     * 展示线路信息
     */
    private void showLineInfo() {
        if (lineInfo == null) {
            return ;
        }
        if (direction) {
            qidianTextView.setText(lineInfo.getStart_stop());
            zhongdianTextView.setText(lineInfo.getEnd_stop());
            startimeTextView.setText(lineInfo.getStart_earlytime());
            stoptimeTextView.setText(lineInfo.getStart_latetime());
        } else {
            qidianTextView.setText(lineInfo.getEnd_stop());
            zhongdianTextView.setText(lineInfo.getStart_stop());
            startimeTextView.setText(lineInfo.getEnd_earlytime());
            stoptimeTextView.setText(lineInfo.getEnd_latetime());
        }
    }
    
    private void showStations(ResultActivity activity) {
        if (lineStation == null) {
            return;
        }
        if (adapter == null) {
            adapter = new FlexListAdapter(activity);
        }
        if (direction) {
            // 正向
            isCurrentItems = new boolean[lineStation.getTrueDirection().size()];
            adapter.setStations(lineStation.getTrueDirection());
        } else {
            // 反向
            isCurrentItems = new boolean[lineStation.getFalseDirection().size()];
            adapter.setStations(lineStation.getFalseDirection());
        }
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
