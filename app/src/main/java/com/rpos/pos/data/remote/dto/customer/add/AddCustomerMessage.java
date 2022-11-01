
package com.rpos.pos.data.remote.dto.customer.add;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddCustomerMessage {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private AddedCustomerData data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AddedCustomerData getData() {
        return data;
    }

    public void setData(AddedCustomerData data) {
        this.data = data;
    }

}
