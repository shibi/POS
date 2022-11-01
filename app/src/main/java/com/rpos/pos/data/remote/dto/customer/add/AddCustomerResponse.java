
package com.rpos.pos.data.remote.dto.customer.add;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddCustomerResponse {

    @SerializedName("message")
    @Expose
    private AddCustomerMessage message;

    public AddCustomerMessage getMessage() {
        return message;
    }

    public void setMessage(AddCustomerMessage message) {
        this.message = message;
    }

}
