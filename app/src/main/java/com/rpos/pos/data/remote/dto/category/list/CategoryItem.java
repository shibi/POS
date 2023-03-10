
package com.rpos.pos.data.remote.dto.category.list;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CategoryItem {

    @SerializedName("category_id")
    @Expose
    private Integer categoryId;
    @SerializedName("category")
    @Expose
    private String category;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
