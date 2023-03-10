package com.rpos.pos.domain.requestmodel.category.delete;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryDeleteRequest {

    @SerializedName("category_id")
    @Expose
    private String categoryId;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

}
