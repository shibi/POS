package com.rpos.pos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.rpos.pos.data.local.entity.OrderEntity;
import com.rpos.pos.data.local.entity.PurchaseOrderEntity;
import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PurchaseOrderDao {

    @Insert(onConflict = REPLACE)
    long insertOrder(PurchaseOrderEntity order);

    @Query("SELECT * FROM PurchaseOrderTable")
    List<PurchaseOrderEntity> getAllOrders();

    @Query("SELECT * FROM PurchaseOrderTable WHERE id=:orderId")
    PurchaseOrderEntity getOrderWithId(int orderId);

    @Query("SELECT COUNT(*) FROM PurchaseOrderTable WHERE status=:orderStatus")
    int getOrdersWithStatus(String orderStatus);

    @Query("DELETE FROM PurchaseOrderTable WHERE id=:orderId")
    void deleteOrderWithId(int orderId);

    @Delete
    void deleteOrder(PurchaseOrderEntity order);
}
