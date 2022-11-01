package com.rpos.pos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.rpos.pos.data.local.entity.OrderDetailsEntity;
import java.util.List;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface OrderDetailsDao {

    @Insert(onConflict = REPLACE)
    long insertOrderDetail(OrderDetailsEntity orderDetails);

    @Insert(onConflict = REPLACE)
    void insertOrderDetailsList(List<OrderDetailsEntity> orderDetails);

    @Query("SELECT * FROM OrderDetailsTable WHERE orderId=:orderId")
    List<OrderDetailsEntity> getAllItemsInOrder(long orderId);

    @Query("SELECT * FROM OrderDetailsTable WHERE orderId=:orderId AND itemId=:itemId")
    OrderDetailsEntity getSingleItem(long orderId, Integer itemId);

    @Query("DELETE FROM OrderDetailsTable WHERE orderId=:orderId AND itemId=:itemId")
    void deleteItem(long orderId, Integer itemId);

}
