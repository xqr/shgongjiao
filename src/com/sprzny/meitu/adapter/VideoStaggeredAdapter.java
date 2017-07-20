package com.sprzny.meitu.adapter;

import java.util.LinkedList;
import java.util.List;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dodola.model.VideoInfo;
import com.dodowaterfall.Options;
import com.everpod.beijing.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sprzny.meitu.view.HeadListView;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class VideoStaggeredAdapter extends BaseAdapter {
    private Context mContext;
    private LinkedList<VideoInfo> mInfos;
    private HeadListView mListView;
    
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    
    public VideoStaggeredAdapter(Context context, HeadListView xListView) {
        mContext = context;
        mInfos = new LinkedList<VideoInfo>();
        mListView = xListView;
        
        options = Options.getListOptions();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final VideoInfo duitangInfo = mInfos.get(position);

        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
            convertView = layoutInflator.inflate(R.layout.item_video, null);
            
            holder = new ViewHolder();
            holder.player = (JCVideoPlayerStandard) convertView.findViewById(R.id.player_list_video);
            holder.tvVideoUserName = (TextView) convertView.findViewById(R.id.tv_video_userName);
            holder.tvVideoPlayCount = (TextView) convertView.findViewById(R.id.tv_video_play_count);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (TextUtils.isEmpty(duitangInfo.getSource())) {
            holder.tvVideoUserName.setText(R.string.app_name);
        } else {
            holder.tvVideoUserName.setText(duitangInfo.getSource());
        }
        
        if (duitangInfo.getPlaybackCount() >= 10000) {
            int wCount = duitangInfo.getPlaybackCount() / 1000;
            String value = String.valueOf(wCount / 10);
            int kCount = wCount % 10;
            if (kCount != 0) {
                value = value+"."+kCount;
            }
            holder.tvVideoPlayCount.setText(value + "万次播放");
        } else {
            holder.tvVideoPlayCount.setText(duitangInfo.getPlaybackCount() + "次播放");
        }
        holder.player.setUp(duitangInfo.getUrl(), JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, 
                duitangInfo.getTitle(), duitangInfo.getFormatDuration());
        
        imageLoader.displayImage(duitangInfo.getThumbUrl(), holder.player.thumbImageView, options);
        
        return convertView;
    }

    class ViewHolder {
        JCVideoPlayerStandard player;
        TextView tvVideoUserName;
        TextView tvVideoPlayCount;
    }

    @Override
    public int getCount() {
        return mInfos.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mInfos.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public void resetDatas(List<VideoInfo> datas) {
        mInfos = new LinkedList<VideoInfo>();
        mInfos.addAll(datas);
    }
    
    public void addItemLast(List<VideoInfo> datas) {
        mInfos.addAll(datas);
    }

    public void addItemTop(List<VideoInfo> datas) {
        for (VideoInfo info : datas) {
            mInfos.addFirst(info);
        }
    }
}
