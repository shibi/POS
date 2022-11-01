
package com.rpos.pos.data.remote.dto.category.details;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CategoryDetail {

    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("category")
    @Expose
    private String category;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
