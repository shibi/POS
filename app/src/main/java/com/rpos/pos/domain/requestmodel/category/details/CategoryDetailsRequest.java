package com.rpos.pos.domain.requestmodel.category.details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CategoryDetailsRequest {

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
