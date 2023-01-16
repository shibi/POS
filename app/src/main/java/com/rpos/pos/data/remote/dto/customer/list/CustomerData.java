
package com.rpos.pos.data.remote.dto.customer.list;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CustomerData {

    @SerializedName("customer_id")
    @Expose
    private String customerId;
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("tax_id")
    @Expose
    private String taxId;
    @SerializedName("credit_limit")
    @Expose
    private List<CreditLimit> creditLimit = null;

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

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public List<CreditLimit> getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(List<CreditLimit> creditLimit) {
        this.creditLimit = creditLimit;
    }


}
