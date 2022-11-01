package com.rpos.pos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.rpos.pos.data.local.entity.SupplierEntity;

import java.util.List;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface SupplierDao {

    @Insert(onConflict = REPLACE)
    long insertSupplier(SupplierEntity supplier);

    @Query("SELECT * FROM supplierTable")
    List<SupplierEntity> getAllSuppliers();

    @Query("SELECT * FROM supplierTable WHERE id=:supplierId")
    SupplierEntity getSupplierWithId(int supplierId);

    @Delete
    void delete(SupplierEntity supplier);
}
