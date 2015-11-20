package com.yhtye.shanghaishishigongjiaochaxun;

import com.umeng.analytics.MobclickAgent;
import com.yhtye.shgongjiao.tools.NetUtil;
import com.yhtye.shgongjiao.tools.RegularUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    private EditText numberoneEditText = null;
    
    private Intent intent=new Intent(); 
    
    private Button shishichaxunButton = null;
    private Button huanshengchaxunButton = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        init();
    }
    
    /**
     * 初始化按钮和界面元素
     */
    private void init() {
        shishichaxunButton = (Button) findViewById(R.id.shishichaxun);
        huanshengchaxunButton = (Button) findViewById(R.id.huanshengchaxun);
        // 获取输入信息
        numberoneEditText = (EditText)findViewById(R.id.numberone);
        
        // 初始化
        shishichaxunButton.setSelected(true);
        huanshengchaxunButton.setSelected(false);
    }
    
    /**
     * 实时查询按钮
     * 
     * @param v
     */
    public void shishichaxunClick(View v) {
        // 按钮变化
        shishichaxunButton.setTextColor(getResources().getColor(R.color.blue));
        shishichaxunButton.setSelected(true);
        huanshengchaxunButton.setTextColor(getResources().getColor(R.color.white));
        huanshengchaxunButton.setSelected(false);
    }
    
    /**
     * 换乘查询按钮
     * 
     * @param v
     */
    public void huanshengchaxunClick(View v) {
        // 按钮变化
        shishichaxunButton.setTextColor(getResources().getColor(R.color.white));
        shishichaxunButton.setSelected(false);
        huanshengchaxunButton.setTextColor(getResources().getColor(R.color.blue));
        huanshengchaxunButton.setSelected(true);
    }
    
    /**
     * 查询按钮点击事件响应
     * 
     * @param v
     */
    public void searchLineClick(View v) {
        // 检查网络
        if (!NetUtil.checkNet(MainActivity.this)) {
            Toast.makeText(MainActivity.this, R.string.network_tip, Toast.LENGTH_LONG).show();
            return;
        }
        String lineName = numberoneEditText.getText().toString();
        if (TextUtils.isEmpty(lineName)) {
            Toast.makeText(MainActivity.this, "请输入要查询的公交路线", Toast.LENGTH_SHORT).show();
            return;
        }
        // 校验输入的完整性
        if (RegularUtil.isNumeric(lineName)) {
            lineName = lineName + "路";
        }
        
        // 切换Activity
        intent.setClass(MainActivity.this, ResultActivity.class);  
        intent.putExtra("lineName", lineName);
        startActivity(intent);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
