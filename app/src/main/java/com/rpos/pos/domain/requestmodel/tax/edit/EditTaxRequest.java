package com.rpos.pos.domain.requestmodel.tax.edit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EditTaxRequest {

    @SerializedName("slab_name")
    @Expose
    private String slabName;
    @SerializedName("tax_slab_id")
    @Expose
    private Integer taxSlabId;
    @SerializedName("tax_percentage")
    @Expose
    private String taxPercentage;

    public String getSlabName() {
        return slabName;
    }

    public void setSlabName(String slabName) {
        this.slabName = slabName;
    }

    public Integer getTaxSlabId() {
        return taxSlabId;
    }

    public void setTaxSlabId(Integer taxSlabId) {
        this.taxSlabId = taxSlabId;
    }

    public String getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(String taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

}
