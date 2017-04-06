package com.yhtye.shgongjiao.tools;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpURLConnectionUtils {
    /**
     * post请求
     * 
     * @param url 请求url
     * @param paramsMap  请求参数
     * @return
     */
    public static String postResponse(String url, Map<String, Object> paramsMap) {
        try {
            URL uri = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) uri.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            httpURLConnection.setConnectTimeout(60000);//连接超时 单位毫秒
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            
            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            String post = null;
            if (paramsMap != null) {
                for (String key : paramsMap.keySet()) {
                    if (post != null) {
                        post=post + "&";
                    }
                    post = post + key + "=" +paramsMap.get(key).toString();
                }
            }
            printWriter.write(post);//post的参数 xx=xx&yy=yy
            // flush输出流的缓冲
            printWriter.flush();
            
          //开始获取数据
            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            byte[] arr = new byte[1024];
            while((len=bis.read(arr))!= -1){
                bos.write(arr,0,len);
                bos.flush();
            }
            bos.close();
            return bos.toString("utf-8");
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
            URL uri = new URL(url);
            //打开连接
            HttpURLConnection urlConnection = (HttpURLConnection) uri.openConnection();
            urlConnection.setConnectTimeout(60000);
            
            // 增加Referer
            if (referer != null) {
                urlConnection.addRequestProperty("Referer", referer);
            }
            
            if(200 == urlConnection.getResponseCode()){
                //得到输入流
                InputStream is =urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while(-1 != (len = is.read(buffer))){
                    baos.write(buffer,0,len);
                    baos.flush();
                }
                return baos.toString("utf-8");
            }
        } catch (Exception e) {
//            Log.i(url, e.getMessage());
        }
        return null;
    }
}
