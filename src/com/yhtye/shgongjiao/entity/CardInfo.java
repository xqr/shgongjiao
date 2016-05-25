package com.yhtye.shgongjiao.entity;

import java.util.Date;

/**
 * 公交卡对象
 */
public class CardInfo {
    private String cardNumber;
    private String yue;
    private long searchTime = new Date().getTime();
    
    public CardInfo() {
        
    }
    
    public CardInfo(String cardNumber, String yue) {
        this.cardNumber = cardNumber;
        this.yue = yue;
    }
    
    public String getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    public String getYue() {
        return yue;
    }
    public void setYue(String yue) {
        this.yue = yue;
    }
    public long getSearchTime() {
        return searchTime;
    }
    public void setSearchTime(long searchTime) {
        this.searchTime = searchTime;
    }
}
