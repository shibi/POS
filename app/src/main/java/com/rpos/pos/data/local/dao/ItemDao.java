package com.rpos.pos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.rpos.pos.data.local.entity.ItemEntity;
import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ItemDao {

    @Insert(onConflict = REPLACE)
    void insertItem(ItemEntity item);

    @Query("SELECT * FROM ItemDataTable")
    List<ItemEntity> getAllItems();

    @Query("SELECT * FROM ItemDataTable WHERE itemId=:itemId")
    ItemEntity getItemDetails(int itemId);

    @Query("SELECT * FROM ItemDataTable WHERE barcodes=:barcodes")
    ItemEntity findItemWithBarcode(List<String> barcodes);

    @Query("SELECT * FROM ItemDataTable WHERE itemId IN (:itemIdList)")
    List<ItemEntity> getSelectedItems(List<Integer> itemIdList);

    @Query("DELETE FROM ItemDataTable WHERE itemId=:itemId")
    void deleteItemWithId(int itemId);

    @Query("SELECT * FROM ItemDataTable WHERE category=:categoryId")
    List<ItemEntity> getAllItemWithProvidedCategory(String categoryId);

    @Insert(onConflict = REPLACE)
    void updateList(List<ItemEntity> selectedItems);
}
