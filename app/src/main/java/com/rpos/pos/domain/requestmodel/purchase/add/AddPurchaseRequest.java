
package com.rpos.pos.domain.requestmodel.purchase.add;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddPurchaseRequest {

    @SerializedName("supplier_id")
    @Expose
    private String supplierId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("items")
    @Expose
    private List<PurchaseItem> items;

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<PurchaseItem> getItems() {
        return items;
    }

    public void setItems(List<PurchaseItem> items) {
        this.items = items;
    }

}
