package com.rpos.pos.data.remote.dto.tax.add;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddTaxData {

    @SerializedName("tax_slab_id")
    @Expose
    private Integer taxSlabId;

    public Integer getTaxSlabId() {
        return taxSlabId;
    }

    public void setTaxSlabId(Integer taxSlabId) {
        this.taxSlabId = taxSlabId;
    }

}
