package com.rpos.pos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.rpos.pos.data.local.entity.CustomerEntity;
import java.util.List;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface CustomerDao {

    @Insert(onConflict = REPLACE)
    void insertCustomer(CustomerEntity customer);

    @Query("SELECT * FROM CustomerTable")
    List<CustomerEntity> getAllCustomer();

    @Query("SELECT * FROM CustomerTable WHERE customerId=:customerId")
    CustomerEntity getCustomerWithId(String customerId);

    @Query("SELECT * FROM CustomerTable WHERE mobileNo=:mobile")
    CustomerEntity findCustomerWithMobile(String mobile);

    @Delete
    void delete(CustomerEntity customer);

}
