package com.rpos.pos.data.remote.dto.payment_modes.details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PaymentAccount {

    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("account")
    @Expose
    private String account;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

}
