
package com.rpos.pos.domain.requestmodel.sales.view;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class InvoiceViewRequest {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("invoice_id")
    @Expose
    private String invoiceId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

}
