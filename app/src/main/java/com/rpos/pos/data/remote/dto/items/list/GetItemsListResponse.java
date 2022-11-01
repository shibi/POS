package com.rpos.pos.data.remote.dto.items.list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GetItemsListResponse {

    @SerializedName("message")
    @Expose
    private List<ItemData> message = null;

    public List<ItemData> getMessage() {
        return message;
    }

    public void setMessage(List<ItemData> message) {
        this.message = message;
    }

}
