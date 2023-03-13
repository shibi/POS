package com.rpos.pos.domain.requestmodel.item.edit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemEditRequest {

    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("item_group")
    @Expose
    private String itemGroup;
    @SerializedName("uom")
    @Expose
    private String uom;
    @SerializedName("barcode")
    @Expose
    private String barcode;
    @SerializedName("item_tax")
    @Expose
    private Float itemTax;
    @SerializedName("maintain_stock")
    @Expose
    private Integer maintainStock;
    @SerializedName("rate")
    @Expose
    private Float rate;
    @SerializedName("stock_qty")
    @Expose
    private Integer stockQty;
    @SerializedName("item_id")
    @Expose
    private String itemId;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(String itemGroup) {
        this.itemGroup = itemGroup;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Float getItemTax() {
        return itemTax;
    }

    public void setItemTax(Float itemTax) {
        this.itemTax = itemTax;
    }

    public Integer getMaintainStock() {
        return maintainStock;
    }

    public void setMaintainStock(Integer maintainStock) {
        this.maintainStock = maintainStock;
    }

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }

    public Integer getStockQty() {
        return stockQty;
    }

    public void setStockQty(Integer stockQty) {
        this.stockQty = stockQty;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

}
