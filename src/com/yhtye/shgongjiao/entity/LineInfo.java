package com.yhtye.shgongjiao.entity;

public class LineInfo {
    private String end_earlytime = "6:00";
    private String end_latetime = "22:00";
    private String end_stop;
    private String line_id;
    private String line_name;
    private String start_earlytime = "6:00";
    private String start_latetime = "22:00";
    private String start_stop;
    
    public String getEnd_earlytime() {
        return end_earlytime;
    }
    public void setEnd_earlytime(String end_earlytime) {
        this.end_earlytime = end_earlytime;
    }
    public String getEnd_latetime() {
        return end_latetime;
    }
    public void setEnd_latetime(String end_latetime) {
        this.end_latetime = end_latetime;
    }
    public String getEnd_stop() {
        return end_stop;
    }
    public void setEnd_stop(String end_stop) {
        this.end_stop = end_stop;
    }
    public String getLine_id() {
        return line_id;
    }
    public void setLine_id(String line_id) {
        this.line_id = line_id;
    }
    public String getLine_name() {
        return line_name;
    }
    public void setLine_name(String line_name) {
        this.line_name = line_name;
    }
    public String getStart_earlytime() {
        return start_earlytime;
    }
    public void setStart_earlytime(String start_earlytime) {
        this.start_earlytime = start_earlytime;
    }
    public String getStart_latetime() {
        return start_latetime;
    }
    public void setStart_latetime(String start_latetime) {
        this.start_latetime = start_latetime;
    }
    public String getStart_stop() {
        return start_stop;
    }
    public void setStart_stop(String start_stop) {
        this.start_stop = start_stop;
    }
}
