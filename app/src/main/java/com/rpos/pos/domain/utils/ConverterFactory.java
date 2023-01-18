package com.rpos.pos.domain.utils;

import com.rpos.pos.data.local.entity.CategoryEntity;
import com.rpos.pos.data.local.entity.CustomerEntity;
import com.rpos.pos.data.remote.dto.category.list.CategoryItem;
import com.rpos.pos.data.remote.dto.customer.list.CustomerData;

public class ConverterFactory {

    public static CustomerEntity convertToEntity(CustomerData customerData){
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setCustomerId(customerData.getCustomerId());
        customerEntity.setCustomerName(customerData.getCustomerName());
        customerEntity.setTaxId(customerData.getCustomerId());
        if(customerData.getCreditLimit()!=null && customerData.getCreditLimit().size()>0) {
            float creditLimit = customerData.getCreditLimit().get(0).getCreditLimit();
            customerEntity.setCreditLimit((int) creditLimit);
        }

        return customerEntity;
    }

    public static CategoryEntity convertToCategoryEntity(CategoryItem category){
        CategoryEntity entity = new CategoryEntity();
        entity.setCategoryId(category.getCategoryId());
        entity.setCategoryName(category.getCategory());
        return entity;
    }


}