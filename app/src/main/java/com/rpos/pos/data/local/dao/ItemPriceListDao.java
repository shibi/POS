package com.rpos.pos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.rpos.pos.data.local.entity.ItemPriceEntity;
import java.util.List;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ItemPriceListDao {

    @Insert(onConflict = REPLACE)
    void insertItem(ItemPriceEntity itemPriceEntity);

    @Query("SELECT * FROM ItemPriceTable")
    List<ItemPriceEntity> getAllItems();

    @Query("SELECT * FROM ItemPriceTable WHERE id=:itemPriceId")
    ItemPriceEntity getItemPriceWithId(int itemPriceId);

    @Query("SELECT * FROM ItemPriceTable WHERE itemId=:itemId AND priceListId=:priceListId")
    ItemPriceEntity findPriceForItemWithPriceListId(int priceListId, int itemId);

    @Delete
    void delete(ItemPriceEntity itemPriceEntity);

}
