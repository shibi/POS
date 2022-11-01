package com.rpos.pos.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "supplierTable")
public class SupplierEntity {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Integer id;

    @SerializedName("supplier_name")
    @Expose
    private String supplierName;
    @SerializedName("tax_id")
    @Expose
    private String taxId;

    private float creaditLimit;

    private float rate;

    private String address;

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public float getCreaditLimit() {
        return creaditLimit;
    }

    public void setCreaditLimit(float creaditLimit) {
        this.creaditLimit = creaditLimit;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
