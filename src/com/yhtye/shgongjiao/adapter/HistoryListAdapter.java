package com.yhtye.shgongjiao.adapter;

import java.util.List;

import com.everpod.shanghai.R;
import com.yhtye.shgongjiao.entity.HistoryInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HistoryListAdapter extends BaseAdapter {
    private Context context; 
    private List<HistoryInfo> list;
    
    public HistoryListAdapter(Context context, List<HistoryInfo> list) {
        this.context = context;
        this.list = list;
    }
    
    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list == null || list.size() <= position) {
            return null;
        }
        return list.get(position);
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
           convertView = mInflater.inflate(R.layout.act_history_line_list, parent, false);
           
           viewHolder = new ViewHolder();
           viewHolder.historylinenameTv = (TextView) convertView.findViewById(R.id.historylinename);
           
           convertView.setTag(viewHolder);
        }
        // 设置数据
        HistoryInfo history = list.get(position);
        if (history != null) {
            String text = null;
            if (history.isDirection()) {
                text = String.format("%s ( %s > %s )", history.getLineName(), 
                        history.getStartStop(), history.getEndStop());
            } else {
                text = String.format("%s ( %s > %s )", history.getLineName(), 
                        history.getEndStop(), history.getStartStop());
            }
            viewHolder.historylinenameTv.setText(text);
        }
        
        return convertView;
    }
    
    private static class ViewHolder
    {
        TextView historylinenameTv;
    }
}
