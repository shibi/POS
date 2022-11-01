package com.rpos.pos.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.rpos.pos.data.local.entity.InvoiceEntity;
import com.rpos.pos.data.local.entity.InvoiceItemHistory;
import java.util.List;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface InvoiceDao {

    @Insert(onConflict = REPLACE)
    long insertInvoice(InvoiceEntity invoice);

    @Query("SELECT * FROM invoiceTable")
    List<InvoiceEntity> getAllInvoices();

    @Query("SELECT * FROM invoiceTable WHERE id=:invoiceId")
    InvoiceEntity getInvoiceWithId(Integer invoiceId);

    @Insert(onConflict = REPLACE)
    void insertInvoiceItems(List<InvoiceItemHistory> invoiceItemList);

    @Query("SELECT * FROM InvoiceItemsTable WHERE invoiceId=:invoice_id")
    List<InvoiceItemHistory> getInvoiceItemsWithId(int invoice_id);

}
