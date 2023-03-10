package com.rpos.pos.data.remote.dto.customer.delete;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomerDeleteResponse {

    @SerializedName("message")
    @Expose
    private CustomerDeleteMessage message;

    public CustomerDeleteMessage getMessage() {
        return message;
    }

    public void setMessage(CustomerDeleteMessage message) {
        this.message = message;
    }

}
