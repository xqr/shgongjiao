package com.yhtye.gongjiaochaxun;

import java.util.List;

import com.yhtye.wuhan.R;
import com.yhtye.gongjiao.entity.BusInfo;
import com.yhtye.gongjiao.entity.StationInfo;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FlexLinearLayout extends LinearLayout {
    private LayoutInflater mInflater;
    
    private RelativeLayout layout;  
    private RelativeLayout llCards; 
    private LinearLayout relative;
    
    private TextView tvStationName;
    private ImageView ivXialaIco;
    private ImageView ivXiatopIco;
    private TextView tvCardName;
    
    private ImageView weixuanzhongicon;
    private View viewTimeline2;
    private View viewTimeline3;
    
    /** 
     * 创建一个带有伸缩效果的LinearLayout 
     *  
     * @param context 
     *            上下文对象 
     * @param station 
     *            内容详细 
     * @param position 
     *            该列所在列表的位置 
     * @param isCurrentItem 
     *            是否为伸展 
     */  
    public FlexLinearLayout(final Context context, final StationInfo station,  
            final List<BusInfo> busList, final int position, boolean isCurrentItem) {  
        super(context);  
        
        mInflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        layout = (RelativeLayout) mInflater.inflate(R.layout.act_stations_list,  null);  
        init();
        
        this.addView(layout);
        setWorkTitleLayout(station, position, isCurrentItem, busList);
    }
    
    /** 
     * 设置该列的状态及样式 
     *  
     * @param station 
     *            内容详细 
     * @param position 
     *            该列所在列表的位置 
     * @param isCurrentItem 
     *            是否为伸展 
     */  
    public void setWorkTitleLayout(final StationInfo station, final int position,  
            boolean isCurrentItem, List<BusInfo> busList) {  
        init();
        
        int lineindex = position + 1;
        tvStationName.setText(lineindex +" . "+ station.getStopName());
        if (isCurrentItem) {
            tvStationName.setTextColor(getResources().getColor(R.color.red));
            weixuanzhongicon.setImageResource(R.drawable.xuanzhongzhuangtai);
            viewTimeline2.setVisibility(View.GONE);
            viewTimeline3.setVisibility(View.VISIBLE);
            relative.setBackgroundResource(R.drawable.yixuanzhongzhandian);
        } else {
            tvStationName.setTextColor(Color.GRAY);
            weixuanzhongicon.setImageResource(R.drawable.weixuanzhong);
            viewTimeline2.setVisibility(View.VISIBLE);
            viewTimeline3.setVisibility(View.GONE);
            relative.setBackgroundResource(R.drawable.zhandianweizhankai);
        }
        
        String carMessage = getCarMessage(station, busList);
        if (TextUtils.isEmpty(carMessage)) {
            tvCardName.setText(R.string.no_cars);
        } else {
            tvCardName.setText(carMessage);
        }
        
        ivXiatopIco.setVisibility(isCurrentItem ? VISIBLE : GONE);
        ivXialaIco.setVisibility(isCurrentItem ? GONE : VISIBLE);
        
        llCards.setVisibility(isCurrentItem ? VISIBLE : GONE);  
    }
    
    private String getCarMessage(StationInfo station, List<BusInfo> busList) {
        if (station == null || busList == null || busList.size() == 0) {
            return null;
        }
        
        int stopNum = 0;
        for (BusInfo item : busList) {
            if (item.getArrived() == 1 
                    && item.getStopId().equals(station.getStopId())) {
                return "车辆已经到站，请准备上车";
            }
            if (station.getOrder() >= item.getOrder()) {
                stopNum = station.getOrder() - item.getOrder();
                if (item.getArrived() == 0) {
                    stopNum = stopNum + 1;
                }
            } else {
                break;
            }
        }
        if (stopNum < 1) {
            return "等待发车";
        }
        return String.format("最近一辆车距离此还有%s站", stopNum);
    }
    
    private void init() {
        if (llCards == null) {
            llCards = (RelativeLayout) layout.findViewById(R.id.ll_cards);  
        }
        if (tvStationName == null) {
            tvStationName = (TextView) layout.findViewById(R.id.tv_station_name);
        }
        if (ivXialaIco == null) {
            ivXialaIco = (ImageView) layout.findViewById(R.id.iv_xiala_ico);
        }
        if (ivXiatopIco == null) {
            ivXiatopIco = (ImageView) layout.findViewById(R.id.iv_xiatop_ico);
        }
        if (tvCardName == null) {
            tvCardName = (TextView) layout.findViewById(R.id.tv_card_name);
        }
        if (viewTimeline2 == null) {
            viewTimeline2 = layout.findViewById(R.id.view_timeline_2);
        }
        if (viewTimeline3 == null) {
            viewTimeline3 = layout.findViewById(R.id.view_timeline_3);
        }
        if (weixuanzhongicon == null) {
            weixuanzhongicon = (ImageView) layout.findViewById(R.id.weixuanzhongicon);
        }
        if (relative == null) {
            relative = (LinearLayout) layout.findViewById(R.id.relative);
        }
    }
}
