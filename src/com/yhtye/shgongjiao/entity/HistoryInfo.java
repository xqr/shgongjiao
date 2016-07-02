package com.yhtye.shgongjiao.entity;

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
    /**
     * 起点
     */
    private String startStop = "";
    /**
     * 终点
     */
    private String endStop = "";
    
//    private LineStationInfo lineStationInfo;
    
    public HistoryInfo() {
        
    }
    
    public HistoryInfo(String lineName, boolean direction, String startStop, String endStop) {
        this.lineName = lineName;
        this.direction = direction;
        this.startStop = startStop;
        this.endStop = endStop;
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

    public String getStartStop() {
        return startStop;
    }

    public void setStartStop(String startStop) {
        this.startStop = startStop;
    }

    public String getEndStop() {
        return endStop;
    }

    public void setEndStop(String endStop) {
        this.endStop = endStop;
    }
}
