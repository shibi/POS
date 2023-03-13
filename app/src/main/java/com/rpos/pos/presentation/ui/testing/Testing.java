package com.rpos.pos.presentation.ui.testing;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.CategoryEntity;
import com.rpos.pos.data.local.entity.CustomerEntity;
import com.rpos.pos.data.local.entity.ItemEntity;
import com.rpos.pos.data.local.entity.PaymentModeEntity;
import com.rpos.pos.data.local.entity.PriceListEntity;
import com.rpos.pos.data.local.entity.TaxSlabEntity;
import com.rpos.pos.data.remote.dto.uom.list.UomItem;
import com.rpos.pos.domain.utils.DateTimeUtils;

public class Testing {

    private AppExecutors appExecutors;
    private AppDatabase localDb;

    public Testing(AppExecutors appExecutors, AppDatabase localDb) {
        this.appExecutors = appExecutors;
        this.localDb = localDb;
    }

    public void insetSampleData(){

        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try{

                    localDb.uomDao().insertSingleUom(getSampleUnitEntity(1,"kilo"));

                    localDb.paymentModeDao().insertPaymentMode(getSamplePaymentMode("Cash", ""+Constants.PAY_TYPE_CASH));

                    localDb.taxesDao().insertTaxes(getTaxSlabData("Vat 10",10.0f));

                    CategoryEntity categoryEntity = getCategory("Burger");

                    //localDb.categoryDao().insertCategory(categoryEntity);

                    localDb.itemDao().insertItem(getAnItem("Veg burger","delicious",200.0f,20, categoryEntity.getCategoryName(), "1",1));

                    localDb.itemDao().insertItem(getAnItem("Chicken burger","delicious",200.0f,20, categoryEntity.getCategoryName(), "1",1));

                    localDb.customerDao().insertCustomer(getNewCustomer("Customer1","9876543210"));

                    localDb.priceListDao().insertItem(getPriceListEntity("Buy", "$", Constants.BUYING, "$"));
                    localDb.priceListDao().insertItem(getPriceListEntity("Sell", "$", Constants.SELLING, "$"));


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private UomItem getSampleUnitEntity(int id, String unitName){
        UomItem uomItem = new UomItem();
        uomItem.setUomId(id);
        uomItem.setUomName(unitName);
        return uomItem;
    }
    private PaymentModeEntity getSamplePaymentMode(String payModeName, String payType){
        PaymentModeEntity paymentModeEntity = new PaymentModeEntity();
        paymentModeEntity.setPaymentModeName(payModeName);
        paymentModeEntity.setType(payType);
        paymentModeEntity.setCreatedOn(DateTimeUtils.getCurrentDateTime());
        paymentModeEntity.setStatus(Constants.ACTIVE);
        return paymentModeEntity;
    }
    private TaxSlabEntity getTaxSlabData(String name,float amount){
        TaxSlabEntity taxSlabEntity = new TaxSlabEntity();
        taxSlabEntity.setTaxSlabName(name);
        taxSlabEntity.setSlabAmount(amount);
        return taxSlabEntity;
    }
    private CategoryEntity getCategory(String cat_name){
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setCategoryId(cat_name);
        categoryEntity.setCategoryName(cat_name);
        categoryEntity.setCategoryStatus(Constants.ACTIVE);
        return categoryEntity;
    }
    private ItemEntity getAnItem(String name, String description, float rate, int stock, String category,String uomId,int maintainSTock){
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setItemName(name);
        itemEntity.setDescription(description);
        itemEntity.setRate(rate);
        itemEntity.setMaintainStock(maintainSTock);
        itemEntity.setCategory(category);
        itemEntity.setAvailableQty(stock);
        itemEntity.setUom(uomId);
        return itemEntity;
    }
    private CustomerEntity getNewCustomer(String customerName, String mobile){
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setCustomerId(customerName);
        customerEntity.setCustomerName(customerName);
        customerEntity.setMobileNo(mobile);
        return customerEntity;
    }

    private PriceListEntity getPriceListEntity(String name, String currencySybol, int type,String countryCode){
        PriceListEntity priceListEntity = new PriceListEntity();
        priceListEntity.setPriceListName(name);
        priceListEntity.setPriceType(type);
        priceListEntity.setCurrencySymbol(currencySybol);
        priceListEntity.setCountryCode(countryCode);
        return priceListEntity;
    }



}
