package com.yhtye.gongjiaochaxun;

import java.util.ArrayList;
import java.util.List;

import com.dodola.model.CategoryInfo;
import com.dodowaterfall.BaseTools;
import com.everpod.beijing.R;
import com.sprzny.meitu.adapter.NewsFragmentPagerAdapter;
import com.sprzny.meitu.fragment.VideosFragment;
import com.sprzny.meitu.service.BaiduVideoService;
import com.sprzny.meitu.view.ColumnHorizontalScrollView;
import com.umeng.analytics.MobclickAgent;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 头条号首页
 */
public class ContentActivity extends AppCompatActivity {
    /** 左阴影部分*/
    public ImageView shade_left;
    /** 右阴影部分 */
    public ImageView shade_right;
    /** 屏幕宽度 */
    private int mScreenWidth = 0;
    /** Item宽度 */
    private int mItemWidth = 0;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    
    /** 自定义HorizontalScrollView */
    private ColumnHorizontalScrollView mColumnHorizontalScrollView;
    private LinearLayout mRadioGroup_content;
    private RelativeLayout rl_column;
    private LinearLayout ll_more_columns;
    private ViewPager mViewPager;
    
    /** 当前选中的栏目*/
    private int columnSelectIndex = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        
        // 禁止默认的页面统计方式
        MobclickAgent.openActivityDurationTrack(false);
        
        mScreenWidth = BaseTools.getWindowsWidth(this);
        mItemWidth = mScreenWidth / 8;// 一个Item宽度为屏幕的1/7
        initView();
    }
    
    /** 初始化layout控件*/
    private void initView() {
        mColumnHorizontalScrollView =  (ColumnHorizontalScrollView)findViewById(R.id.mColumnHorizontalScrollView);
        mRadioGroup_content = (LinearLayout) findViewById(R.id.mRadioGroup_content);
        rl_column = (RelativeLayout) findViewById(R.id.rl_column);
        ll_more_columns = (LinearLayout) findViewById(R.id.ll_more_columns);
        
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        shade_left = (ImageView) findViewById(R.id.shade_left);
        shade_right = (ImageView) findViewById(R.id.shade_right);
        
        setChangelView();
    }
    
    /** 
     *  当栏目项发生变化时候调用
     * */
    private void setChangelView() {
        new CategoryTask().execute();
    }
    
    /**
     * 加载分类类别
     */
    private class CategoryTask extends AsyncTask<String, Integer, List<CategoryInfo>> {
        
        @Override
        protected List<CategoryInfo> doInBackground(String... params) {
            return BaiduVideoService.createCategorys();
        }
        
        @Override
        protected void onPostExecute(List<CategoryInfo> result) {
            initTabColumn(result);
            initFragment(result);
        }
    }
    
    /** 
     *  初始化Column栏目项
     * */
    private void initTabColumn(final List<CategoryInfo> result) {
        mRadioGroup_content.removeAllViews();
        int count =  result.size();
        mColumnHorizontalScrollView.setParam(this, mScreenWidth, mRadioGroup_content, 
                shade_left, shade_right, ll_more_columns, rl_column);
        for(int i = 0; i< count; i++){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth , LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = 5;
            TextView columnTextView = new TextView(this);
            columnTextView.setTextAppearance(this, R.style.top_category_scroll_view_item_text);
            columnTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
            columnTextView.setGravity(Gravity.CENTER);
            columnTextView.setPadding(5, 5, 5, 5);
            columnTextView.setId(i);
            columnTextView.setText(result.get(i).getCategoryTitle());
            columnTextView.setTextColor(getResources().getColorStateList(R.color.top_category_scroll_text_color_day));
            if(columnSelectIndex == i){
                columnTextView.setSelected(true);
            }
            columnTextView.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                      for(int i = 0;i < mRadioGroup_content.getChildCount();i++){
                          View localView = mRadioGroup_content.getChildAt(i);
                          if (localView != v)
                              localView.setSelected(false);
                          else{
                              localView.setSelected(true);
                              mViewPager.setCurrentItem(i);
                              selectTab(i);
                          }
                      }
                }
            });
            mRadioGroup_content.addView(columnTextView, i ,params);
        }
    }
    
    /** 
     *  选择的Column里面的Tab
     * */
    private void selectTab(int tab_postion) {
        columnSelectIndex = tab_postion;
        for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
            View checkView = mRadioGroup_content.getChildAt(tab_postion);
            int k = checkView.getMeasuredWidth();
            int l = checkView.getLeft();
            int i2 = l + k / 2 - mScreenWidth / 2;
            mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
        }
        //判断是否选中
        for (int j = 0; j <  mRadioGroup_content.getChildCount(); j++) {
            View checkView = mRadioGroup_content.getChildAt(j);
            boolean ischeck;
            if (j == tab_postion) {
                ischeck = true;
            } else {
                ischeck = false;
            }
            checkView.setSelected(ischeck);
        }
    }
    
    /** 
     *  初始化Fragment
     * */
    private void initFragment(final List<CategoryInfo> result) {
        fragments.clear();//清空
        int count =  result.size();
        for(int i = 0; i< count;i++){
            Bundle data = new Bundle();
            data.putString("text", result.get(i).getCategoryTitle());
            data.putString("id", String.valueOf(result.get(i).getCategoryId()));
            VideosFragment newfragment = new VideosFragment();
            newfragment.setArguments(data);
            fragments.add(newfragment);
        }
        NewsFragmentPagerAdapter mAdapetr = new NewsFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mAdapetr);
        mViewPager.setOnPageChangeListener(pageListener);
    }
    /** 
     *  ViewPager切换监听方法
     * */
    public OnPageChangeListener pageListener= new OnPageChangeListener(){

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            mViewPager.setCurrentItem(position);
            selectTab(position);
        }
    };
    
    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
     }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
        MobclickAgent.onPause(this);
    }
}// end of class
