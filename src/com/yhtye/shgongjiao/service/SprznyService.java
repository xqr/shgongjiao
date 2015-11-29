package com.yhtye.shgongjiao.service;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import android.text.TextUtils;
import android.util.Log;

import com.yhtye.shgongjiao.tools.HttpClientUtils;

public class SprznyService {
    
    /**
     * 附近的公交站
     * 
     * @param x
     * @param y
     * @return
     */
    public static List<String> searchNearStations(String x, String y) {
        String url = String.format("http://api.sprzny.com/gongjiao/location/%s/%s", x, y);
        
        try {
            String content = HttpClientUtils.getResponse(url);
            if (TextUtils.isEmpty(content)) {
                return null;
            }
            
            ObjectMapper mapper = new ObjectMapper();
            
            JsonNode jsonNode = mapper.readValue(content, JsonNode.class);
            if (jsonNode != null) {
                List<String> list = new ArrayList<String>();
                for (JsonNode node : jsonNode) {
                    list.add(node.getTextValue());
                }
                return list;
            }
        } catch (Exception e) {
            Log.e("com.yhtye.shgongjiao.service.SprznyService", "searchNearStations()", e);
        }
        return null;
    }
}
