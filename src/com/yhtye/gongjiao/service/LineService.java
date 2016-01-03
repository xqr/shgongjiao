package com.yhtye.gongjiao.service;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import android.text.TextUtils;
import android.util.Log;

import com.yhtye.gongjiao.entity.BusLineInfo;
import com.yhtye.gongjiao.tools.HttpClientUtils;

public class LineService {
    private String apiUrl = "http://www.wbus.cn/getQueryServlet";
    
    public BusLineInfo getLineInfo(String lineName, boolean direction, int retryTimes) {
        String url = apiUrl ;
        
        try {
            Map<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("Referer", "http://www.wbus.cn/");
            
            Map<String, Object> paramsMap = new HashMap<String, Object>();
            paramsMap.put("Type", "LineDetail");
            paramsMap.put("lineNo", lineName);
            paramsMap.put("direction", direction? "1" : "0");
            
            String content = HttpClientUtils.postResponse(url, paramsMap, headerMap);
            if (TextUtils.isEmpty(content)) {
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNodes = mapper.readValue(content, JsonNode.class);
            if (jsonNodes == null 
                    || jsonNodes.get("data") == null) {
                return null;
            }
            
            return mapper.readValue(jsonNodes.get("data"), BusLineInfo.class);
        } catch (Exception e) {
            if (retryTimes > 0) {
                return getLineInfo(lineName, direction, --retryTimes);
            }
            Log.e("com.yhtye.shgongjiao.service.LineService", "getLineInfo()", e);
        }
        return null;
    }
}
