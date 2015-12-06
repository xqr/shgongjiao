package com.yhtye.shgongjiao.entity;

public class PositionInfo {
    private String x;
    private String y;
    
    public PositionInfo(double x, double y) {
        this.x = String.valueOf(x);
        this.y = String.valueOf(y);
    }
    
    /**
     * 经度
     * @return
     */
    public String getX() {
        return x;
    }
    public void setX(String x) {
        this.x = x;
    }
    /**
     * 纬度
     * @return
     */
    public String getY() {
        return y;
    }
    public void setY(String y) {
        this.y = y;
    }
}
