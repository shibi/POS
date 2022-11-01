package com.rpos.pos.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ItemPriceTable")
public class ItemPriceEntity {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    private String itemCode;
    private int itemId;
    private String itemName;
    private int priceListType;
    private float rate;
    private int currencyId;
    private String customerId;
    private int itemUomId;
    private int priceListId;
    private String priceListName;
    private String batchNo;
    private float packageUnit;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getPriceListType() {
        return priceListType;
    }

    public void setPriceListType(int priceListType) {
        this.priceListType = priceListType;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public int getItemUomId() {
        return itemUomId;
    }

    public void setItemUomId(int itemUomId) {
        this.itemUomId = itemUomId;
    }

    public int getPriceListId() {
        return priceListId;
    }

    public void setPriceListId(int priceListId) {
        this.priceListId = priceListId;
    }

    public String getPriceListName() {
        return priceListName;
    }

    public void setPriceListName(String priceListName) {
        this.priceListName = priceListName;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public float getPackageUnit() {
        return packageUnit;
    }

    public void setPackageUnit(float packageUnit) {
        this.packageUnit = packageUnit;
    }
}
