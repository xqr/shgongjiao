package com.yhtye.shanghaishishigongjiaochaxun;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yhtye.shgongjiao.tools.NetUtil;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 余额查询
 */
public class YuechaxunFragment extends Fragment {
    
    private EditText gongjiaokahaonumberoneEditText = null;
    
    /**
     * 为Fragment加载布局时调用
     */
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {
        View settingLayout = inflater.inflate(R.layout.activity_main_yuechaxun, container, false);  
        return settingLayout;  
    }
    
    /**
     * 当Activity中的onCreate方法执行完后调用
     */
    @Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);
        
        // 初始化页面元素
        if (gongjiaokahaonumberoneEditText == null) {
            gongjiaokahaonumberoneEditText = (EditText) getActivity().findViewById(R.id.gongjiaokahaonumberone);
            // 调用数字键盘
            gongjiaokahaonumberoneEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            // 软键盘设置
            gongjiaokahaonumberoneEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                
                @Override
                public boolean onEditorAction(TextView v, int actionId,
                        KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {  
                        searchyueClick(v);
                    }
                    return true;
                }
            });
        }
        
        Button searchyueButton = (Button) getActivity().findViewById(R.id.searchyue);
        searchyueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchyueClick(v);
            }
        });
    }
    
    /**
     * 余额查询—余额查询
     * 
     * @param v
     */
    public void searchyueClick(View v) {
        Activity nowActivity = getActivity();
        // 检查网络
        if (!NetUtil.checkNet(nowActivity)) {
            Toast.makeText(nowActivity, R.string.network_tip, Toast.LENGTH_LONG).show();
            return;
        }
        
        String gongjiaokahao = gongjiaokahaonumberoneEditText.getText().toString();
        if (TextUtils.isEmpty(gongjiaokahao)) {
            Toast.makeText(nowActivity, "请输入要查询的公交卡号", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 上海公交卡号全部为数字
        String regEx="[^0-9]";   
        Pattern p = Pattern.compile(regEx);   
        Matcher m = p.matcher(gongjiaokahao);   
        gongjiaokahao = m.replaceAll("").trim();
        
        // 切换Activity
        Intent intent=new Intent();
        intent.setClass(nowActivity, YueActivity.class);  
        intent.putExtra("carNumber", gongjiaokahao);
        startActivity(intent);
    }
}
