package com.yhtye.shanghaishishigongjiaochaxun;

import java.util.List;
import java.util.Map;

import com.instant.bus.R;
import com.yhtye.shgongjiao.entity.StopStation;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NearListParentsAdapter extends BaseAdapter {
    private Context context; 
    private boolean[] isCurrentItems;
    private List<String> stationNameList;
    private Map<String, List<StopStation>> stationMap;
    
    public NearListParentsAdapter(Context context, boolean[] isCurrentItems,
            List<String> stationNameList,
            Map<String, List<StopStation>> stationMap) {
        this.context = context;
        this.isCurrentItems = isCurrentItems;
        this.stationNameList = stationNameList;
        this.stationMap = stationMap;
    }
    
    @Override
    public int getCount() {
        if (stationNameList == null) {
            return 0;
        }
        return stationNameList.size();
    }

    @Override
    public Object getItem(int position) {
        if (stationNameList == null) {
            return null;
        }
        return stationNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;  
        if (null == convertView) {  
            convertView = LayoutInflater.from(context).inflate(R.layout.act_near_parent_list, parent, false);
            
            viewHolder = new ViewHolder();
            viewHolder.nearstationTv = (TextView) convertView.findViewById(R.id.nearstation);
            viewHolder.listInner = (ListView) convertView.findViewById(R.id.near_list_inner);
            viewHolder.divNearParentBar = convertView.findViewById(R.id.div_near_parent_bar);
            
            convertView.setTag(viewHolder);
        } else {  
            viewHolder = (ViewHolder) convertView.getTag();  
        }  
        
        // TODO 设置数据
        String name = stationNameList.get(position);
        viewHolder.nearstationTv.setText(name);
        if (isCurrentItems[position]) {
            NearListInnerAdapter adapter = new NearListInnerAdapter(context, stationMap.get(name));
            viewHolder.listInner.setAdapter(adapter);
            //根据innerlistview的高度机损parentlistview item的高度
            setListViewHeightBasedOnChildren(viewHolder.listInner);
        }
        
        viewHolder.divNearParentBar.setVisibility(isCurrentItems[position] ? View.VISIBLE : View.GONE);
        viewHolder.listInner.setVisibility(isCurrentItems[position] ? View.VISIBLE : View.GONE);
        
        return convertView;
    }
    
    private static class ViewHolder
    {
        TextView nearstationTv;
        ListView listInner;
        View divNearParentBar;
    }
    
    /**
     * @param listview
     *            此方法是本次listview嵌套listview的核心方法：计算parentlistview item的高度。
     *            如果不使用此方法，无论innerlistview有多少个item，则只会显示一个item。
     **/
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
