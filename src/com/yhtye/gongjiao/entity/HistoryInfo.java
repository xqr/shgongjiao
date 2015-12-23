package com.yhtye.gongjiao.entity;

import java.io.Serializable;
import java.util.List;

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
    
    private List<LineInfo> lineList;
    
    public HistoryInfo() {
        
    }
    
    public HistoryInfo(String lineFangxiang, boolean direction, List<LineInfo> lineList) {
        this.setLineFangxiang(lineFangxiang);
        this.direction = direction;
        this.lineList = lineList;
    }
    
    public boolean isDirection() {
        return direction;
    }
    public void setDirection(boolean direction) {
        this.direction = direction;
    }
    
    public List<LineInfo> getLineList() {
        return lineList;
    }

    public void setLineList(List<LineInfo> lineList) {
        this.lineList = lineList;
    }

    public String getLineFangxiang() {
        return lineFangxiang;
    }

    public void setLineFangxiang(String lineFangxiang) {
        this.lineFangxiang = lineFangxiang;
    }
}
