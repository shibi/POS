package com.rpos.pos.domain.requestmodel.item.add;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddItemRequest {

    @SerializedName("item_name")
    @Expose
    private String itemName;

    @SerializedName("user_id")
    @Expose
    private String userId;

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
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("item_tax")
    @Expose
    private Float itemTax;
    @SerializedName("rate")
    @Expose
    private Float rate;
    @SerializedName("maintain_stock")
    @Expose
    private boolean maintainStock;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Float getItemTax() {
        return itemTax;
    }

    public void setItemTax(Float itemTax) {
        this.itemTax = itemTax;
    }

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }

    public boolean getMaintainStock() {
        return maintainStock;
    }

    public void setMaintainStock(boolean maintainStock) {
        this.maintainStock = maintainStock;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
