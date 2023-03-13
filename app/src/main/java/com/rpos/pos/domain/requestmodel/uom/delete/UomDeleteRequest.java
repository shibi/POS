package com.rpos.pos.domain.requestmodel.uom.delete;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UomDeleteRequest {

    @SerializedName("uom_id")
    @Expose
    private String uomId;

    public String getUomId() {
        return uomId;
    }

    public void setUomId(String uomId) {
        this.uomId = uomId;
    }

}
