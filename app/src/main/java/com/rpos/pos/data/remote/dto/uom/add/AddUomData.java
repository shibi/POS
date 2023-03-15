
package com.rpos.pos.data.remote.dto.uom.add;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddUomData {

    @SerializedName("uom_id")
    @Expose
    private Integer uomId;

    public Integer getUomId() {
        return uomId;
    }

    public void setUomId(Integer uomId) {
        this.uomId = uomId;
    }

}
