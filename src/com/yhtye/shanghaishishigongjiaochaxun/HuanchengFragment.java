package com.yhtye.shanghaishishigongjiaochaxun;

import com.yhtye.shgongjiao.tools.NetUtil;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 换乘查询
 */
public class HuanchengFragment extends Fragment {
    
    // 换乘查询元素
    private EditText qidianEditText = null;
    private EditText zongdianEditText = null;
    
    /**
     * 为Fragment加载布局时调用
     */
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {
        View settingLayout = inflater.inflate(R.layout.activity_main_huancheng, container, false);  
        return settingLayout;  
    }
    
    /**
     * 当Activity中的onCreate方法执行完后调用
     */
    @Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);
        
        initHuansheng();
    }
    
    /**
     * 初始化换乘界面元素
     */
    private void initHuansheng() {
        if (qidianEditText == null) {
            qidianEditText = (EditText) getActivity().findViewById(R.id.qiidianEdit);
        }
        if (zongdianEditText == null) {
            zongdianEditText = (EditText) getActivity().findViewById(R.id.zongdianEdit);
        }
        
        // 切换起点和终点
        ImageView switchqizongButton = (ImageView) getActivity().findViewById(R.id.switchqizongButton);
        switchqizongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchStationsClick(v);
            }
        });
        
        // 线路查询
        Button searchRoutesButton = (Button) getActivity().findViewById(R.id.searchRoutes);
        searchRoutesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchRoutesClick(v);
            }
        });
    }
    
    /**
     * 换乘查询—交换起点和终点
     * 
     * @param v
     */
    public void switchStationsClick(View v) {
        String qidianText = qidianEditText.getText().toString();
        String zongdianText = zongdianEditText.getText().toString();
        qidianEditText.setText(zongdianText);
        zongdianEditText.setText(qidianText);
    }
    
    /**
     * 换乘查询—换乘路线查询
     * 
     * @param v
     */
    public void searchRoutesClick(View v) {
        Activity nowActivity = getActivity();
        // 检查网络
        if (!NetUtil.checkNet(nowActivity)) {
            Toast.makeText(nowActivity, R.string.network_tip, Toast.LENGTH_LONG).show();
            return;
        }
        
        String qidianText = qidianEditText.getText().toString();
        String zongdianText = zongdianEditText.getText().toString();
        if (TextUtils.isEmpty(qidianText)) {
            Toast.makeText(nowActivity, "请输入要查询的线路起点", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(zongdianText)) {
            Toast.makeText(nowActivity, "请输入要查询的线路终点", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 切换Activity
        Intent intent=new Intent();
        intent.setClass(nowActivity, SchemeActivity.class);  
        intent.putExtra("qidian", qidianText);
        intent.putExtra("zongdian", zongdianText);
        startActivity(intent);
    }
}
