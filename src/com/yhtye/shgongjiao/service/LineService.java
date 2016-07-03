package com.yhtye.shgongjiao.service;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import android.text.TextUtils;
import android.util.Log;
import com.yhtye.shgongjiao.entity.CarInfo;
import com.yhtye.shgongjiao.entity.LineInfo;
import com.yhtye.shgongjiao.entity.LineStationInfo;
import com.yhtye.shgongjiao.entity.StationInfo;
import com.yhtye.shgongjiao.tools.HttpClientUtils;

public class LineService implements ILineService {
    
    private String refer = "http://xxbs.sh.gov.cn:8080/weixinpage/index.html";
    
    @Override
    public LineInfo getLineInfo(String lineName, int retryTimes) {
        String url = "http://xxbs.sh.gov.cn:8080/weixinpage/HandlerOne.ashx?name=" 
                    + lineName;
        
        try {
            String content = HttpClientUtils.getResponse(url, refer);
            if (TextUtils.isEmpty(content) || content.equals("no")) {
                return null;
            }
            // 非法字符过滤
            content = content.replace("\\", "").replace("ntt", "").replace("nt", "");
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(content, LineInfo.class);
        } catch (Exception e) {
            if (retryTimes > 0) {
                return getLineInfo(lineName, --retryTimes);
            }
            Log.e("com.yhtye.shgongjiao.service.LineService", "getLineInfo()", e);
        }
        return null;
    }
    @Override
    public LineStationInfo getLineStation(String lineName, String lineId) {
        String url = String.format("http://xxbs.sh.gov.cn:8080/weixinpage/HandlerTwo.ashx?name=%s&lineid=%s", 
                lineName, lineId);
        
        String content = HttpClientUtils.getResponse(url, refer);
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNodes = mapper.readValue(content, JsonNode.class);
            if (jsonNodes != null) {
                LineStationInfo lineStation = new LineStationInfo();
                for (JsonNode node : jsonNodes) {
                    node = mapper.readValue(node, JsonNode.class);
                    String direction = node.get("direction").getValueAsText();
                    node = mapper.readValue(node.get("stops"), JsonNode.class);
                    List<StationInfo> stations = new ArrayList<StationInfo>();
                    for (JsonNode stopNode : node) {
                        stations.add(mapper.readValue(stopNode, StationInfo.class));
                    }
                    if (direction.equals("true")) {
                        lineStation.setTrueDirection(stations);
                    } else {
                        lineStation.setFalseDirection(stations);
                    }
                }
                return lineStation;
            }
        } catch (Exception e) {
            Log.e("com.yhtye.shgongjiao.service.LineService", "getLineStation()", e);
        }
        return null;
    }
    
    @Override
    public List<CarInfo> getStationCars(String lineName, String lineId, String stopId, boolean direction) {
        String url = String.format("http://xxbs.sh.gov.cn:8080/weixinpage/HandlerThree.ashx?name=%s&lineid=%s&stopid=%s&direction=%s", 
                lineName, lineId, stopId, direction ? 0 : 1);
        
        String content = HttpClientUtils.getResponse(url, refer);
        if (TextUtils.isEmpty(content)) {
            return null;
        }

        List<CarInfo> cars = new ArrayList<CarInfo>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readValue(content, JsonNode.class);
            if (jsonNode != null && jsonNode.get("cars") != null) {
                JsonNode nodes =  mapper.readValue(jsonNode.get("cars"), JsonNode.class);
                for (JsonNode node : nodes) {
                    cars.add(mapper.readValue(node, CarInfo.class));
                }
            }
        } catch (Exception e) {
            Log.e("com.yhtye.shgongjiao.service.LineService", "getStationCars(): " + e.getMessage());
        }
        return cars;
    }
}
