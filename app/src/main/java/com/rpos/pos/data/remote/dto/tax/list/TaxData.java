package com.rpos.pos.data.remote.dto.tax.list;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "TaxDataTable")
public class TaxData {

    @PrimaryKey(autoGenerate = false)
    @SerializedName("tax_slab_id")
    @Expose
    private Integer taxSlabId;
    @SerializedName("slab_name")
    @Expose
    private String slabName;
    @SerializedName("tax_percentage")
    @Expose
    private Float taxPercentage;

    public Integer getTaxSlabId() {
        return taxSlabId;
    }

    public void setTaxSlabId(Integer taxSlabId) {
        this.taxSlabId = taxSlabId;
    }

    public String getSlabName() {
        return slabName;
    }

    public void setSlabName(String slabName) {
        this.slabName = slabName;
    }

    public Float getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(Float taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

}
