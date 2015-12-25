package com.yhtye.gongjiaochaxun;

import com.everpod.beijing.R;
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
            final int position, boolean isCurrentItem) {  
        super(context);  
        
        mInflater = (LayoutInflater) context  
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        layout = (RelativeLayout) mInflater.inflate(R.layout.act_stations_list,  null);  
        init();
        
        this.addView(layout);
        setWorkTitleLayout(station, position, isCurrentItem);
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
            boolean isCurrentItem) {  
        init();
        
        int lineindex = position + 1;
        tvStationName.setText(lineindex +" . "+ station.getZdmc());
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
        if (TextUtils.isEmpty(station.getCarmessage())) {
            tvCardName.setText(R.string.no_cars);
        } else {
            tvCardName.setText(station.getCarmessage());
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
