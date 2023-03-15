package com.rpos.pos.data.local.dao;

import static androidx.room.OnConflictStrategy.REPLACE;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.rpos.pos.data.local.entity.TaxSlabEntity;
import com.rpos.pos.data.remote.dto.tax.list.TaxData;

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

    //------------------------

    @Insert(onConflict = REPLACE)
    void insertSingleTax(TaxData tax);

    @Insert(onConflict = REPLACE)
    void insertTax(List<TaxData> taxDataList);

    @Query("SELECT * FROM TaxDataTable")
    List<TaxData> getAllTaxDatas();

    @Query("SELECT * FROM TaxDataTable WHERE taxSlabId=:id")
    TaxData getTaxDetailWithId(Integer id);

    @Delete
    void deleteSingleTax(TaxData taxData);

    @Query("DELETE FROM TaxDataTable")
    void deleteTaxTable();
}
