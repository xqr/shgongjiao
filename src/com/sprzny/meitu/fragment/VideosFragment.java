package com.sprzny.meitu.fragment;

import java.util.List;
import com.dodola.model.VideoInfo;
import com.dodowaterfall.Helper;
import com.everpod.beijing.R;
import com.huewu.pla.lib.internal.PLA_AbsListView;

import com.sprzny.meitu.adapter.VideoStaggeredAdapter;
import com.sprzny.meitu.service.BaiduVideoService;
import com.sprzny.meitu.view.HeadListView;
import com.sprzny.meitu.view.HeadListView.IXListViewListener;
import com.sprzny.meitu.view.HeadListView.OnXScrollListener;
import com.umeng.analytics.MobclickAgent;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VideosFragment extends Fragment  implements IXListViewListener, OnXScrollListener {
	private final static String TAG = "NewsFragment";
	private Activity activity;
	
	private HeadListView mListView = null;
	private VideoStaggeredAdapter mAdapter = null;
	
	// 当前频道title和频道ID
	private String text;
    private String channel_id;
    private int currentPage = 0;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    // 参数传递
		Bundle args = getArguments();
		text = args != null ? args.getString("text") : "";
		channel_id = args != null ? args.getString("id") : "1033";
		
		super.onCreate(savedInstanceState);
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.news_fragment, null);
        
        mListView = (HeadListView) view.findViewById(R.id.mListView);
        mListView.setPullLoadEnable(true);
        mListView.setXListViewListener(this);
        
        if (mAdapter == null) {
            mAdapter = new VideoStaggeredAdapter(activity, mListView);
        }
        mListView.setAdapter(mAdapter);
        
        // 监听滑动
        mListView.setMScrollListener(this);
        
        // 加载1页
        AddItemToContainer(currentPage, 2);
        
        return view;
    }
	
	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		super.onAttach(activity);
	}
	
	/** 此方法意思为fragment是否可见 ,可见时候加载数据 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			//fragment可见时加载数据
		    
//			if(newsList !=null && newsList.size() !=0){
//				handler.obtainMessage(SET_NEWSLIST).sendToTarget();
//			}else{
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						try {
//							Thread.sleep(2);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						handler.obtainMessage(SET_NEWSLIST).sendToTarget();
//					}
//				}).start();
//			}
		}else{
			//fragment不可见时不执行操作
		}
		super.setUserVisibleHint(isVisibleToUser);
	}
	
	private ContentTask task = new ContentTask(activity, 2);

    private class ContentTask extends AsyncTask<String, Integer, List<VideoInfo>> {

        private Context mContext;
        private int mType = 1;
        
        /**
         * 
         * @param context
         * @param type  1为下拉刷新 2为加载更多
         */
        public ContentTask(Context context, int type) {
            super();
            mContext = context;
            mType = type;
        }

        /**
         *  参数1：页数
         */
        @Override
        protected List<VideoInfo> doInBackground(String... params) {
            try {
                if (!Helper.checkConnection(mContext)) {
                    return null;
                }
                
                int pageIndex = Integer.parseInt(params[0]);
                return BaiduVideoService.parseVideoList(channel_id, pageIndex, 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<VideoInfo> result) {
            if (result == null || result.isEmpty()) {
                return;
            }
            if (mType == 1) {
                mAdapter.addItemTop(result);
                mAdapter.notifyDataSetChanged();
                mListView.stopRefresh();
            } else if (mType == 2) {
                mListView.stopLoadMore();
                mAdapter.addItemLast(result);
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }

    /**
     * 添加内容
     * 
     * @param pageindex
     * @param type
     *            1为下拉刷新 2为加载更多
     */
    private void AddItemToContainer(int pageindex, int type) {
        if (task.getStatus() != Status.RUNNING) {
            ContentTask task = new ContentTask(activity, type);
            // 加载
            task.execute(String.valueOf(pageindex));
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("video_" + text); //统计页面，"MainScreen"为页面名称，可自定义
    }
    
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("video_" + "text"); 
    }
    	
	/* 摧毁视图 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	/* 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

    @Override
    public void onRefresh() {
        AddItemToContainer(++currentPage, 1);
    }

    @Override
    public void onLoadMore() {
        AddItemToContainer(++currentPage, 2);
    }

    @Override
    public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {
        switch (scrollState) {
        case PLA_AbsListView.OnScrollListener.SCROLL_STATE_FLING: // 手指离开屏幕后，惯性滑动
            break;

        case PLA_AbsListView.OnScrollListener.SCROLL_STATE_IDLE: //  滑动后静止
            //滑动停止自动播放视频
            autoPlayVideo(view);
            break;

        case PLA_AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: // 手指在屏幕上滑动
            break;
        }
    }

    // firstVisibleItem 当前能看见的第一个列表项ID（从0开始）
    // visibleItemCount：当前能看见的列表项个数（小半个也算）  
    // totalItemCount：列表项共数  
    @Override
    public void onScroll(PLA_AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        if (firstVisible == firstVisibleItem) {
            return;
        }

        firstVisible = firstVisibleItem;
        visibleCount = visibleItemCount;
    }

    @Override
    public void onXScrolling(View view) {
        // TODO 
    }
    
    private JCVideoPlayerStandard currPlayer;
    private int firstVisible;//当前第一个可见的item
    private int visibleCount;//当前可见的item个数

    /**
     * 滑动停止自动播放视频
     */
    private void autoPlayVideo(PLA_AbsListView view) {
        for (int i = 0; i < visibleCount; i++) {
            if (view != null
                    && view.getChildAt(i) != null
                    && view.getChildAt(i).findViewById(R.id.player_list_video) != null) {
                currPlayer = (JCVideoPlayerStandard) view.getChildAt(i)
                        .findViewById(R.id.player_list_video);
                
                if (currPlayer.currentState == JCVideoPlayer.CURRENT_STATE_PLAYING) {
                    return;
                }
            }
        }
        // 释放其他视频资源
        JCVideoPlayer.releaseAllVideos();
    }
}
