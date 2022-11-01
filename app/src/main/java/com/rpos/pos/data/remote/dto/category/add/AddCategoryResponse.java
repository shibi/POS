package com.rpos.pos.data.remote.dto.category.add;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddCategoryResponse {

    @SerializedName("message")
    @Expose
    private AddCategoryMessage message;

    public AddCategoryMessage getMessage() {
        return message;
    }

    public void setMessage(AddCategoryMessage message) {
        this.message = message;
    }

}
