package com.yhtye.shanghaishishigongjiaochaxun;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.umeng.analytics.MobclickAgent;
import com.yhtye.shgongjiao.entity.HistoryInfo;
import com.yhtye.shgongjiao.myui.HistoryListAdapter;
import com.yhtye.shgongjiao.service.HistoryService;
import com.yhtye.shgongjiao.tools.NetUtil;
import com.yhtye.shgongjiao.tools.RegularUtil;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 实时查询
 */
public class ShishichaxunFragment extends Fragment implements OnItemClickListener {
    
    private EditText numberoneEditText = null;
    
    // List 历史记录
    private HistoryListAdapter adapter;
    private ListView listHistoryView = null;
    private HistoryService historyService = null;
    
    /**
     * 为Fragment加载布局时调用
     */
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {  
        View settingLayout = inflater.inflate(R.layout.activity_main_shishichaxun, container, false);  
        return settingLayout;  
    }
    
    /**
     * 当Activity中的onCreate方法执行完后调用
     */
    @Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);  
        
        // 初始化界面
        initBar();
        
        // 查询历史记录
        showHistory();
    }
    
    /**
     * 初始化按钮和界面元素
     */
    private void initBar() {
        // 获取输入信息
        numberoneEditText = (EditText)getActivity().findViewById(R.id.numberone);
        
        numberoneEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchLineClick(v);
                }
                return true;
            }
        });
        
        Button searchLineButton = (Button)getActivity().findViewById(R.id.searchLine);
        searchLineButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                searchLineClick(v);
            }
        });
        
        listHistoryView = (ListView) getActivity().findViewById(R.id.list_history_line);
        historyService = new HistoryService(getActivity());
    }
    
    /**
     * 实时公交—线路查询按钮点击事件响应
     * 
     * @param v
     */
    public void searchLineClick(View v) {
        Activity nowActivity = getActivity();
        // 检查网络
        if (!NetUtil.checkNet(nowActivity)) {
            Toast.makeText(nowActivity, R.string.network_tip, Toast.LENGTH_LONG).show();
            return;
        }
        
        String lineName = numberoneEditText.getText().toString();
        if (TextUtils.isEmpty(lineName)) {
            Toast.makeText(nowActivity, "请输入要查询的公交路线", Toast.LENGTH_SHORT).show();
            return;
        }
        // 校验输入的完整性
        if (RegularUtil.isNumeric(lineName)) {
            lineName = lineName + "路";
        }
        
        // 统计
        Map<String,String> m = new HashMap<String,String>();
        m.put("lineName", lineName);
        MobclickAgent.onEventValue(nowActivity, "searchline", m, Integer.MAX_VALUE);
        
        // 切换Activity
        Intent intent=new Intent();
        intent.setClass(nowActivity, ResultActivity.class);  
        intent.putExtra("lineName", lineName);
        startActivity(intent);
    }
    
    private List<HistoryInfo> historyList = null;
    
    private void showHistory() {
        historyList = historyService.getHistory();
        if (historyList == null || historyList.size() == 0) {
            return;
        }
        if (adapter == null) {
            adapter = new HistoryListAdapter(getActivity(), historyList);
        }
        listHistoryView.setAdapter(adapter);
        listHistoryView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        // 点击历史记录 
        HistoryInfo historyInfo = historyList.get(position);
        if (historyInfo == null) {
            return;
        }
        
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("historyInfo", historyInfo);
        bundle.putString("flag", "history");
        bundle.putString("lineName", historyInfo.getLineName());
        bundle.putBoolean("direction", historyInfo.isDirection());
        intent.putExtras(bundle);
        
        intent.setClass(getActivity(), ResultActivity.class);  
        
        Map<String,String> m = new HashMap<String,String>();
        m.put("lineName", historyInfo.getLineName());
        m.put("direction", historyInfo.isDirection() +"");
        MobclickAgent.onEventValue(getActivity(), "historyclick", m, Integer.MAX_VALUE);
        
        startActivity(intent);
    }
}
