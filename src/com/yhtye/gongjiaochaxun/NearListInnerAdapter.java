package com.yhtye.gongjiaochaxun;

import java.util.List;

import com.yhtye.beijingshishigongjiaochaxun.R;
import com.yhtye.gongjiao.entity.StopStation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NearListInnerAdapter extends BaseAdapter {
    private Context context;
    private List<StopStation> lineList;
    
    public NearListInnerAdapter(Context context, List<StopStation> lineList) {
        this.context = context;
        this.lineList = lineList;
    }
    
    @Override
    public int getCount() {
        if (lineList == null) {
            return 0;
        }
        return lineList.size();
    }

    @Override
    public Object getItem(int position) {
        if (lineList == null || lineList.size() <= position) {
            return null;
        }
        return lineList.get(position);
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
           convertView = mInflater.inflate(R.layout.act_near_inner_list, parent, false);
           
           viewHolder = new ViewHolder();
           viewHolder.nearlinecarTv = (TextView) convertView.findViewById(R.id.nearlinecar);
           viewHolder.nearlinefangxiangTv = (TextView) convertView.findViewById(R.id.nearlinefangxiang);
           viewHolder.nearlinenameTv = (TextView) convertView.findViewById(R.id.nearlinename);
           viewHolder.nearlinetimeTv = (TextView) convertView.findViewById(R.id.nearlinetime);
           
           convertView.setTag(viewHolder);
        }
        
        // TODO 设置内容
        StopStation stopStation = lineList.get(position);
        viewHolder.nearlinenameTv.setText(stopStation.getLine_name());
        viewHolder.nearlinefangxiangTv.setText("开往  " + stopStation.getFangxiang());
        
        return convertView;
    }
    
    private static class ViewHolder
    {
        TextView nearlinenameTv;
        TextView nearlinefangxiangTv;
        TextView nearlinecarTv;
        TextView nearlinetimeTv;
    }
}
