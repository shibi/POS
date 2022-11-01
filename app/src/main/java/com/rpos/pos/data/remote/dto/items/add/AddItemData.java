package com.rpos.pos.data.remote.dto.items.add;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddItemData {

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
