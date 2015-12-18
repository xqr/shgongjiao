package com.yhtye.gongjiao.service;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;

import com.yhtye.gongjiao.entity.HistoryInfo;

public class HistoryService {
    private Context context;
    private int maxCount = 3;
    
    public HistoryService(Context context) {
        this.context = context;
    }
    
    public HistoryService(Context context, int maxCount) {
        this.context = context;
        this.maxCount = maxCount;
    }
    
    public void saveHistory(List<HistoryInfo> historyList) {
        StringWriter str=new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(str, historyList);
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
        
        SharedPreferences sp =context.getSharedPreferences("history_strs", 0);
        Editor editor=sp.edit();
        editor.putString("history", str.toString());
        editor.commit();
    }
    
    public void appendHistory(HistoryInfo history) {
        List<HistoryInfo> list = getHistory();
        if (list == null || list.size() == 0) {
            list = new ArrayList<HistoryInfo>();
            list.add(history);
            saveHistory(list);
            return;
        }
        
        List<HistoryInfo> historyList = new ArrayList<HistoryInfo>(maxCount);
        historyList.add(history);
        
        int count = 1;
        for (int i = 0; i < list.size(); i++) {
            HistoryInfo item = list.get(i);
            if (!item.getLineName().equals(history.getLineName()) 
                    && count < maxCount) {
                historyList.add(item);
            }
            count++;
        }
        
        saveHistory(historyList);
    }
    
    public List<HistoryInfo> getHistory() {
        SharedPreferences sp =context.getSharedPreferences("history_strs", 0);
        String content = sp.getString("history", "");
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readValue(content, JsonNode.class);
            if (jsonNode != null) {
                List<HistoryInfo> list = new ArrayList<HistoryInfo>();
                for (JsonNode node : jsonNode) {
                    list.add(mapper.readValue(node, HistoryInfo.class));
                }
                return list;
            }
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
        
        return null;
    }
    
    public void deleteHistory() {
        SharedPreferences sp =context.getSharedPreferences("history_strs", 0);
        Editor editor=sp.edit();
        editor.putString("history", "");
        editor.commit();
    }
}
