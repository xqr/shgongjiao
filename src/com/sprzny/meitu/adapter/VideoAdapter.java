package com.sprzny.meitu.adapter;

import java.util.ArrayList;
import java.util.List;

import com.dodowaterfall.Options;
import com.everpod.beijing.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class VideoAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mData;
    
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    
    public VideoAdapter(Context context, List<String> data) {
        this.mContext = context;
        this.mData = data;
        
        options = Options.getListOptions();
    }
    
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
            convertView = layoutInflator.inflate(R.layout.item_video, null);
            holder = new ViewHolder();
            holder.player = (JCVideoPlayerStandard) convertView.findViewById(R.id.player_list_video);

            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();
        
        holder.player.setUp("http://2449.vod.myqcloud.com/2449_22ca37a6ea9011e5acaaf51d105342e3.f20.mp4"
                , JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "嫂子闭眼睛");
        imageLoader.displayImage("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640", 
                holder.player.thumbImageView, options);
        
//jcVideoPlayerStandard.thumbImageView.setImage("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640");
        
        return convertView;
    }
    
    class ViewHolder {
        JCVideoPlayerStandard player;
    }
}
