package com.rpos.pos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.rpos.pos.data.local.entity.CompanyEntity;
import java.util.List;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface CompanyDetailsDao {

    @Insert(onConflict = REPLACE)
    long insertDetails(CompanyEntity companyDetails);

    @Query("SELECT * FROM CompanyDetailsTable")
    List<CompanyEntity> getAllCompanyDetails();

    @Delete
    void deleteOrder(CompanyEntity company);
}
