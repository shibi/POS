package com.rpos.pos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.rpos.pos.data.local.entity.OrderDetailsEntity;
import com.rpos.pos.data.local.entity.PurchaseOrderDetailsEntity;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PurchaseOrderDetailsDao {

    @Insert(onConflict = REPLACE)
    long insertOrderDetail(PurchaseOrderDetailsEntity orderDetails);

    @Insert(onConflict = REPLACE)
    void insertOrderDetailsList(List<PurchaseOrderDetailsEntity> orderDetails);

    @Query("SELECT * FROM PurchaseOrderDetailsTable WHERE orderId=:orderId")
    List<PurchaseOrderDetailsEntity> getAllItemsInOrder(long orderId);

    @Query("SELECT * FROM PurchaseOrderDetailsTable WHERE orderId=:orderId AND itemId=:itemId")
    PurchaseOrderDetailsEntity getSingleItem(long orderId, Integer itemId);

    @Query("DELETE FROM PurchaseOrderDetailsTable WHERE orderId=:orderId AND itemId=:itemId")
    void deleteItem(long orderId, Integer itemId);

}
