
package com.rpos.pos.data.remote.dto.customer.details;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CreditLimit {

    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("credit_limit")
    @Expose
    private Float creditLimit;
    @SerializedName("credit_days")
    @Expose
    private Float creditDays;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Float getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Float creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Float getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Float creditDays) {
        this.creditDays = creditDays;
    }

}
