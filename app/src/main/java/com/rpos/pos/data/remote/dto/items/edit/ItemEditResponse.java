package com.rpos.pos.data.remote.dto.items.edit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemEditResponse {

    @SerializedName("message")
    @Expose
    private ItemEditMessage message;

    public ItemEditMessage getMessage() {
        return message;
    }

    public void setMessage(ItemEditMessage message) {
        this.message = message;
    }

}
