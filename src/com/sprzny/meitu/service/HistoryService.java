package com.sprzny.meitu.service;

import java.io.StringWriter;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;

public class HistoryService {
    private Context context;
    
    public HistoryService(Context context) {
        this.context = context;
    }
    
    /**
     * appId
     * 
     * @param appId
     */
    public void saveAppId(String appId) {
        SharedPreferences sp =context.getSharedPreferences("history_strs", 0);
        Editor editor=sp.edit();
        editor.putString("history_appId", appId);
        editor.commit();
    }
    
    /**
     * 获得appId
     * 
     * @return
     */
    public String getAppId() {
        SharedPreferences sp =context.getSharedPreferences("history_strs", 0);
        return sp.getString("history_appId", "");
    }
    
    /**
     * 保存Cookie
     * 
     * @param cookieHistory
     */
    public void saveCookieHistory(Map<String, String> cookieHistory) {
        StringWriter str=new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(str, cookieHistory);
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
        
        SharedPreferences sp =context.getSharedPreferences("history_strs", 0);
        Editor editor=sp.edit();
        editor.putString("history", str.toString());
        editor.commit();
    }
    
    /**
     * 查询cookie
     * 
     * @return
     */
    public Map<String, String> getCookieHistory() {
        SharedPreferences sp =context.getSharedPreferences("history_strs", 0);
        String content = sp.getString("history", "");
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(content, Map.class);
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
        
        return null;
    }
    /**
     * 删除cookie
     */
    public void deleteCookieHistory() {
        SharedPreferences sp =context.getSharedPreferences("history_strs", 0);
        Editor editor=sp.edit();
        editor.putString("history", "");
        editor.commit();
    }
}
