package com.yhtye.gongjiao.entity;

import android.text.TextUtils;

public class StopStation extends LineInfo {
    private static final long serialVersionUID = 1L;
    
    private String stop_id;
    private String direction;
    private String fangxiang;
    
    public String getStop_id() {
        return stop_id;
    }
    public void setStop_id(String stop_id) {
        this.stop_id = stop_id;
    }
    public String getDirection() {
        return direction;
    }
    public void setDirection(String direction) {
        this.direction = direction;
    }
    
    public boolean getDirectionFlag() {
        if (TextUtils.isEmpty(direction)) {
            return false;
        }
        return direction.equals("1");
    }
    
    public String getFangxiang() {
        return fangxiang;
    }
    public void setFangxiang(String fangxiang) {
        this.fangxiang = fangxiang;
    }
}
