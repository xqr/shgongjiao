package com.yhtye.gongjiaochaxun;

import com.everpod.beijing.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class IconListAdapter extends BaseAdapter {
    /**
     * 图标
     */
    private Integer[] iconImages = new Integer[] {
            R.drawable.icon1, R.drawable.icon2, 
//            R.drawable.button_3, 
            R.drawable.shishi, R.drawable.huancheng, 
//            R.drawable.button_61
    };
    
    /**
     * 图标名称
     */
    private String[] iconNames = new String[] {
            "实时公交", "换乘查询"
//            , "余额查询"
    };
    
    private Context context; 
    private boolean[] isCurrentItems;
    
    public IconListAdapter(Context context, boolean[] isCurrentItems) {
        this.context = context;
        this.isCurrentItems = isCurrentItems;
    }
    
    @Override
    public int getCount() {
        return iconNames.length;
    }

    @Override
    public Object getItem(int position) {
        return isCurrentItems[position];
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
           convertView = mInflater.inflate(R.layout.activity_main_bottom, parent, false);
           
           viewHolder = new ViewHolder();
//           viewHolder.iconIv = (ImageView) convertView.findViewById(R.id.image);
           viewHolder.iconNameTv = (TextView) convertView.findViewById(R.id.bottom_text);
           convertView.setTag(viewHolder);
        }
        
        viewHolder.iconNameTv.setText(iconNames[position]);
        // 设置数据
        if (isCurrentItems[position]) {
            // 点击
            Drawable topDrawable = context.getResources().getDrawable(iconImages[position + iconNames.length]);  
            topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());  
            viewHolder.iconNameTv.setCompoundDrawables(null, topDrawable, null, null);  
            
//            viewHolder.iconIv.setImageResource(iconImages[position + iconNames.length]);
            viewHolder.iconNameTv.setTextColor(context.getResources().getColor(R.color.splashbackground));
        } else {
            Drawable topDrawable = context.getResources().getDrawable(iconImages[position]);  
            topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());  
            viewHolder.iconNameTv.setCompoundDrawables(null, topDrawable, null, null);  
            
//            viewHolder.iconIv.setImageResource(iconImages[position]);
            viewHolder.iconNameTv.setTextColor(context.getResources().getColor(R.color.silver));
        }
        
        return convertView;
    }
    
    private static class ViewHolder
    {
//        ImageView iconIv;
        TextView iconNameTv;
    }
}
