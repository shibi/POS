package com.rpos.pos.domain.requestmodel.uom.add;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddUomRequest {

    @SerializedName("uom_name")
    @Expose
    private String uomName;

    public String getUomName() {
        return uomName;
    }

    public void setUomName(String uomName) {
        this.uomName = uomName;
    }

}
