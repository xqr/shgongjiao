package com.yhtye.gongjiao.entity;

import java.io.Serializable;

public class HistoryInfo implements Serializable {
    private static final long serialVersionUID = 9070687073013182163L;
    /**
     * 线路
     */
    private String lineFangxiang;
    /**
     * 方向
     */
    private boolean direction;
    
    private BusLineInfo busLine;
    
    public HistoryInfo() {
        
    }
    
    public HistoryInfo(String lineFangxiang, boolean direction, BusLineInfo busLine) {
        this.setLineFangxiang(lineFangxiang);
        this.direction = direction;
        this.busLine = busLine;
    }
    
    public boolean isDirection() {
        return direction;
    }
    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public String getLineFangxiang() {
        return lineFangxiang;
    }

    public void setLineFangxiang(String lineFangxiang) {
        this.lineFangxiang = lineFangxiang;
    }

    public BusLineInfo getBusLine() {
        return busLine;
    }

    public void setBusLine(BusLineInfo busLine) {
        this.busLine = busLine;
    }
}
