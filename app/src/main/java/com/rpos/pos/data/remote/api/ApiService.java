package com.rpos.pos.data.remote.api;

import com.rpos.pos.data.remote.dto.ZatcaResponse;
import com.rpos.pos.data.remote.dto.address.list.GetAddressListResponse;
import com.rpos.pos.data.remote.dto.category.add.AddCategoryResponse;
import com.rpos.pos.data.remote.dto.category.details.CategoryDetailsResponse;
import com.rpos.pos.data.remote.dto.category.list.GetCategoryResponse;
import com.rpos.pos.data.remote.dto.customer.add.AddCustomerResponse;
import com.rpos.pos.data.remote.dto.customer.details.CustomerDetailsResponse;
import com.rpos.pos.data.remote.dto.customer.list.CustomerListResponse;
import com.rpos.pos.data.remote.dto.items.add.AddItemResponse;
import com.rpos.pos.data.remote.dto.items.list.GetItemsListResponse;
import com.rpos.pos.data.remote.dto.login.LoginResponse;
import com.rpos.pos.data.remote.dto.suppliers.add.AddSupplierMessage;
import com.rpos.pos.data.remote.dto.suppliers.add.AddSupplierResponse;
import com.rpos.pos.data.remote.dto.suppliers.list.GetSuppliersListResponse;
import com.rpos.pos.data.remote.dto.uom.list.GetUomListResponse;
import com.rpos.pos.domain.requestmodel.category.add.AddCategoryRequest;
import com.rpos.pos.domain.requestmodel.category.details.CategoryDetailsRequest;
import com.rpos.pos.domain.requestmodel.customer.add.AddCustomerRequest;
import com.rpos.pos.domain.requestmodel.customer.details.CustomerDetailsRequest;
import com.rpos.pos.domain.requestmodel.item.add.AddItemRequest;
import com.rpos.pos.domain.requestmodel.item.getlist.GetItemListRequest;
import com.rpos.pos.domain.requestmodel.login.LoginRequestJson;
import com.rpos.pos.domain.requestmodel.supplier.add.AddSupplierRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface ApiService {


    @POST("custom_integration.api.login")
    Call<LoginResponse> posLogin(@Body LoginRequestJson requestBody);

    @GET("custom_integration.api.category.category_list")
    Call<GetCategoryResponse> getCategoryList();

    @POST("custom_integration.api.category.add_category")
    Call<AddCategoryResponse> addCategory(@Body AddCategoryRequest addCategoryRequest);

    //Modified on 23-08-2022
    @POST("custom_integration.api.items.item_list")
    Call<GetItemsListResponse> getItemsList(@Body GetItemListRequest getItemListRequest);

    @POST("custom_integration.api.category.view_category")
    Call<CategoryDetailsResponse> getCategoryDetails(@Body CategoryDetailsRequest requestBody);

    @GET("custom_integration.api.customer.customer_list")
    Call<CustomerListResponse> getCustomerList();

    @POST("custom_integration.api.customer.view_customer")
    Call<CustomerDetailsResponse> getCustomerDetails(@Body CustomerDetailsRequest requestBody);

    @POST("custom_integration.api.customer.add_customer")
    Call<AddCustomerResponse> addCustomer(@Body AddCustomerRequest addCustomerRequest);

    @GET("custom_integration.api.suppliers.supplier_list")
    Call<GetSuppliersListResponse> getSuppliersList();

    @POST("custom_integration.api.suppliers.add_supplier")
    Call<AddSupplierResponse> addSupplier(@Body AddSupplierRequest addSupplierRequest);

    @GET("custom_integration.api.address.address_list")
    Call<GetAddressListResponse> getAddressList();

    @POST("custom_integration.api.items.add_item")
    Call<AddItemResponse> addItem(@Body AddItemRequest addItemRequest);

    @GET("custom_integration.api.uom.uom_list")
    Call<GetUomListResponse> getUOMList();


    @GET("https://api-fatoora.herokuapp.com/to_base64?")
    Call<ZatcaResponse> generate(@QueryMap Map<String, String> params);

}
