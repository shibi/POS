package com.rpos.pos.data.remote.dto.items.list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rpos.pos.data.local.entity.ItemEntity;

import java.util.List;


public class ItemData {

    @SerializedName("item_id")
    @Expose
    private String itemId;
    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("uom")
    @Expose
    private String uom;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("maintain_stock")
    @Expose
    private Integer maintainStock;
    @SerializedName("barcodes")
    @Expose
    private List<String> barcodes = null;
    @SerializedName("available_qty")
    @Expose
    private Integer availableQty;

    @SerializedName("item_desc")
    @Expose
    private String description;

    @SerializedName("rate")
    @Expose
    private float rate;

    @SerializedName("itemTax")
    @Expose
    private float itemTax;

    private String uomName;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getMaintainStock() {
        return maintainStock;
    }

    public void setMaintainStock(Integer maintainStock) {
        this.maintainStock = maintainStock;
    }

    public List<String> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<String> barcodes) {
        this.barcodes = barcodes;
    }

    public Integer getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Integer availableQty) {
        this.availableQty = availableQty;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getItemTax() {
        return itemTax;
    }

    public void setItemTax(float itemTax) {
        this.itemTax = itemTax;
    }

    public String getUomName() {
        return uomName;
    }

    public void setUomName(String uomName) {
        this.uomName = uomName;
    }

    public void convertFromItemEntity(ItemEntity itemEntity){
        this.itemId = ""+itemEntity.getItemId();
        this.itemName = itemEntity.getItemName();
        this.uom = itemEntity.getUom();

        this.uomName = itemEntity.getUomName();

        this.category = itemEntity.getCategory();
        this.maintainStock = itemEntity.getMaintainStock();
        barcodes = itemEntity.getBarcodes();
        this.availableQty = itemEntity.getAvailableQty();
        this.description = itemEntity.getDescription();
        this.rate = itemEntity.getRate();
        this.itemTax = itemEntity.getItemTax();
    }
}
