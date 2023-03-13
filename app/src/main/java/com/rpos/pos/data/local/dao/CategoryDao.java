package com.rpos.pos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.rpos.pos.data.local.entity.CategoryEntity;
import com.rpos.pos.data.remote.dto.category.list.CategoryItem;

import java.util.List;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface CategoryDao {

    /*@Insert(onConflict = REPLACE)
    void insertCategory(CategoryEntity category);

    @Insert(onConflict = REPLACE)
    void insertCategoryList(List<CategoryEntity> category);

    @Query("SELECT * FROM CategoryTable WHERE categoryId=:categoryId")
    CategoryEntity getCategoryWithId(String categoryId);

    @Query("SELECT * FROM CategoryTable")
    List<CategoryEntity> getAllCategory();

    @Query("DELETE FROM CategoryTable WHERE categoryId=:categoryId")
    void deleteCategoryWithId(String categoryId);

    @Query("DELETE FROM CategoryTable")
    void deleteAll();*/

    //-------------------------------------------2.0
    @Insert(onConflict = REPLACE)
    void insert_category(CategoryItem category);

    @Query("SELECT * FROM category_table")
    List<CategoryItem> getAllCategories();

    @Insert(onConflict = REPLACE)
    void insertCategoryAsList(List<CategoryItem> category);

    @Query("SELECT * FROM category_table WHERE categoryId=:categoryId")
    CategoryItem getCategoryWithIds(Integer categoryId);

    @Query("DELETE FROM category_table")
    void deleteAllCategories();


}
