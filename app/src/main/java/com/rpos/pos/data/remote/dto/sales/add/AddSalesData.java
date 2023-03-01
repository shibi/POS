
package com.rpos.pos.data.remote.dto.sales.add;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddSalesData {

    @SerializedName("invoice_id")
    @Expose
    private String invoiceId;
    @SerializedName("pos_queue")
    @Expose
    private PosQueue posQueue;

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public PosQueue getPosQueue() {
        return posQueue;
    }

    public void setPosQueue(PosQueue posQueue) {
        this.posQueue = posQueue;
    }

}
