
package com.rpos.pos.data.remote.dto.tax.list;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TaxListResponse {

    @SerializedName("message")
    @Expose
    private TaxMessage message;

    public TaxMessage getMessage() {
        return message;
    }

    public void setMessage(TaxMessage message) {
        this.message = message;
    }

}
