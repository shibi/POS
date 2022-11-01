package com.rpos.pos.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CompanyDetailsTable")
public class CompanyEntity {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Integer id;

    private String companyNameInEng;
    private String companyNameInArb;
    private String taxNumber;

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public String getCompanyNameInEng() {
        return companyNameInEng;
    }

    public void setCompanyNameInEng(String companyNameInEng) {
        this.companyNameInEng = companyNameInEng;
    }

    public String getCompanyNameInArb() {
        return companyNameInArb;
    }

    public void setCompanyNameInArb(String companyNameInArb) {
        this.companyNameInArb = companyNameInArb;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }
}
