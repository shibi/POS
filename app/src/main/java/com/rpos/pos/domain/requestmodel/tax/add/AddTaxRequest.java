package com.rpos.pos.domain.requestmodel.tax.add;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddTaxRequest {

    @SerializedName("slab_name")
    @Expose
    private String slabName;
    @SerializedName("tax_percentage")
    @Expose
    private String taxPercentage;

    public String getSlabName() {
        return slabName;
    }

    public void setSlabName(String slabName) {
        this.slabName = slabName;
    }

    public String getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(String taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

}
