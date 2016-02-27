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

public class LineService {
    private String apiUrl = "http://113.247.250.208:15388/cskgjweb_haixin";
    
    public LineInfo getLineInfo(String lineName, int retryTimes) {
        String url = apiUrl + "/HandlerOne.ashx?name=" + lineName;
        
        try {
            String content = HttpClientUtils.getResponse(url);
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
    
    public LineStationInfo getLineStation(String lineName) {
        String url = String.format("%s/lineQuery/queryStationByLinename?linename=%s", 
                apiUrl, lineName);
        
        String content = HttpClientUtils.getResponse(url);
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNodes = mapper.readValue(content.toLowerCase(), JsonNode.class);
            if (jsonNodes != null) {
                LineStationInfo lineStation = new LineStationInfo();
                List<StationInfo> trueStations = new ArrayList<StationInfo>();
                List<StationInfo> falseStations = new ArrayList<StationInfo>();
                for (JsonNode node : jsonNodes) {
                    StationInfo stationInfo = mapper.readValue(node, StationInfo.class);
                    if (stationInfo == null) {
                        continue;
                    }
                    if (stationInfo.getUpdown().equals("uprun")) {
                        trueStations.add(stationInfo);
                    } else {
                        falseStations.add(stationInfo);
                    }
                }
                lineStation.setTrueDirection(trueStations);
                lineStation.setFalseDirection(falseStations);
                return lineStation;
            }
        } catch (Exception e) {
            Log.e("com.yhtye.shgongjiao.service.LineService", "getLineStation()", e);
        }
        return null;
    }
    
    public List<CarInfo> getStationCars(String lineId, String stopId) {
        String url = String.format("%s/car/queryCar?stationid=%s&runlineid=%s&carnum=2", 
                apiUrl, stopId, lineId);
        
        String content = HttpClientUtils.getResponse(url);
        if (TextUtils.isEmpty(content) || content.equals("[]")) {
            return null;
        }

        List<CarInfo> cars = new ArrayList<CarInfo>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readValue(content, JsonNode.class);
            if (jsonNode != null) {
                for (JsonNode node : jsonNode) {
                    CarInfo carInfo = new CarInfo();
                    carInfo.setTerminal(node.get("plate_number").getTextValue());
                    carInfo.setDistance(node.get("distance").getIntValue());
                    carInfo.setStopdis(Integer.parseInt(node.get("dis_num").getTextValue()));
                    cars.add(carInfo);
                }
            }
        } catch (Exception e) {
            Log.e("com.yhtye.shgongjiao.service.LineService", "getStationCars(): " + e.getMessage());
        }
        return cars;
    }
}
