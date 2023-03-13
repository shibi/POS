
package com.rpos.pos.data.remote.dto.category.list;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "category_table")
public class CategoryItem {

    @PrimaryKey(autoGenerate = false)
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
