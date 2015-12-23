package com.yhtye.gongjiaochaxun;

import java.util.List;

import com.yhtye.gongjiao.entity.StationInfo;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 自定义listview
 *
 */
public class FlexListAdapter extends BaseAdapter {
    private List<StationInfo> stations;
    private Context context; 
    private boolean[] isCurrentItems;
    
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
        FlexLinearLayout view = null;  
        StationInfo station = stations.get(position);
        if (null == convertView) {  
            view = new FlexLinearLayout(context, station,  
                    position, false);  
        } else {
            view = (FlexLinearLayout) convertView;  
            view.setWorkTitleLayout(station, position, isCurrentItems[position]);  
        }  
        return view;  
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
}
