package com.rpos.pos.data.remote.dto.items.delete;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeleteItemResponse {

    @SerializedName("message")
    @Expose
    private ItemDeleteMessage message;

    public ItemDeleteMessage getMessage() {
        return message;
    }

    public void setMessage(ItemDeleteMessage message) {
        this.message = message;
    }

}
