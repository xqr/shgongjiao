package com.sprzny.meitu.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import android.util.Log;

import com.dodola.model.CategoryInfo;
import com.dodola.model.DuitangInfo;
import com.dodowaterfall.HttpClientUtils;
import com.everpod.beijing.R;

public class SprznyService {
    
    /**
     * 生成美图类别
     * 
     * @return
     */
    public static List<CategoryInfo> createCategorys() {
        List<CategoryInfo> list = new LinkedList<CategoryInfo>();
        
        list.add(new CategoryInfo(1, "性感美女", R.drawable.bofang));
        list.add(new CategoryInfo(2, "丝袜美女", R.drawable.bofang));
        list.add(new CategoryInfo(3, "韩国美女", R.drawable.bofang));
        list.add(new CategoryInfo(4, "外国美女", R.drawable.bofang));
        list.add(new CategoryInfo(5, "比基尼", R.drawable.bofang));
        list.add(new CategoryInfo(6, "内衣美女", R.drawable.bofang));
        list.add(new CategoryInfo(7, "清纯美女", R.drawable.bofang));
        list.add(new CategoryInfo(8, "长腿美女", R.drawable.bofang));
        list.add(new CategoryInfo(9, "美女明星", R.drawable.bofang));
        list.add(new CategoryInfo(10, "街拍美女", R.drawable.bofang));
        
        return list;
    }
    
    /**
     * 从服务端获取需要展示的类别
     * 
     * @return
     */
    public static List<CategoryInfo> getShowCategorys() {
        List<CategoryInfo> result = new ArrayList<CategoryInfo>();
        result.add(new CategoryInfo(0, "推荐美女", R.drawable.bofang));
        try {
            String json = HttpClientUtils.getResponse("http://www.sprzny.com/mmonly/showid/");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNodes = mapper.readValue(json, JsonNode.class);
            if (jsonNodes != null) {
                String ids = jsonNodes.get("id").getTextValue();
                List<CategoryInfo> list = createCategorys();
                for (String item : ids.split(",")) {
                    for (CategoryInfo data : list) {
                        if (item.equals(String.valueOf(data.getCategoryId()))) {
                            result.add(data);
                        }
                    }
                }
            }
        } catch (IOException e) {
            Log.e("IOException is : ", e.toString());
        }
        return result;
    }
    
    
    /**
     * 精选
     * 
     * @return
     */
    public static List<DuitangInfo> mmonly(int page, int categoryid) {
        String url = "http://www.sprzny.com/mmonly/l/" ;
        if (categoryid != 0) {
            url = url + categoryid + "/" + page;
        } else {
            url = url + page;
        }
        
        String json = "";
        json = HttpClientUtils.getResponse(url);
        
        return parseNewsJSON(json);
    }
    
    /**
     * 解析json
     * 
     * @param json
     * @return
     */
    private static List<DuitangInfo> parseNewsJSON(String json) {
        List<DuitangInfo> duitangs = new ArrayList<DuitangInfo>();
        try {
            if (null != json) {
                
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNodes = mapper.readValue(json, JsonNode.class);
                if (jsonNodes != null) {
                    for (JsonNode node : jsonNodes) {
                        duitangs.add(mapper.readValue(node, DuitangInfo.class));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return duitangs;
    }
}
