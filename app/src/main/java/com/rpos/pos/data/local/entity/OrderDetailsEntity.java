package com.rpos.pos.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "OrderDetailsTable")
public class OrderDetailsEntity {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Integer id;

    private long orderId;
    private Integer itemId;
    private Integer quantity;
    private float variablePrice;
    private String orderStatus;

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public float getVariablePrice() {
        return variablePrice;
    }

    public void setVariablePrice(float variablePrice) {
        this.variablePrice = variablePrice;
    }
}
