package com.rpos.pos.data.remote.dto.items.list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rpos.pos.data.local.entity.ItemEntity;
import java.util.List;

public class ItemData {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("item_id")
    @Expose
    private Integer itemId;
    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("brand")
    @Expose
    private Object brand;
    @SerializedName("uom")
    @Expose
    private Integer uom;
    @SerializedName("category")
    @Expose
    private Integer category;
    @SerializedName("maintain_stock")
    @Expose
    private Integer maintainStock;
    @SerializedName("barcodes")
    @Expose
    private List<BarcodeData> barcodes;
    @SerializedName("rate")
    @Expose
    private Float rate;
    @SerializedName("available_qty")
    @Expose
    private Float availableQty;
    @SerializedName("tax")
    @Expose
    private float itemTax;

    private String uomName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getUom() {
        return uom;
    }

    public void setUom(Integer uom) {
        this.uom = uom;
    }

    public Object getBrand() {
        return brand;
    }

    public void setBrand(Object brand) {
        this.brand = brand;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Integer getMaintainStock() {
        return maintainStock;
    }

    public void setMaintainStock(Integer maintainStock) {
        this.maintainStock = maintainStock;
    }

    public List<BarcodeData> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<BarcodeData> barcodes) {
        this.barcodes = barcodes;
    }

    public Float getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Float availableQty) {
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
        /*this.itemId = itemEntity.getItemId();
        this.itemName = itemEntity.getItemName();
        this.uom = itemEntity.getUom();

        this.uomName = itemEntity.getUomName();

        this.category = itemEntity.getCategory();
        this.maintainStock = itemEntity.getMaintainStock();
        //barcodes = itemEntity.getBarcodes();
        this.availableQty = itemEntity.getAvailableQty();
        this.description = itemEntity.getDescription();
        this.rate = itemEntity.getRate();
        this.itemTax = itemEntity.getItemTax();*/
    }
}
