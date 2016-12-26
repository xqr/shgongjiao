package com.yhtye.shgongjiao.adapter;

import java.util.List;
import com.everpod.shanghai.R;
import com.yhtye.shgongjiao.entity.CarInfo;
import com.yhtye.shgongjiao.entity.StationInfo;
import com.yhtye.shgongjiao.tools.RegularUtil;

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
            final int position, boolean isCurrentItem, List<CarInfo> cars) {  
        super(context);  
        
        mInflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        layout = (RelativeLayout) mInflater.inflate(R.layout.act_stations_list,  null);  
        init();
        
        this.addView(layout);
        setWorkTitleLayout(station, position, isCurrentItem, cars);  
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
            boolean isCurrentItem, List<CarInfo> cars) {  
        init();
        
        int lineindex = position + 1;
        if (station.getZdmc().length() > 10) {
            tvStationName.setText(lineindex + " . " + station.getZdmc().substring(0, 10));
        } else {
            tvStationName.setText(lineindex + " . " + station.getZdmc());
        }
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
        
        if (isCurrentItem && cars != null && !cars.isEmpty()) {
            String text = "";
            for(CarInfo car : cars) {
                if (!TextUtils.isEmpty(text)) {
                    text = text + "\n";
                }
                String time = car.getTime();
                if (RegularUtil.isNumeric(time)) {
                    try {
                        int totalTime = Integer.parseInt(time);
                        int needTime = 1;
                        if (totalTime < 60) {
                            // 小于1分钟
                            if (car.getStopdis() <= 0 || totalTime < 30) {
                                text = text + String.format("%s即将到站", car.getTerminal());
                                continue;
                            }
                        } else {
                            needTime =  totalTime / 60;
                            if (totalTime % 60 != 0) {
                                needTime = needTime + 1;
                            }
                        }
                        time = needTime + "分钟";
                    } catch (Exception e) {
                    }
                } else {
                    // 处理25分钟
                    time = (car.getStopdis() * 2) + "分钟";
                }
                
                text = text + String.format("%s还有%s站，约%s", car.getTerminal(), 
                        car.getStopdis(), time);
            }
            tvCardName.setText(text);
        } else {
            tvCardName.setText(R.string.no_cars);
        }
        
        ivXiatopIco.setVisibility(isCurrentItem ? VISIBLE : GONE);
        ivXialaIco.setVisibility(isCurrentItem ? GONE : VISIBLE);
        
        llCards.setVisibility(isCurrentItem ? VISIBLE : GONE);  
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
