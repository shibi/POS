package com.rpos.pos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.rpos.pos.data.local.entity.OrderEntity;
import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface OrdersDao {

    @Insert(onConflict = REPLACE)
    long insertOrder(OrderEntity order);

    @Query("SELECT * FROM OrdersTable")
    List<OrderEntity> getAllOrders();

    @Query("SELECT * FROM OrdersTable WHERE id=:orderId")
    OrderEntity getOrderWithId(int orderId);

    @Query("SELECT COUNT(*) FROM OrdersTable WHERE status=:orderStatus")
    int getOrdersWithStatus(String orderStatus);

    @Query("DELETE FROM OrdersTable WHERE id=:orderId")
    void deleteOrderWithId(int orderId);

    @Delete
    void deleteOrder(OrderEntity order);

}
