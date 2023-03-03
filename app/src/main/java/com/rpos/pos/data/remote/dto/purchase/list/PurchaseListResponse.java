
package com.rpos.pos.data.remote.dto.purchase.list;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PurchaseListResponse {

    @SerializedName("message")
    @Expose
    private PurchaseListMessage message;

    public PurchaseListMessage getMessage() {
        return message;
    }

    public void setMessage(PurchaseListMessage message) {
        this.message = message;
    }

}
