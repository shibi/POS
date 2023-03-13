package com.rpos.pos.data.remote.dto.uom.delete;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UomDeleteResponse {

    @SerializedName("message")
    @Expose
    private UomDeleteMessage message;

    public UomDeleteMessage getMessage() {
        return message;
    }

    public void setMessage(UomDeleteMessage message) {
        this.message = message;
    }

}
