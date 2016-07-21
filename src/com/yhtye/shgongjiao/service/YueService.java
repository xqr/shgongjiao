package com.yhtye.shgongjiao.service;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;

import com.yhtye.shgongjiao.entity.CardInfo;
import com.yhtye.shgongjiao.tools.HttpClientUtils;

public class YueService {
    
    private Context context;
    private int maxCount = 3;
    
    public YueService(Context context) {
        this.context = context;
    }
    
    public YueService(Context context, int maxCount) {
        this.context = context;
        this.maxCount = maxCount;
    }
    
    public void saveHistory(List<CardInfo> historyList) {
        StringWriter str=new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(str, historyList);
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
        
        SharedPreferences sp =context.getSharedPreferences("card_history_strs", 0);
        Editor editor=sp.edit();
        editor.putString("card", str.toString());
        editor.commit();
    }
    
    public void appendHistory(CardInfo history) {
        List<CardInfo> list = getHistory();
        if (list == null || list.size() == 0) {
            list = new ArrayList<CardInfo>();
            list.add(history);
            saveHistory(list);
            return;
        }
        
        List<CardInfo> historyList = new ArrayList<CardInfo>(maxCount);
        historyList.add(history);
        
        int count = 1;
        for (int i = 0; i < list.size(); i++) {
            CardInfo item = list.get(i);
            if (!item.getCardNumber().equals(history.getCardNumber()) 
                    && count < maxCount) {
                historyList.add(item);
            }
            count++;
        }
        
        saveHistory(historyList);
    }
    
    public List<CardInfo> getHistory() {
        SharedPreferences sp =context.getSharedPreferences("card_history_strs", 0);
        String content = sp.getString("card", "");
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readValue(content, JsonNode.class);
            if (jsonNode != null) {
                List<CardInfo> list = new ArrayList<CardInfo>();
                for (JsonNode node : jsonNode) {
                    list.add(mapper.readValue(node, CardInfo.class));
                }
                return list;
            }
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
        
        return null;
    }
    
    public void deleteHistory() {
        SharedPreferences sp =context.getSharedPreferences("card_history_strs", 0);
        Editor editor=sp.edit();
        editor.putString("card", "");
        editor.commit();
    }
    
    private Map<String, CardInfo> yueMap = new HashMap<String, CardInfo>();
    
    /**
     * 查询余额
     * 
     * @param carNumber
     * @return
     */
    public CardInfo searchYue(String carNumber) {
        // 余额查询缓存1分钟
        CardInfo cardInfo = yueMap.get(carNumber);
        if (cardInfo != null 
                && cardInfo.getSearchTime() + 60 * 1000 >= new Date().getTime()) {
            return cardInfo;
        }
        
        String url = "http://220.248.75.36/handapp/PGcardAmtServlet?arg1=" 
                    + carNumber +"&callback=yue&_=" + new Date().getTime();
        try {
            String content = HttpClientUtils.getResponse(url, "http://www.sptcc.com/");
            if (content == null || TextUtils.isEmpty(content)) {
                return null;
            }
            String[] resultStr = content.split("'", 3);
            if (resultStr.length == 3) {
                cardInfo = new CardInfo(carNumber, resultStr[1]);
                yueMap.put(carNumber, cardInfo);
                
                return cardInfo;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}