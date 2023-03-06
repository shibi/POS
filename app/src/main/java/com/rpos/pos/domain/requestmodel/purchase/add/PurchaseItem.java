
package com.rpos.pos.domain.requestmodel.purchase.add;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PurchaseItem {

    @SerializedName("item_code")
    @Expose
    private String itemCode;
    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("rate")
    @Expose
    private String rate;
    @SerializedName("qty")
    @Expose
    private String qty;
    @SerializedName("uom")
    @Expose
    private String uom;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

}
