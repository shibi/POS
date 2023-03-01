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
import com.rpos.pos.data.remote.dto.payment_modes.PaymentModesListResponse;
import com.rpos.pos.data.remote.dto.royalty.RoyaltyProgram;
import com.rpos.pos.data.remote.dto.royalty.RoyaltyProgramListResponse;
import com.rpos.pos.data.remote.dto.sales.add.AddSalesInvoiceResponse;
import com.rpos.pos.data.remote.dto.sales.list.SalesListResponse;
import com.rpos.pos.data.remote.dto.suppliers.add.AddSupplierResponse;
import com.rpos.pos.data.remote.dto.suppliers.list.GetSuppliersListResponse;
import com.rpos.pos.data.remote.dto.uom.list.GetUomListResponse;

import com.rpos.pos.domain.requestmodel.RequestWithUserId;
import com.rpos.pos.domain.requestmodel.category.add.AddCategoryRequest;
import com.rpos.pos.domain.requestmodel.category.details.CategoryDetailsRequest;
import com.rpos.pos.domain.requestmodel.customer.add.AddCustomerRequest;
import com.rpos.pos.domain.requestmodel.customer.details.CustomerDetailsRequest;
import com.rpos.pos.domain.requestmodel.item.add.AddItemRequest;
import com.rpos.pos.domain.requestmodel.item.getlist.GetItemListRequest;
import com.rpos.pos.domain.requestmodel.login.LoginRequestJson;
import com.rpos.pos.domain.requestmodel.sales.add.AddSalesRequest;
import com.rpos.pos.domain.requestmodel.sales.view.InvoiceViewRequest;
import com.rpos.pos.domain.requestmodel.shift.ShiftOpenRequestJson;
import com.rpos.pos.domain.requestmodel.supplier.add.AddSupplierRequest;
import com.rpos.pos.data.remote.dto.shift.ShiftOpenResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
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

    @POST("custom_integration.api.pos_opening_entry.shift_open")
    Call<ShiftOpenResponse> openShift(@Body ShiftOpenRequestJson shiftOpenRequest);

    @POST("custom_integration.api.payment_mode.payment_mode_list")
    Call<PaymentModesListResponse> getAllPaymentModeList();

    @GET("custom_integration.api.loyalty_program.loyalty_program_list")
    Call<RoyaltyProgramListResponse> getRoyaltyProgramsList();

    @POST("custom_integration.api.invoice.sales_list")
    Call<SalesListResponse> getAllInvoicesList(@Body RequestWithUserId params);

    @POST("custom_integration.api.invoice.view_invoice")
    Call<SalesListResponse> invoiceDetails(@Body InvoiceViewRequest params);

    @POST("custom_integration.api.invoice.add_invoice")
    Call<AddSalesInvoiceResponse> addSalesInvoice(@Body AddSalesRequest params);


    @GET("https://api-fatoora.herokuapp.com/to_base64?")
    Call<ZatcaResponse> generate(@QueryMap Map<String, String> params);

}
