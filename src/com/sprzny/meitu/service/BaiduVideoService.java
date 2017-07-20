package com.sprzny.meitu.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import android.text.TextUtils;

import com.dodola.model.CategoryInfo;
import com.dodola.model.VideoInfo;
import com.dodowaterfall.HttpClientUtils;
import com.everpod.beijing.R;
import com.sprzny.meitu.app.AppApplication;

public class BaiduVideoService {
    
    public static List<CategoryInfo> createCategorys() {
        List<CategoryInfo> list = new LinkedList<CategoryInfo>();
        
        list.add(new CategoryInfo(1033, "推荐", R.drawable.bofang));
//        list.add(new CategoryInfo(1060, "影视", R.drawable.bofang));
        list.add(new CategoryInfo(1059, "搞笑", R.drawable.bofang));
//        list.add(new CategoryInfo(1058, "音乐", R.drawable.bofang));
//        list.add(new CategoryInfo(1062, "小品", R.drawable.bofang));
        list.add(new CategoryInfo(1061, "娱乐", R.drawable.bofang));
        list.add(new CategoryInfo(1063, "社会", R.drawable.bofang));
        list.add(new CategoryInfo(1066, "生活", R.drawable.bofang));
        list.add(new CategoryInfo(1064, "猎奇", R.drawable.bofang));
        list.add(new CategoryInfo(1067, "游戏", R.drawable.bofang));
        list.add(new CategoryInfo(1065, "呆萌", R.drawable.bofang));
        
        return list;
    }
    
    private static String appId = null;
    /**
     * 生成appId
     * 
     * @return
     */
    private static String getAppId() {
        if (appId  == null) {
            HistoryService history = new HistoryService(AppApplication.getApp());
            appId = history.getAppId();
            if (TextUtils.isEmpty(appId)) {
                UUID uuid = UUID.randomUUID();
                appId = uuid.toString().substring(0, 8);
                history.saveAppId(appId);
            }
        }
        return appId;
    }
    
    /**
     * 查询数据
     * 
     * @param channelId
     * @param pageNo
     * @param pageSize
     * @return
     */
    public static List<VideoInfo> parseVideoList(String channelId, int pageNo, int pageSize) {
        String url = String.format("http://cpu.baidu.com/%s/%s", channelId, getAppId());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.put("Referer", url);
        headers.put("X-Requested-With", "XMLHttpReques");
        
        String content = HttpClientUtils.postResponse(url, params, headers);
        return parseVideosJSON(channelId, content);
    }
    
    /**
     * 解析json格式
     * 
     * @param json
     * @return
     */
    private static List<VideoInfo> parseVideosJSON(String channelId, String json) {
        List<VideoInfo> videoList = new ArrayList<VideoInfo>();
        if (json == null) {
            return videoList;
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNodes = mapper.readValue(json, JsonNode.class);
            if (jsonNodes != null && jsonNodes.get("status").getIntValue() == 0) {
                jsonNodes = mapper.readValue(jsonNodes.get("data"), JsonNode.class);
                if (jsonNodes == null) {
                    return videoList;
                }
                jsonNodes = mapper.readValue(jsonNodes.get("result"), JsonNode.class);
                for (JsonNode node : jsonNodes) {
                    if (!node.get("type").getTextValue().equals("video")) {
                        continue;
                    }
                    node = mapper.readValue(node.get("data"), JsonNode.class);
                    VideoInfo videoInfo = new VideoInfo();
                    videoInfo.setPlaybackCount(node.get("playbackCount").getIntValue());
                    videoInfo.setThumbUrl("http:" + node.get("thumbUrl").getTextValue());
                    videoInfo.setTitle(node.get("title").getTextValue());
                    videoInfo.setUrl(node.get("url").getTextValue());
                    videoInfo.setUpdateTime(node.get("updateTime").getTextValue());
                    videoInfo.setSource(node.get("source").getTextValue());
                    videoInfo.setId(node.get("id").getIntValue());
                    videoInfo.setDetailUrl("http://cpu.baidu.com/"+channelId+"/abc00564/detail/"+videoInfo.getId()+"/video?foward=list");
                    videoInfo.setDuration(node.get("duration").getIntValue());
                    
                    videoList.add(videoInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return videoList;
    }
}
