
package com.rpos.pos.data.remote.dto.sales.list;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SalesListResponse {

    @SerializedName("message")
    @Expose
    private SalesListMessage message;

    public SalesListMessage getMessage() {
        return message;
    }

    public void setMessage(SalesListMessage message) {
        this.message = message;
    }

}
