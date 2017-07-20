package com.dodola.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class VideoInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String thumbUrl;
    private String title;
    private String updateTime;
    private String url;
    private String source;
    private int playbackCount;
    private int id;
    private String detailUrl;
    /**
     * 视频长度，单位为秒
     */
    private int duration;
    private String clusterNo;
    
    /**
     * 格式化的视频长度
     * 
     * @return
     */
    public String getFormatDuration() {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss", Locale.CHINA);
        return formatter.format(this.duration * 1000);
    }
    
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public String getDetailUrl() {
        return detailUrl;
    }
    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getThumbUrl() {
        return thumbUrl;
    }
    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public int getPlaybackCount() {
        return playbackCount;
    }
    public void setPlaybackCount(int playbackCount) {
        this.playbackCount = playbackCount;
    }
    
    public int getWidth() {
        return 375;
    }

//    public void setWidth(int width) {
//        this.width = width;
//    }

    public int getHeight() {
        return 210;
    }

    public String getClusterNo() {
        return clusterNo;
    }

    public void setClusterNo(String clusterNo) {
        this.clusterNo = clusterNo;
    }

//  public void setHeight(int height) {
//      this.height = height;
//  }
}
