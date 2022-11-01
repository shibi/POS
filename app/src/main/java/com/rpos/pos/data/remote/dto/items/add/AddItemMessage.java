package com.rpos.pos.data.remote.dto.items.add;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddItemMessage {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private AddItemData data;

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

    public AddItemData getData() {
        return data;
    }

    public void setData(AddItemData data) {
        this.data = data;
    }

}
