package com.rpos.pos.data.remote.dto.sales.add;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddSalesInvoiceResponse {

    @SerializedName("message")
    @Expose
    private AddSalesMessage message;

    public AddSalesMessage getMessage() {
        return message;
    }

    public void setMessage(AddSalesMessage message) {
        this.message = message;
    }

}
