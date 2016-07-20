package com.yhtye.shgongjiao.entity;

import java.io.Serializable;

public class StationInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String zdmc;
    private String id;
    public String getZdmc() {
        return zdmc;
    }
    public void setZdmc(String zdmc) {
        this.zdmc = zdmc;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
