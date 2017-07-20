package com.yhtye.gongjiao.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
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

public class LineService {
    private String apiUrl = "http://www.bjbus.com/home/ajax_search_bus_stop_token.php";
    
    private String SERVERID = null;
    private String PHPSESSID = null;
    
    public List<LineInfo> getLineInfo(String lineName, int retryTimes) {
        String url = apiUrl + "?act=getLineDirOption&selBLine=" + lineName;
        
        try {
            if (PHPSESSID == null || SERVERID == null) {
                freshCookieValue();
            }
            
            Map<String, String> headerMap = getBaseHeaderMap();
            headerMap.put("Cookie", String.format("%s;%s", PHPSESSID, SERVERID));
            
            String content = getResponse(url, headerMap);
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
        
        if (PHPSESSID == null || SERVERID == null) {
            freshCookieValue();
        }
        
        Map<String, String> headerMap = getBaseHeaderMap();
        headerMap.put("Cookie", String.format("%s;%s", PHPSESSID, SERVERID));
        
        String content = getResponse(url, headerMap);
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
     * 基本Header
     * 
     * @return
     */
    private Map<String, String> getBaseHeaderMap() {
        Map<String, String> headerMap = new HashMap<String, String>();
        
        headerMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
        headerMap.put("Host", "www.bjbus.com");
        headerMap.put("Connection", "keep-alive");
        headerMap.put("Referer", "http://www.bjbus.com/home/fun_rtbus.php?uSec=00000160&uSub=00000162");
        headerMap.put("X-Requested-With", "XMLHttpRequest");
        
        return headerMap;
    }
    
    /**
     * 刷新Cookie
     */
    private void freshCookieValue() {
        String url = "http://www.bjbus.com/home/index.php";
        
        Map<String, String> headerMap = new HashMap<String, String>();
        
        headerMap.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
        headerMap.put("Host", "www.bjbus.com");
        headerMap.put("Connection", "keep-alive");
        headerMap.put("Upgrade-Insecure-Requests", "1");
        
        // 请求获取新的Token
        getResponse(url, headerMap);
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
            String message = carsInfo.get(1).text();
//            if (message != null && message.contains(" 0 站")) {
//                return "车辆即将到站，请留意";
//            }
            return message;
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
        } else if (list.size() == 1) {
            lineInfo.setStart_earlytime(list.get(0));
            lineInfo.setEnd_latetime("--");
        } else {
            // 首车：7:00-8:30、17:30-19:00末车
            int shouIndex = content.indexOf("首车：");
            int moIndex = content.indexOf("末车");
            int endIndex = content.indexOf("分段计价");
            String earlytime = content.substring(shouIndex+3, moIndex);
            String latetime = content.substring(moIndex+3, endIndex);
            if (earlytime.contains("、")) {
                String[] timeStr = earlytime.split("、", 2);
                if (timeStr.length == 2) {
                    earlytime = timeStr[0];
                    latetime = timeStr[1];
                }
            }
            lineInfo.setStart_earlytime(earlytime);
            lineInfo.setEnd_latetime(latetime);
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
    
    /**
     * get请求
     * 
     * @param url 请求url
     * @return
     */
    public String getResponse(String url, Map<String, String> headerMap) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            URI uri = new URI(url);
            HttpGet httpGet = new HttpGet(uri);
            httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                    CookiePolicy.BROWSER_COMPATIBILITY);
            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000); 
            
            if (headerMap != null) {
                for (String key : headerMap.keySet()) {
                    httpGet.setHeader(key, headerMap.get(key));
                }
            }
            
            HttpResponse response = httpclient.execute(httpGet);
            if (response == null) {
                return null;
            }
            
            Header[] cookies = response.getHeaders("Set-Cookie");
            if (cookies != null) {
                for (Header header : cookies) {
                    String value = header.getValue();
                    if (value != null) {
                        if (value.startsWith("PHPSESSID")) {
                            PHPSESSID = value.split(";")[0];
                        } else if (value.startsWith("SERVERID")) {
                            SERVERID = value.split(";")[0];
                        }
                    }
                }
            }
            HttpEntity entity = response.getEntity();
            String htmlStr = null;
            if (entity != null) {
                entity = new BufferedHttpEntity(entity);
                htmlStr = EntityUtils.toString(entity, "UTF-8");
                entity.consumeContent();
            }
            if (htmlStr != null && htmlStr.contains("timeout")) {
                PHPSESSID = null;
                SERVERID = null;
            }
            return htmlStr;

        } catch (Exception e) {
            
        }
        return null;
    }
}