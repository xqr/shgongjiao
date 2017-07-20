package com.dodola.model;

public class CategoryInfo {
    private int categoryId;
    private String categoryTitle;
    private int imageId;
    
    public CategoryInfo(int categoryId, String categoryTitle, int imageId) {
        this.categoryId = categoryId;
        this.categoryTitle = categoryTitle;
        this.imageId = imageId;
    }
    
    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public int getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    public String getCategoryTitle() {
        return categoryTitle;
    }
    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }
}
