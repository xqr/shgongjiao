package com.yhtye.gongjiao.tools;

import java.util.regex.Pattern;

public class RegularUtil {
    /**
     * 判断是否为数字
     * 
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){ 
        Pattern pattern = Pattern.compile("[0-9]*"); 
        return pattern.matcher(str).matches();    
     } 
}
