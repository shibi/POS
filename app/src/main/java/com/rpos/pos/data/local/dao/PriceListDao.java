package com.rpos.pos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.rpos.pos.data.local.entity.PriceListEntity;
import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PriceListDao {

    @Insert(onConflict = REPLACE)
    void insertItem(PriceListEntity item);

    @Query("SELECT * FROM PriceListTable")
    List<PriceListEntity> getAllPriceList();

    @Query("SELECT * FROM PriceListTable WHERE id=:priceListId")
    PriceListEntity getPriceListWithId(int priceListId);

    @Delete
    void deletePriceList(PriceListEntity priceListEntity);

}
