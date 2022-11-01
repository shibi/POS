package com.rpos.pos.data.local.dao;

import static androidx.room.OnConflictStrategy.REPLACE;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.rpos.pos.data.local.entity.TaxSlabEntity;
import java.util.List;

@Dao
public interface TaxesDao {

    @Insert(onConflict = REPLACE)
    void insertTaxes(TaxSlabEntity tax);

    @Query("SELECT * FROM taxslabsTable")
    List<TaxSlabEntity> getAllSlabs();

    @Query("SELECT * FROM taxslabsTable WHERE id=:id")
    TaxSlabEntity getTaxDetailsWithId(Integer id);

    @Delete
    void delete(TaxSlabEntity taxSlab);
}
