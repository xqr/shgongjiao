package com.yhtye.shanghaishishigongjiaochaxun;

import java.lang.ref.WeakReference;
import java.util.List;

import com.everpod.shanghai.R;
import com.yhtye.shgongjiao.adapter.FlexListAdapter;
import com.yhtye.shgongjiao.entity.CarInfo;
import com.yhtye.shgongjiao.entity.HistoryInfo;
import com.yhtye.shgongjiao.entity.LineInfo;
import com.yhtye.shgongjiao.entity.LineStationInfo;
import com.yhtye.shgongjiao.entity.StationInfo;
import com.yhtye.shgongjiao.service.HistoryService;
import com.yhtye.shgongjiao.service.ILineService;
import com.yhtye.shgongjiao.service.LineService;
import com.yhtye.shgongjiao.tools.NetUtil;
import com.yhtye.shgongjiao.tools.ThreadPoolManagerFactory;

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

public class ResultActivity extends BaseActivity implements OnItemClickListener {
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
    private ILineService lineService = new LineService();
    
    private TextView linenameTextView;
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
    
//     加载状态
//    private ProgressDialog progressDialog = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        
        init();
        
        Intent intent = getIntent();
        lineName = intent.getStringExtra("lineName");
        direction = intent.getBooleanExtra("direction", true);
        
        linenameTextView.setText(lineName);
        
        String flag = intent.getStringExtra("flag");
        if (flag != null && flag.equals("history")) {
            HistoryInfo  history = (HistoryInfo) intent.getSerializableExtra("historyInfo");
            if (history != null 
                    && history.getLineInfo() != null 
                    && history.getLineStationInfo() != null) {
                lineInfo = history.getLineInfo();
                lineStation = history.getLineStationInfo();
                showLineInfo();
                lineinfoLayout.setVisibility(View.VISIBLE);
                
                showStations(this);
                lv_cards.setAdapter(adapter);
                lv_cards.setOnItemClickListener(this);
                
                // 自动展开当前站点
                gundong();
                return;
            }
        }
        
        // 启动新线程获取线路信息
//        progressDialog.show();
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
            final ResultActivity  theActivity =  mActivity.get();
            if (theActivity == null) {
                return;
            }
//            
//            if (theActivity.progressDialog != null 
//                    &&  theActivity.progressDialog.isShowing()) {
//                theActivity.progressDialog.dismiss();
//            }
            
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
                
                theActivity.gundong();
                
            } else if (messageFlag == CarsMessage) {
                // 车辆信息
                theActivity.adapter.setCars(theActivity.cars);
                // 即时刷新  
                theActivity.adapter.notifyDataSetChanged(); 
            }
        }
    }
    
    
    private void gundong() {
        // 初始化
        checkListPosition();
          // 尝试滚动
          lv_cards.setSelected(true);
          if (truePosition >= 4 && falsePosition >= 4) { 
              lv_cards.post(new Runnable() {
                  @Override
                  public void run() {
                      setListViewPos(direction ? truePosition : falsePosition);
                  }
              });
          }
          if (truePosition >=0 && falsePosition >= 0) {
              onItemClick(null, null, direction ? truePosition : falsePosition, 0);
          }
    }
    
    /**
     * 定位初始化滚动
     */
    private void checkListPosition() {
        if (lineStation == null 
                || MainActivity.stationNameList == null 
                || MainActivity.stationNameList.size() < 1) {
            return;
        }
        
        for (String name : MainActivity.stationNameList) {
            int i = 0;
            if (falsePosition < 0) {
                for (StationInfo station : lineStation.getFalseDirection()) {
                    if (station.getZdmc().equals(name)) {
                        falsePosition = i;
                        break;
                    }
                    i++;
                }
            }
            
            i = 0;
            if (truePosition < 0) { 
                for (StationInfo station : lineStation.getTrueDirection()) {
                    if (station.getZdmc().equals(name)) {
                        truePosition = i;
                        break;
                    }
                    i++;
                }
            }
        }
    }
    
    /**
     * 交换方向
     * 
     * @param v
     */
    public void switchDirectionClick(View v) {
        if (lineInfo == null || lineStation == null) {
            return;
        }
        direction = !direction;
        showLineInfo();
        showStations(this);
        // 即时刷新  
        adapter.notifyDataSetChanged(); 
        
        // 尝试滚动
        lv_cards.setSelected(true);
        if (truePosition >= 4 && falsePosition >= 4) {
            lv_cards.post(new Runnable() {
                @Override
                public void run() {
                    setListViewPos(direction ? truePosition : falsePosition);
                }
            });
        }
        if (truePosition >=0 && falsePosition >= 0) {
            onItemClick(null, null, direction ? truePosition : falsePosition, 0);
        }
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
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
//        
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
//            progressDialog.show();
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
        
        lineinfoLayout = (RelativeLayout)findViewById(R.id.lineinfo);
        qidianTextView = (TextView)findViewById(R.id.qidian);
        zhongdianTextView = (TextView)findViewById(R.id.zhongdian);
        startimeTextView = (TextView)findViewById(R.id.startime);
        stoptimeTextView = (TextView)findViewById(R.id.stoptime);
        
        // 初始化
        historyService = new HistoryService(ResultActivity.this);
        
//        progressDialog = new ProgressDialog(ResultActivity.this);
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage("数据加载中，请耐心等待……");
    }
    
    /**
     * 展示线路信息
     */
    private void showLineInfo() {
        if (lineInfo == null) {
            return ;
        }
        if (direction) {
            if (lineInfo.getStart_stop().length() > 8) {
                qidianTextView.setText(lineInfo.getStart_stop().subSequence(0, 6));
            } else {
                qidianTextView.setText(lineInfo.getStart_stop());
            }
            if (lineInfo.getEnd_stop().length() > 8) {
                zhongdianTextView.setText(lineInfo.getEnd_stop().subSequence(0, 8));
            } else {
                zhongdianTextView.setText(lineInfo.getEnd_stop());
            }
            startimeTextView.setText(lineInfo.getStart_earlytime());
            stoptimeTextView.setText(lineInfo.getStart_latetime());
        } else {
            if (lineInfo.getEnd_stop().length() > 8) {
                qidianTextView.setText(lineInfo.getEnd_stop().subSequence(0, 8));
            } else {
                qidianTextView.setText(lineInfo.getEnd_stop());
            }
            if (lineInfo.getStart_stop().length() > 8) {
                zhongdianTextView.setText(lineInfo.getStart_stop().subSequence(0, 8));
            } else {
                zhongdianTextView.setText(lineInfo.getStart_stop());
            }
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
        
        
        // 记录搜索历史
        HistoryInfo history = new HistoryInfo(lineName, direction, 
                lineInfo.getStart_stop(), lineInfo.getEnd_stop());
        history.setLineInfo(lineInfo);
        history.setLineStationInfo(lineStation);
        historyService.appendHistory(history);
    }
    
//    @Override
//    protected void onDestroy() {
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
//        super.onDestroy();
//    }
}
