package com.rpos.pos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.rpos.pos.data.local.entity.PaymentModeEntity;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PaymenModeDao {

    @Insert(onConflict = REPLACE)
    void insertPaymentMode(PaymentModeEntity payMode);

    @Query("SELECT * FROM PayModeTable")
    List<PaymentModeEntity> getAllPaymentModeList();

    @Delete
    void delete(PaymentModeEntity payMode);
}
