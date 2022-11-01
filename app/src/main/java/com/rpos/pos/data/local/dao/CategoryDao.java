package com.rpos.pos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.rpos.pos.data.local.entity.CategoryEntity;
import java.util.List;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface CategoryDao {

    @Insert(onConflict = REPLACE)
    void insertCategory(CategoryEntity category);

    @Query("SELECT * FROM CategoryTable WHERE categoryId=:categoryId")
    CategoryEntity getCategoryWithId(String categoryId);

    @Query("SELECT * FROM CategoryTable")
    List<CategoryEntity> getAllCategory();

    @Query("DELETE FROM CategoryTable WHERE categoryId=:categoryId")
    void deleteCategoryWithId(String categoryId);

}
