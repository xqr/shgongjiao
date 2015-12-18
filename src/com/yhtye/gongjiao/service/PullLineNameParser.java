package com.yhtye.gongjiao.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class PullLineNameParser {
    private List<String> lineNames = null;
    
    public List<String> getAllLineNames() {
        return lineNames;
    }
    
    public List<String> getAllLineNames(InputStream is) {
        if (lineNames == null) {
            lineNames = parseXml(is);
        }
        return lineNames;
    }
    
    private List<String> parseXml(InputStream is) {
        List<String> lineNames = new ArrayList<String>();
        
        XmlPullParser parser = Xml.newPullParser(); 
        try {
            //设置输入流 并指明编码方式  
            parser.setInput(is, "UTF-8");
            
            int eventType = parser.getEventType();  
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("name")) {
                        eventType = parser.next();
                        lineNames.add(parser.getText());
                    }
                    break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lineNames;
    }
}
