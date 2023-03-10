package com.rpos.pos.data.remote.dto.category.delete;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryDeleteResponse {

    @SerializedName("message")
    @Expose
    private CategoryDeleteMessage message;

    public CategoryDeleteMessage getMessage() {
        return message;
    }

    public void setMessage(CategoryDeleteMessage message) {
        this.message = message;
    }

}
