
package com.rpos.pos.domain.requestmodel.sales.add;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddSalesRequest {

    @SerializedName("customer_id")
    @Expose
    private String customerId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("items")
    @Expose
    private List<SalesItem> items;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<SalesItem> getItems() {
        return items;
    }

    public void setItems(List<SalesItem> items) {
        this.items = items;
    }

}
