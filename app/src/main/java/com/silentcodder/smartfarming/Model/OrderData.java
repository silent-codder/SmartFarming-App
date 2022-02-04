package com.silentcodder.smartfarming.Model;

public class OrderData extends OrderId{
    String UserId;
    String Status;
    long ProductCount;
    long TotalPrice;
    long TimeStamp;

    public OrderData() {
    }

    public OrderData(String userId, String status, long productCount, long totalPrice, long timeStamp) {
        UserId = userId;
        Status = status;
        ProductCount = productCount;
        TotalPrice = totalPrice;
        TimeStamp = timeStamp;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public long getProductCount() {
        return ProductCount;
    }

    public void setProductCount(long productCount) {
        ProductCount = productCount;
    }

    public long getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        TotalPrice = totalPrice;
    }

    public long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        TimeStamp = timeStamp;
    }
}
