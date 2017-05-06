package com.yhtye.shgongjiao.adapter;

import java.util.List;

import com.instantbus.sh.R;
import com.yhtye.shgongjiao.entity.RoutesScheme;
import com.yhtye.shgongjiao.entity.SchemeSteps;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class SchemeListAdapter extends BaseAdapter {
    private Context context; 
    private boolean[] isCurrentItems;
    private int[] isOpendItems;
    private List<RoutesScheme> routesSchemeList;
    private String qidian;
    private String zongdian;
    
    public SchemeListAdapter(Context context, List<RoutesScheme> routesSchemeList, 
            boolean[] isCurrentItems, int[] isOpendItems, String qidian, String zongdian) {
        this.context = context;
        this.routesSchemeList = routesSchemeList;
        this.isCurrentItems = isCurrentItems;
        this.isOpendItems = isOpendItems;
        this.qidian = qidian;
        this.zongdian = zongdian;
    }
    
    @Override
    public int getCount() {
        if (routesSchemeList == null) {
            return 0;
        }
        return routesSchemeList.size();
    }

    @Override
    public Object getItem(int position) {
        if (routesSchemeList == null 
                || routesSchemeList.size() <= position) {
            return null;
        }
        return routesSchemeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SchemeLinearLayout view = null;  
        if (null == convertView) {  
            view = new SchemeLinearLayout(context);
        } else {  
            view = (SchemeLinearLayout) convertView;  
        }  
        view.setWorkTitleLayout(routesSchemeList.get(position), 
                position, isCurrentItems[position], isOpendItems[position]); 
        return view;
    }
    
    public class SchemeLinearLayout extends LinearLayout {
        
        private LayoutInflater mInflater;  
        // 列表的布局
        private LinearLayout layout;
        // 列表展开的显示的内容
        private TableLayout ll_steps;
        
        private TextView tv_scheme_name;
        private View div_scheme_bar;
        
        public SchemeLinearLayout(final Context context) {
            super(context);
            
            mInflater = (LayoutInflater) context  
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
            
            layout = (LinearLayout) mInflater.inflate(R.layout.act_scheme_list,  null);  
            
            init();
            
            this.addView(layout);
        }
        
        public void setWorkTitleLayout(final RoutesScheme routesScheme, final int position,  
                boolean isCurrentItem, int isOpendItem) {
            // 路线信息
            String schemeName = null;
            for (String carName : routesScheme.getVehicleNames()) {
                if (TextUtils.isEmpty(schemeName)) {
                    schemeName = carName;
                } else {
                    schemeName = schemeName + "  > " + carName;
                }
            }
            tv_scheme_name.setText(schemeName);  
            // 换乘详细信息
            if (isCurrentItem && isOpendItem == 1) {
                ll_steps.removeAllViews();
                ll_steps.addView(getTableRow(R.drawable.start, qidian));
                for(SchemeSteps steps : routesScheme.getSteps()) {
                    if (steps.getType() == 5) {
                        ll_steps.addView(getTableRow(R.drawable.xuxian, steps.getStepInstruction()));
                    } else if (steps.getType() == 3) {
                        ll_steps.addView(getTableRow(R.drawable.zhantaitubiao, 
                                steps.getVehicleStartName() +" [上车] "));
                        ll_steps.addView(getTableRow(R.drawable.shixian, steps.getVehicleName() 
                                +" (" + steps.getVehicleStopNum() +"站)"));
                        ll_steps.addView(getTableRow(R.drawable.zhantaitubiao, 
                                steps.getVehicleEndName() + " [下车] "));
                    }
                }
                ll_steps.addView(getTableRow(R.drawable.end, zongdian));
            }
            
            div_scheme_bar.setVisibility(isCurrentItem ? VISIBLE : GONE);
            ll_steps.setVisibility(isCurrentItem ? VISIBLE : GONE);  
        }
        
        /**
         * 返回一行信息
         * 
         * @param imageResource
         * @param text
         * @return
         */
        private View getTableRow(int imageResource, String text) {
            TableRow tableRow = new TableRow(context);

            ImageView imageView = new ImageView(context);
            imageView.setImageResource(imageResource);
            imageView.setPadding(0, 0, 18, 0);
            
            TextView textView = new TextView(context);
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setText(text);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            // 控件居中
            TableRow.LayoutParams tparam = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
           tparam.gravity = Gravity.CENTER_VERTICAL; 
           textView.setLayoutParams(tparam);
            
            tableRow.addView(imageView);
            tableRow.addView(textView);

            return tableRow;
        }
        
        /**
         * 初始化控件
         */
        private void init() {
            tv_scheme_name = (TextView) layout.findViewById(R.id.tv_scheme_name);
            ll_steps = (TableLayout) layout.findViewById(R.id.ll_steps);
            div_scheme_bar = layout.findViewById(R.id.div_scheme_bar); 
        }
    }
}


