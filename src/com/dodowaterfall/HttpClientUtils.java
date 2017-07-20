package com.dodowaterfall;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.sprzny.meitu.app.AppApplication;
import com.sprzny.meitu.service.HistoryService;

public class HttpClientUtils {
    
    private static Map<String, String> baiduCookie = null;
    
    private static HistoryService history = new HistoryService(AppApplication.getApp());
    /**
     * 获取cookie
     * 
     * @return
     */
    private static Map<String, String> getBaiduCookie() {
        if (baiduCookie == null) {
            baiduCookie = history.getCookieHistory();
        }
        if (baiduCookie == null) {
            baiduCookie = new HashMap<String, String>();
        }
        return baiduCookie;
    }
    
    
    /**
     * post请求
     * 
     * @param url 请求url
     * @param paramsMap  请求参数
     * @return
     */
    public static String postResponse(String url, Map<String, Object> paramsMap, 
            Map<String, String> headersMap) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpost = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            if (paramsMap != null) {
                for (String key : paramsMap.keySet()) {
                    params.add(new BasicNameValuePair(key, paramsMap.get(key).toString()));
                }
            }
            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000); 
            
            httpost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            
            // 添加请求头部
            if (headersMap != null) {
                for (String key : headersMap.keySet()) {
                    httpost.setHeader(key, headersMap.get(key));
                }
            }
            
            // 添加cookie
            String cookieStr = null;
            for (String key : getBaiduCookie().keySet()) {
                if (cookieStr == null) {
                    cookieStr = key +"="+baiduCookie.get(key);
                } else {
                    cookieStr = cookieStr + "; " +key +"=" + baiduCookie.get(key);
                }
            }
            if (cookieStr != null) {
                httpost.setHeader("Cookie", cookieStr);
            }
            
            HttpResponse response = httpclient.execute(httpost);
            HttpEntity entity = response.getEntity();
            String htmlStr = null;
            if (entity != null) {
                // 解析cookie
                Header[] cookieHeaders = response.getHeaders("Set-Cookie");
                if (cookieHeaders != null) {
                    boolean update = false;
                    for (Header item : cookieHeaders) {
                        update = true;
                        String value = item.getValue();
                        String[] cookieValue = value.split(";")[0].split("=", 2);
                        baiduCookie.put(cookieValue[0], cookieValue[1]);
                    }
                    if (update) {
                        history.saveCookieHistory(baiduCookie);
                    }
                }
                
                entity = new BufferedHttpEntity(entity);
                htmlStr = EntityUtils.toString(entity);
                entity.consumeContent();
            }
            return htmlStr;

        } catch (Exception e) {
            // TODO 
//            e.printStackTrace();
//            ErrorLogger.info("url:%s, params:%s", e, url, paramsMap.toString());
        }
        return null;
    }
    
    public static String getResponse(String url) {
        return getResponse(url, null);
    }
    
    /**
     * get请求
     * 
     * @param url 请求url
     * @return
     */
    public static String getResponse(String url, String referer) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            URI uri = new URI(url);
            HttpGet httpGet = new HttpGet(uri);
            httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                    CookiePolicy.BROWSER_COMPATIBILITY);
            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000); 
            
            // 增加Referer
            if (referer != null) {
                httpGet.setHeader("Referer", referer);
            }
            HttpResponse response = httpclient.execute(httpGet);
            if (response == null) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            String htmlStr = null;
            if (entity != null) {
                entity = new BufferedHttpEntity(entity);
                htmlStr = EntityUtils.toString(entity);
                entity.consumeContent();
            }
            
            return htmlStr;

        } catch (Exception e) {
//            Log.i(url, e.getMessage());
        }
        return null;
    }
}
