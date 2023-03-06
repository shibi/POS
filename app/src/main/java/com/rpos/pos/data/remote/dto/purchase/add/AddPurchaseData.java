package com.rpos.pos.data.remote.dto.purchase.add;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddPurchaseData {

    @SerializedName("invoice_id")
    @Expose
    private String invoiceId;

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

}
