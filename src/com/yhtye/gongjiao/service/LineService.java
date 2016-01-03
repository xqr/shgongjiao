package com.yhtye.gongjiao.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String apiUrl = "http://www.szjt.gov.cn/apts/APTSLine.aspx";
    
    public List<LineInfo> getLineInfo(String lineName, int retryTimes) {
        String url = apiUrl;
        
        try {
            Map<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("Referer", "http://www.szjt.gov.cn/apts/APTSLine.aspx");
            
            Map<String, Object> paramsMap = new HashMap<String, Object>();
            paramsMap.put("ctl00$MainContent$SearchLine", "搜索");
            paramsMap.put("ctl00$MainContent$LineName", lineName);
            paramsMap.put("__VIEWSTATE", "/wEPDwUJNDk3MjU2MjgyD2QWAmYPZBYCAgMPZBYCAgEPZBYCAgYPDxYCHgdWaXNpYmxlaGRkZJjIjf9wec64bUk0awl8Fmu9ZpeMHtOkmveJctfcLWzs");
            paramsMap.put("__EVENTVALIDATION", "/wEWAwLC6/qEDgL88Oh8AqX89aoKYSqjSGRgG6uatob0mRtv8UxGdjgHvVdIogSh29pwM0M=");
            paramsMap.put("__VIEWSTATEGENERATOR", "964EC381");
            
            String content = HttpClientUtils.postResponse(url, paramsMap, headerMap);
            if (TextUtils.isEmpty(content) || !content.contains("LineGuid")) {
                return null;
            }
            
            Document doc = Jsoup.parse(content);
            Element mainContentData = doc.getElementById("MainContent_DATA");
            Elements lineData = mainContentData.getElementsByTag("tr");
            List<LineInfo> list = new ArrayList<LineInfo>();
            for (Element data : lineData) {
                Elements items = data.getElementsByTag("td");
                if (items == null || items.size() != 2) {
                    continue;
                }
                String name = items.get(0).text();
                if (TextUtils.isEmpty(name) || !name.equals(lineName)) {
                    continue;
                }
                LineInfo lineInfo = new LineInfo();
                lineInfo.setLine_id(items.get(0).getElementsByTag("a").first().attr("href"));
                lineInfo.setFangxiang(items.get(1).text());
                lineInfo.setLine_name(name);
                list.add(lineInfo);
            }
            return list;
        } catch (Exception e) {
            if (retryTimes > 0) {
                return getLineInfo(lineName, --retryTimes);
            }
            Log.e("com.yhtye.shgongjiao.service.LineService", "getLineInfo()", e);
        }
        return null;
    }
    
    public LineInfo getLineStation(LineInfo lineInfo) {
        String url = String.format("%s%s", 
                "http://www.szjt.gov.cn/apts/", lineInfo.getLine_id());
        
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Referer", "http://www.szjt.gov.cn/apts/APTSLine.aspx");
        
        String content = HttpClientUtils.getResponse(url, headerMap);
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        
        try {
            Document doc = Jsoup.parse(content);
            Element mainContentData = doc.getElementById("MainContent_DATA");
            Elements lineData = mainContentData.getElementsByTag("tr");
            List<StationInfo> list = new ArrayList<StationInfo>();
            for (Element data : lineData) {
                Elements items = data.getElementsByTag("td");
                if (items == null || items.size() != 4) {
                    continue;
                }
                StationInfo stationInfo = new StationInfo(items.get(1).text(), items.get(0).text());
//                String carName = items.get(2).text();
//                if (!TextUtils.isEmpty(carName)) {
//                    String carMessage = String.format("%s", carName);
//                }           
                list.add(stationInfo);
            }
            if (list.size() > 2) {
                lineInfo.setStart_stop(list.get(0).getZdmc());
                lineInfo.setEnd_stop(list.get(list.size() - 1).getZdmc());
                lineInfo.setStations(list);
            }
        } catch (Exception e) {
            Log.e("com.yhtye.shgongjiao.service.LineService", "getLineStation()", e);
        }
        return lineInfo;
    }

}
