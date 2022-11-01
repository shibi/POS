package com.rpos.pos.data.local;

import androidx.room.Database;
import androidx.room.TypeConverters;
import androidx.room.RoomDatabase;

import com.rpos.pos.data.local.dao.CategoryDao;
import com.rpos.pos.data.local.dao.CompanyAddressDao;
import com.rpos.pos.data.local.dao.CompanyDetailsDao;
import com.rpos.pos.data.local.dao.CustomerDao;
import com.rpos.pos.data.local.dao.InvoiceDao;
import com.rpos.pos.data.local.dao.ItemDao;
import com.rpos.pos.data.local.dao.ItemPriceListDao;
import com.rpos.pos.data.local.dao.OrderDetailsDao;
import com.rpos.pos.data.local.dao.OrdersDao;
import com.rpos.pos.data.local.dao.PaymenModeDao;
import com.rpos.pos.data.local.dao.PriceListDao;
import com.rpos.pos.data.local.dao.PurchaseInvoiceDao;
import com.rpos.pos.data.local.dao.PurchaseOrderDao;
import com.rpos.pos.data.local.dao.PurchaseOrderDetailsDao;
import com.rpos.pos.data.local.dao.SupplierDao;
import com.rpos.pos.data.local.dao.TaxesDao;
import com.rpos.pos.data.local.dao.UomDao;
import com.rpos.pos.data.local.entity.CategoryEntity;
import com.rpos.pos.data.local.entity.CompanyAddressEntity;
import com.rpos.pos.data.local.entity.CompanyEntity;
import com.rpos.pos.data.local.entity.CustomerEntity;
import com.rpos.pos.data.local.entity.InvoiceEntity;
import com.rpos.pos.data.local.entity.InvoiceItemHistory;
import com.rpos.pos.data.local.entity.ItemEntity;
import com.rpos.pos.data.local.entity.ItemPriceEntity;
import com.rpos.pos.data.local.entity.OrderDetailsEntity;
import com.rpos.pos.data.local.entity.OrderEntity;
import com.rpos.pos.data.local.entity.PaymentModeEntity;
import com.rpos.pos.data.local.entity.PriceListEntity;
import com.rpos.pos.data.local.entity.PurchaseInvoiceEntity;
import com.rpos.pos.data.local.entity.PurchaseInvoiceItemHistory;
import com.rpos.pos.data.local.entity.PurchaseOrderDetailsEntity;
import com.rpos.pos.data.local.entity.PurchaseOrderEntity;
import com.rpos.pos.data.local.entity.SupplierEntity;
import com.rpos.pos.data.local.entity.TaxSlabEntity;
import com.rpos.pos.data.remote.dto.Customer;
import com.rpos.pos.data.remote.dto.uom.list.UomItem;
import com.rpos.pos.presentation.ui.settings.company_address.CompanyAddressActivity;


@Database(entities = {ItemEntity.class,
        OrderEntity.class, OrderDetailsEntity.class,
        CategoryEntity.class, UomItem.class,
        InvoiceEntity.class, PaymentModeEntity.class,
        CustomerEntity.class, TaxSlabEntity.class,
        CompanyEntity.class, InvoiceItemHistory.class,
        SupplierEntity.class , PurchaseOrderEntity.class,
        PurchaseOrderDetailsEntity.class, PriceListEntity.class,
        ItemPriceEntity.class, PurchaseInvoiceEntity.class,
        PurchaseInvoiceItemHistory.class , CompanyAddressEntity.class }, version = 31, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract ItemDao itemDao();
    public abstract OrdersDao ordersDao();
    public abstract CategoryDao categoryDao();
    public abstract UomDao uomDao();
    public abstract OrderDetailsDao orderDetailsDao();
    public abstract InvoiceDao invoiceDao();
    public abstract PaymenModeDao paymentModeDao();
    public abstract CustomerDao customerDao();
    public abstract TaxesDao taxesDao();
    public abstract CompanyDetailsDao companyDetailsDao();
    public abstract SupplierDao supplierDao();
    public abstract PurchaseOrderDao purchaseOrderDao();
    public abstract PurchaseOrderDetailsDao purchaseOrderDetailsDao();
    public abstract PriceListDao priceListDao();
    public abstract ItemPriceListDao itemPriceListDao();
    public abstract PurchaseInvoiceDao purchaseInvoiceDao();
    public abstract CompanyAddressDao companyAddressDao();

}
