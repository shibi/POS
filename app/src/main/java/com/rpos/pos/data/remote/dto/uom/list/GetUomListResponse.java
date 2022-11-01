package com.rpos.pos.data.remote.dto.uom.list;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class GetUomListResponse {

    @SerializedName("message")
    @Expose
    private List<UomItem> message = null;

    public List<UomItem> getMessage() {
        return message;
    }

    public void setMessage(List<UomItem> message) {
        this.message = message;
    }

}
