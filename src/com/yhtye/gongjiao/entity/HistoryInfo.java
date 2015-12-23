package com.yhtye.gongjiao.entity;

import java.io.Serializable;

public class HistoryInfo implements Serializable {
    private static final long serialVersionUID = 9070687073013182163L;
    /**
     * 线路
     */
    private String lineName;
    /**
     * 方向
     */
    private boolean direction;
    
    private String trueLineId;
    private String trueFangxiang;
    
    private String falseLineId = "";
    private String falseFangxiang = "";
    
    public HistoryInfo() {
        
    }
    
    public HistoryInfo(String lineName, boolean direction) {
        this.lineName = lineName;
        this.direction = direction;
    }
    
    public String getLineName() {
        return lineName;
    }
    public void setLineName(String lineName) {
        this.lineName = lineName;
    }
    public boolean isDirection() {
        return direction;
    }
    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public String getTrueLineId() {
        return trueLineId;
    }

    public void setTrueLineId(String trueLineId) {
        this.trueLineId = trueLineId;
    }

    public String getTrueFangxiang() {
        return trueFangxiang;
    }

    public void setTrueFangxiang(String trueFangxiang) {
        this.trueFangxiang = trueFangxiang;
    }

    public String getFalseLineId() {
        return falseLineId;
    }

    public void setFalseLineId(String falseLineId) {
        this.falseLineId = falseLineId;
    }

    public String getFalseFangxiang() {
        return falseFangxiang;
    }

    public void setFalseFangxiang(String falseFangxiang) {
        this.falseFangxiang = falseFangxiang;
    }
}
