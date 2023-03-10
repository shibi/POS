package com.rpos.pos.domain.requestmodel.item.delete;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeleteItemRequest {

    @SerializedName("item_id")
    @Expose
    private String itemId;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

}
