package com.rpos.pos.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;


@Entity(tableName = "ItemDataTable")
public class ItemEntity {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @SerializedName("item_id")
    @Expose
    private Integer itemId;
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
}
