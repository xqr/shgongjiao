package com.yhtye.gongjiao.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;
import android.util.Log;

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
    
    public LineInfo getLineStation(LineInfo lineInfo, int selBStop, boolean allStops) {
        String url = String.format("%s?act=busTime&selBLine=%s&selBDir=%s&selBStop=%s", 
                apiUrl, lineInfo.getLine_name(), lineInfo.getLine_id(), selBStop);
        
        String content = HttpClientUtils.getResponse(url);
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNodes = mapper.readValue(content, JsonNode.class);
            if (jsonNodes != null && jsonNodes.get("html") != null) {
                content = jsonNodes.get("html").getTextValue();
                // 空判断
              if (TextUtils.isEmpty(content)) {
                  return null;
              }
                Document doc = Jsoup.parse(content); 
                // 解析头部
                Element header = doc.getElementsByClass("inquiry_header").first();
                // 解析车辆
                String carInfo = parseCarInfo(lineInfo, header);
                if (allStops) {
                    // 解析所有站点
                    List<StationInfo> list = parseStops(doc.getElementById("cc_stop"));
                    if (list != null) {
                        lineInfo.setStart_stop(list.get(0).getZdmc());
                        lineInfo.setEnd_stop(list.get(list.size() - 1).getZdmc());
                        lineInfo.setStations(list);
                    }
                }
                // 保存车辆信息
                if (lineInfo.getStations() != null && lineInfo.getStations().size() >= selBStop) {
                    lineInfo.getStations().get(selBStop-1).setCarmessage(carInfo);
                }
            }
        } catch (Exception e) {
            Log.e("com.yhtye.shgongjiao.service.LineService", "getLineStation()", e);
        }
        return lineInfo;
    }
    
    /**
     * 解析车辆信息
     * 
     * @param element
     * @return
     */
    private String parseCarInfo(LineInfo lineInfo, Element element) {
        if (element == null) {
            return null;
        }
        Element car = element.getElementsByTag("article").first();
        if (car == null) {
            return null;
        }
        Elements carsInfo = car.getElementsByTag("p");
        if (carsInfo.size() == 2) {
            // 解析车辆出发时间
            parseLineTime(carsInfo.get(0).text(), lineInfo);
            return carsInfo.get(1).text();
        }
        return null;
    }
    
    /**
     * 解析车辆时间
     * 
     * @param content
     * @param lineInfo
     */
    private void parseLineTime(String content, LineInfo lineInfo) {
        if (lineInfo == null) {
            return;
        }
        if (TextUtils.isEmpty(content)) {
            return;
        }
        Pattern p=Pattern.compile("([0-9]+):([0-9]+)");   
        Matcher m=p.matcher(content);
        List<String> list = new ArrayList<String>();
        while(m.find()){
            list.add(m.group());
        }
        if (list.size() == 2) {
            lineInfo.setStart_earlytime(list.get(0));
            lineInfo.setEnd_latetime(list.get(1));
        }
    }
    
    /**
     * 解析所有站点
     * 
     * @param element
     * @return
     */
    private List<StationInfo> parseStops(Element element) {
        if (element == null) {
            return null;
        }
        Elements stops = element.getElementsByTag("span");
        if (stops == null || stops.size() == 0) {
            return null;
        }
        int steps = 1;
        List<StationInfo> list = new ArrayList<StationInfo>();
        for(Element item : stops) {
            list.add(new StationInfo(String.valueOf(steps), item.text()));
            steps++;
        }
        return list;
    }
    
    public static void main(String[] args) {
        LineService service = new LineService();
        String lineName = "1";
        List<LineInfo> list = service.getLineInfo(lineName, 0);
        if (list == null) {
            return;
        }
        for (LineInfo lineInfo : list) {
            service.getLineStation(lineInfo, 3, true);
        }
        for (LineInfo item : list) {
            System.out.println(item.getStart_stop());
            System.out.println(item.getStart_earlytime());
            System.out.println(item.getStations().size());
            System.out.println(item.getStations().get(2).getCarmessage());
        }
    }
}
