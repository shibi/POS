
package com.rpos.pos.data.remote.dto.customer.add;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddedCustomerData {

    @SerializedName("customer_id")
    @Expose
    private String customerId;
    @SerializedName("customer_name")
    @Expose
    private String customerName;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

}
