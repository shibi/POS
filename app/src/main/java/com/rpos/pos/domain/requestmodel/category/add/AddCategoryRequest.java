package com.rpos.pos.domain.requestmodel.category.add;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddCategoryRequest {

    @SerializedName("category")
    @Expose
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
