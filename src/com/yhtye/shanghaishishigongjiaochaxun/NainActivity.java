package com.yhtye.shanghaishishigongjiaochaxun;

import java.util.List;

import com.baidu.mobads.AdSettings;
import com.baidu.mobads.AdView;
import com.yhtye.shanghaishishigongjiaochaxun.R;
import com.yhtye.shgongjiao.entity.PositionInfo;
import com.yhtye.shgongjiao.myui.IconListAdapter;
import com.yhtye.shgongjiao.service.BaiduApiService;
import com.yhtye.shgongjiao.tools.NetUtil;
import com.yhtye.shgongjiao.tools.ThreadPoolManagerFactory;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 主界面
 */
public class NainActivity extends BaseActivity {

    private ShishichaxunFragment shishichaxunFragment;
    private HuanchengFragment huanchengFragment;
    private YuechaxunFragment yuechaxunFragment;
    
    /** 
     * 用于对Fragment进行管理 
     */  
    private FragmentManager fragmentManager;
    
    // 顶部banner
    private TextView bannerrtitle = null;
    
    // 用户定位信息
    private PositionInfo myPosition = null;
    
    // 广告
    private AdView adView;
    
    // 底部工具栏
    private GridView gview;
    private IconListAdapter simAdapter;
    private boolean[] isCurrentItems;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fragmentManager = getFragmentManager(); 
        if (savedInstanceState != null) {
            // 解决切入后台界面重叠问题
            shishichaxunFragment = (ShishichaxunFragment) fragmentManager.findFragmentByTag("shishichaxun");  
            huanchengFragment = (HuanchengFragment) fragmentManager.findFragmentByTag("huancheng");  
            yuechaxunFragment = (YuechaxunFragment) fragmentManager.findFragmentByTag("yuechaxun");          
        }  
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nain);
        
        // 初始化布局元素  
        initViews();
        
        // 通过经纬度查询附件站点
        ThreadPoolManagerFactory.getInstance().execute(new SearchNearStationsRunable());
        
        // 初始化底部栏
        initIcons();
        
        // 加载广告
        if (NetUtil.checkNet(this)) {
            initAd();
        }
    }
    
    /** 
     * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。 
     */  
    private void initViews() {  
        // 顶部banner
        bannerrtitle = (TextView) findViewById(R.id.bannerrtitle);
    }  
    
    /**
     * 初始化底部icon面板
     */
    private void initIcons() {
        isCurrentItems = new boolean[3];
        isCurrentItems[0] = true;
        isCurrentItems[1] = false;
        isCurrentItems[2] = false;
        gview = (GridView) findViewById(R.id.gview);
        simAdapter = new IconListAdapter(this, isCurrentItems);
        // 配置适配器
        gview.setAdapter(simAdapter);
        gview.setOnItemClickListener(new ItemClickListener());
        
        // 设置默认界面
        setTabSelection(0);
    }
    
    /**
     * 初始化广告
     */
    private void initAd() {        
         // 人群属性
         AdSettings.setKey(new String[] { "baidu", "中国" });
         AdSettings.setCity("上海");
         
         // 创建广告View
         String adPlaceId = "2422749"; // 重要：不填写代码位id不能出广告
         adView = new AdView(this, adPlaceId);
         
         LinearLayout baiduguanggaolayout = (LinearLayout) findViewById(R.id.baiduguanggao);
         baiduguanggaolayout.addView(adView);
    }
    
    class  ItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            for (int i = 0; i < isCurrentItems.length; i++) {
                isCurrentItems[i] = (i == position);
            }
            if (position == 0) {
                bannerrtitle.setText("实时公交");
            } else if (position == 1) {
                bannerrtitle.setText("换乘查询");
            } else {
                bannerrtitle.setText("公交卡余额查询");
            }
            // 界面处理
            setTabSelection(position);
            
            simAdapter.notifyDataSetChanged();
        }
    }
    
    /** 
     * 根据传入的index参数来设置选中的tab页。 
     *  
     * @param index 
     *            每个tab页对应的下标。0表示实时查询，1表示换乘查询，2表示余额查询。 
     */  
    private void setTabSelection(int index) {
        // 开启一个Fragment事务  
        FragmentTransaction transaction = fragmentManager.beginTransaction();  
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况  
        hideFragments(transaction);  
        switch (index) {  
        case 0:
            if (shishichaxunFragment == null) {  
                // 如果ShishichaxunFragment为空，则创建一个并添加到界面上  
                shishichaxunFragment = new ShishichaxunFragment();  
                transaction.add(R.id.main_content, shishichaxunFragment, "shishichaxun");  
            } else {  
                // 如果MessageFragment不为空，则直接将它显示出来  
                transaction.show(shishichaxunFragment);  
            }  
            break;  
        case 1:  
            if (huanchengFragment == null) {  
                // 如果HuanchengFragment为空，则创建一个并添加到界面上  
                huanchengFragment = new HuanchengFragment();  
                transaction.add(R.id.main_content, huanchengFragment, "huancheng");  
            } else {  
                // 如果HuanchengFragment不为空，则直接将它显示出来  
                transaction.show(huanchengFragment);  
            }  
            break;  
        case 2:  
            if (yuechaxunFragment == null) {  
                // 如果YuechaxunFragment为空，则创建一个并添加到界面上  
                yuechaxunFragment = new YuechaxunFragment();  
                transaction.add(R.id.main_content, yuechaxunFragment, "yuechaxun");  
            } else {  
                // 如果NewsFragment不为空，则直接将它显示出来  
                transaction.show(yuechaxunFragment);  
            }  
            break; 
        }
        transaction.commit();  
    }
  
    /** 
     * 将所有的Fragment都置为隐藏状态。 
     *  
     * @param transaction 
     *            用于对Fragment执行操作的事务 
     */  
    private void hideFragments(FragmentTransaction transaction) {  
        if (shishichaxunFragment != null) {  
            transaction.hide(shishichaxunFragment);  
        }  
        if (huanchengFragment != null) {  
            transaction.hide(huanchengFragment);  
        }  
        if (yuechaxunFragment != null) {  
            transaction.hide(yuechaxunFragment);  
        }  
    }  
    
    
    public static  List<String> stationNameList;
    
    /**
     * 通过经纬度查询站点信息
     *
     */
    private class SearchNearStationsRunable  implements Runnable {
        
        @Override
        public void run() {
            try {
                myPosition = NetUtil.checkGps(NainActivity.this);
                if (myPosition == null) {
                    return;
                }
                
                stationNameList = BaiduApiService.getNearStations(myPosition);
                if (stationNameList == null || stationNameList.size() == 0) {
                    return;
                }
            } catch (Exception e) {
                
            }
        }
    }
    
    /**
     * Activity销毁时，销毁adView
     */
    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}