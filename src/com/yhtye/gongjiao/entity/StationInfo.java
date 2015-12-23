package com.yhtye.gongjiao.entity;

public class StationInfo {
    private String zdmc;
    private String id;
    private String carmessage;
    
    public StationInfo() {
        
    }
    
    public StationInfo(String id, String zdmc) {
        this.id = id;
        this.zdmc = zdmc;
    }
    
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
    public String getCarmessage() {
        return carmessage;
    }
    public void setCarmessage(String carmessage) {
        this.carmessage = carmessage;
    }
}
