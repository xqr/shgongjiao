package com.yhtye.gongjiao.service;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import android.text.TextUtils;
import android.util.Log;

import com.yhtye.gongjiao.entity.CarInfo;
import com.yhtye.gongjiao.entity.LineInfo;
import com.yhtye.gongjiao.entity.StationInfo;
import com.yhtye.gongjiao.tools.HttpClientUtils;

public class LineService {
    private String apiUrl = "http://www.bjbus.com/home/ajax_search_bus_stop.php";
    
    public List<LineInfo> getLineInfo(String lineName, int retryTimes) {
        String url = apiUrl + "?act=getLineDirOption&selBLine=" + lineName;
        
        try {
            String content = HttpClientUtils.getResponse(url);
            if (TextUtils.isEmpty(content)) {
                return null;
            }
            
            String[] contentStr = content.split("</option>");
            if (contentStr.length > 1) {
                List<LineInfo> list = new ArrayList<LineInfo>();
                for (String item : contentStr) {
                    String[] cc = item.split(">");
                    if (cc.length < 2) {
                        continue;
                    }
                    String[] ee = cc[0].split("\"");
                    if (ee.length == 2) {
                        LineInfo lineInfo = new LineInfo();
                        lineInfo.setLine_id(ee[1]);
                        lineInfo.setFangxiang(cc[1]);
                        lineInfo.setLine_name(lineName);
                        
                        list.add(lineInfo);
                    }
                }
                return list;
            }
            
        } catch (Exception e) {
            if (retryTimes > 0) {
                return getLineInfo(lineName, --retryTimes);
            }
            Log.e("com.yhtye.shgongjiao.service.LineService", "getLineInfo()", e);
        }
        return null;
    }
    
    public LineInfo getLineStation(LineInfo lineInfo, String selBStop, boolean allStops) {
        String url = String.format("%s?act=busTime&selBLine=%s&selBDir=%s&selBStop=%s", 
                apiUrl, lineInfo.getLine_name(), lineInfo.getLine_id(), selBStop);
        
        String content = HttpClientUtils.getResponse(url);
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        
        
        
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            JsonNode jsonNodes = mapper.readValue(content, JsonNode.class);
//            if (jsonNodes != null) {
//                LineStationInfo lineStation = new LineStationInfo();
//                for (JsonNode node : jsonNodes) {
//                    node = mapper.readValue(node, JsonNode.class);
//                    String direction = node.get("direction").getValueAsText();
//                    node = mapper.readValue(node.get("stops"), JsonNode.class);
//                    List<StationInfo> stations = new ArrayList<StationInfo>();
//                    for (JsonNode stopNode : node) {
//                        stations.add(mapper.readValue(stopNode, StationInfo.class));
//                    }
//                    if (direction.equals("true")) {
//                        lineStation.setTrueDirection(stations);
//                    } else {
//                        lineStation.setFalseDirection(stations);
//                    }
//                }
//                return lineStation;
//            }
//        } catch (Exception e) {
//            Log.e("com.yhtye.shgongjiao.service.LineService", "getLineStation()", e);
//        }
        return null;
    }
    
    public List<CarInfo> getStationCars(String lineName, String lineId, String stopId, boolean direction) {
        String url = String.format("%s/HandlerThree.ashx?name=%s&lineid=%s&stopid=%s&direction=%s", 
                apiUrl, lineName, lineId, stopId, direction ? 0 : 1);
        
        String content = HttpClientUtils.getResponse(url);
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
