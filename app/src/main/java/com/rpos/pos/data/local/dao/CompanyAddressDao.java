package com.rpos.pos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.rpos.pos.data.local.entity.CompanyAddressEntity;
import java.util.List;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface CompanyAddressDao {

    @Insert(onConflict = REPLACE)
    long insert(CompanyAddressEntity companyDetails);

    @Query("SELECT * FROM CompanyAddressTable")
    List<CompanyAddressEntity> getCompanyDetails();

    @Delete
    void delete(CompanyAddressEntity company);

    @Query("DELETE FROM CompanyAddressTable")
    void deleteAll();
}
