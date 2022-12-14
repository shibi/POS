package com.rpos.pos.data.remote.dto.customer.details;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CustomerDetailsResponse {

    @SerializedName("message")
    @Expose
    private CustomerData message;

    public CustomerData getMessage() {
        return message;
    }

    public void setMessage(CustomerData message) {
        this.message = message;
    }

}
