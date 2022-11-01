package com.rpos.pos.data.remote.dto.items.add;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddItemResponse {

    @SerializedName("message")
    @Expose
    private AddItemMessage message;

    public AddItemMessage getMessage() {
        return message;
    }

    public void setMessage(AddItemMessage message) {
        this.message = message;
    }

}
