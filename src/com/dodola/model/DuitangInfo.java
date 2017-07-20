package com.dodola.model;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

public class DuitangInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
//    // 图片高和宽
//    private int width;
//    private int height;
	
    // 图片属性
    private String id;
    private String categoryid;
    private String title;
    private String totalnum;
    private List<String> images;
    private String source;
    private String status = "";
    private int rand = -1;
    
    /**
     * 随机选择一张图片作为封面
     * 
     * @return
     */
    public String getIsrc() {
        if (images == null || images.isEmpty()) {
            return "";
        }
        if (rand == -1) {
            rand = new Random().nextInt(images.size());
        }
        return images.get(rand);
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotalnum() {
        return totalnum;
    }

    public void setTotalnum(String totalnum) {
        this.totalnum = totalnum;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImage(List<String> images) {
        this.images = images;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public int getWidth() {
        return 550;
    }

//    public void setWidth(int width) {
//        this.width = width;
//    }

	public int getHeight() {
		return 902;
	}

//	public void setHeight(int height) {
//		this.height = height;
//	}
}
