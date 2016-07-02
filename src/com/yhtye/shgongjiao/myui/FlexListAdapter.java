package com.yhtye.shgongjiao.myui;

import java.util.List;

import com.yhtye.shanghaishishigongjiaochaxun.R;
import com.yhtye.shgongjiao.entity.CarInfo;
import com.yhtye.shgongjiao.entity.HistoryInfo;
import com.yhtye.shgongjiao.entity.StationInfo;
import com.yhtye.shgongjiao.tools.RegularUtil;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义listview
 *
 */
public class FlexListAdapter extends BaseAdapter {
    private List<StationInfo> stations;
    private Context context; 
    private boolean[] isCurrentItems;
    private List<CarInfo> cars;
    
    public FlexListAdapter(Context context) {
        this.context = context;
    }
    
    public FlexListAdapter(Context context, List<StationInfo> stations, 
            boolean[] isCurrentItems) {
        this.context = context;
        this.stations = stations;
        this.isCurrentItems = isCurrentItems;
    }
    
    @Override
    public int getCount() {
        if (stations != null) {
            return stations.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (stations == null || stations.size() <= position) {
            return null;
        }
        return stations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        ViewHolder viewHolder = null;
        if (convertView != null){
            viewHolder = (ViewHolder)convertView.getTag();
        } else { 
           LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
           convertView = mInflater.inflate(R.layout.act_stations_list, parent, false);
           
           viewHolder = new ViewHolder();
           viewHolder.ivXialaIco = (ImageView) convertView.findViewById(R.id.iv_xiala_ico);
           viewHolder.ivXiatopIco = (ImageView) convertView.findViewById(R.id.iv_xiatop_ico);
           viewHolder.tvStationName = (TextView) convertView.findViewById(R.id.tv_station_name);
           viewHolder.tvCardName = (TextView) convertView.findViewById(R.id.tv_card_name);
           viewHolder.viewTimeline2 = convertView.findViewById(R.id.view_timeline_2);
           viewHolder.viewTimeline3 = convertView.findViewById(R.id.view_timeline_3);
           viewHolder.weixuanzhongicon = (ImageView) convertView.findViewById(R.id.weixuanzhongicon);
           viewHolder.relative = (LinearLayout) convertView.findViewById(R.id.relative);
           viewHolder.llCards = (RelativeLayout) convertView.findViewById(R.id.ll_cards);  
           
           convertView.setTag(viewHolder);
        }
        // 设置数据
        StationInfo station = stations.get(position);
        setWorkTitleLayout(viewHolder, station, position, isCurrentItems[position], cars);  
        
        return convertView;
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
    public void setWorkTitleLayout(ViewHolder viewHolder,
            final StationInfo station, final int position,
            boolean isCurrentItem, List<CarInfo> cars) {
        int lineindex = position + 1;
        if (station.getZdmc().length() > 8) {
            viewHolder.tvStationName.setText(lineindex + " . " + station.getZdmc().substring(0, 8));
        } else {
            viewHolder.tvStationName.setText(lineindex + " . " + station.getZdmc());
        }

        if (isCurrentItem) {
            viewHolder.tvStationName.setTextColor(context.getResources().getColor(R.color.red));
            viewHolder.weixuanzhongicon.setImageResource(R.drawable.xuanzhongzhuangtai);
            viewHolder.viewTimeline2.setVisibility(View.GONE);
            viewHolder.viewTimeline3.setVisibility(View.VISIBLE);
            viewHolder.relative.setBackgroundResource(R.drawable.yixuanzhongzhandian);
        } else {
            viewHolder.tvStationName.setTextColor(Color.GRAY);
            viewHolder.weixuanzhongicon.setImageResource(R.drawable.weixuanzhong);
            viewHolder.viewTimeline2.setVisibility(View.VISIBLE);
            viewHolder.viewTimeline3.setVisibility(View.GONE);
            viewHolder.relative.setBackgroundResource(R.drawable.zhandianweizhankai);
        }

        if (isCurrentItem && cars != null && !cars.isEmpty()) {
            String text = "";
            for (CarInfo car : cars) {
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
                                text = text
                                        + String.format("%s即将到站",
                                                car.getTerminal());
                                continue;
                            }
                        } else {
                            needTime = totalTime / 60;
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

                text = text
                        + String.format("%s还有%s站，约%s", car.getTerminal(),
                                car.getStopdis(), time);
            }
            viewHolder.tvCardName.setText(text);
        } else {
            viewHolder.tvCardName.setText(R.string.no_cars);
        }

        viewHolder.ivXiatopIco.setVisibility(isCurrentItem ? View.VISIBLE : View.GONE);
        viewHolder.ivXialaIco.setVisibility(isCurrentItem ? View.GONE : View.VISIBLE);

        viewHolder.llCards.setVisibility(isCurrentItem ? View.VISIBLE : View.GONE);
    }

    public List<StationInfo> getStations() {
        return stations;
    }

    public void setStations(List<StationInfo> stations) {
        this.stations = stations;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean[] getIsCurrentItems() {
        return isCurrentItems;
    }

    public void setIsCurrentItems(boolean[] isCurrentItems) {
        this.isCurrentItems = isCurrentItems;
    }
    
    public List<CarInfo> getCars() {
        return cars;
    }

    public void setCars(List<CarInfo> cars) {
        this.cars = cars;
    }
    
    
    private static class ViewHolder
    {
        RelativeLayout llCards; 
        LinearLayout relative;
        
        TextView tvStationName;
        ImageView ivXialaIco;
        ImageView ivXiatopIco;
        TextView tvCardName;
        
        ImageView weixuanzhongicon;
        View viewTimeline2;
        View viewTimeline3;
    }
}
