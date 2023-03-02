package com.rpos.pos.domain.models.item;

import com.rpos.pos.data.local.entity.ItemEntity;
import com.rpos.pos.data.local.entity.OrderDetailsEntity;

public class PickedItem {

    private String id;
    private String itemName;
    private String uom;
    private int quantity;
    private int stock;
    private float rate;
    private String categoryId;
    private String barcode;
    private float tax;
    private boolean isMaintainStock;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }

    public boolean getMaintainStock() {
        return isMaintainStock;
    }

    public void setMaintainStock(boolean maintainStock) {
        isMaintainStock = maintainStock;
    }

    public void convertFromSavedItemEntity(ItemEntity item){
        //this.id = item.getItemId();
        this.itemName = item.getItemName();
        this.uom = item.getUom();
        this.stock = item.getAvailableQty();
        this.rate = item.getRate();
        this.categoryId = item.getCategory();
        //this.barcode = item.getBarcodes();
        this.tax = item.getItemTax();
        this.isMaintainStock = (item.getMaintainStock() !=0);
    }

    public ItemEntity getItemEntityFromPickedItem(){
        ItemEntity itemEntity = new ItemEntity();
        //itemEntity.setItemId(id);
        itemEntity.setItemName(itemName);
        itemEntity.setUom(uom);
        itemEntity.setAvailableQty(stock);
        itemEntity.setRate(rate);
        itemEntity.setCategory(categoryId);
        itemEntity.setItemTax(tax);
        itemEntity.setMaintainStock(isMaintainStock?1:0);
        return itemEntity;
    }

    public PickedItem getPickedItemFrom(String id, String itemName, float rate,int quantity,int stock, boolean maintain_stock){
        PickedItem item = new PickedItem();
        item.setId(id);
        item.setItemName(itemName);
        item.setRate(rate);
        item.setQuantity(quantity);
        item.setStock(stock);
        item.setMaintainStock(maintain_stock);
        return item;
    }

    public PickedItem getPickedItemFromFields(String id, String itemName,String uomId, float rate,int quantity,int stock, boolean maintain_stock){
        PickedItem item = new PickedItem();
        item.setId(id);
        item.setItemName(itemName);
        item.setUom(uomId);
        item.setRate(rate);
        item.setQuantity(quantity);
        item.setStock(stock);
        item.setMaintainStock(maintain_stock);
        return item;
    }
}
