package com.rpos.pos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.rpos.pos.data.local.entity.PurchaseInvoiceEntity;
import com.rpos.pos.data.local.entity.PurchaseInvoiceItemHistory;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PurchaseInvoiceDao {

    @Insert(onConflict = REPLACE)
    long insertInvoice(PurchaseInvoiceEntity invoice);

    @Query("SELECT * FROM PurchaseInvoiceTable")
    List<PurchaseInvoiceEntity> getAllInvoices();

    @Query("SELECT * FROM PurchaseInvoiceTable WHERE id=:invoiceId")
    PurchaseInvoiceEntity getInvoiceWithId(Integer invoiceId);

    @Insert(onConflict = REPLACE)
    void insertInvoiceItems(List<PurchaseInvoiceItemHistory> invoiceItemList);

    @Query("SELECT * FROM PurchaseInvoiceItemsTable WHERE invoiceId=:invoice_id")
    List<PurchaseInvoiceItemHistory> getInvoiceItemsWithId(int invoice_id);

}
