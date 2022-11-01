package com.rpos.pos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.rpos.pos.data.remote.dto.uom.list.UomItem;
import java.util.List;

@Dao
public interface UomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUom(List<UomItem> uomItems);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSingleUom(UomItem uomItem);

    @Query("SELECT * FROM uomtable")
    List<UomItem> getAllUnitsOfMessurements();

    @Query("SELECT * FROM uomtable WHERE uomId=:id")
    UomItem getUomDetailsWithId(int id);

    @Delete
    void delete(UomItem uomItem);
}
