package com.rpos.pos.presentation.ui.customer.select;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.CustomerEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.customer.list.CustomerData;
import com.rpos.pos.data.remote.dto.customer.list.CustomerListResponse;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.customer.addcustomer.AddCustomerActivity;
import com.rpos.pos.presentation.ui.customer.list.adapter.CustomerListAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerSelectActivity extends SharedActivity {

    private SearchView sv_search;
    private List<CustomerEntity> customerList;
    private RecyclerView rv_customerList;
    private CustomerListAdapter customerListAdapter;
    private LinearLayout ll_back;
    private AppDialogs progressDialog;
    private LinearLayout ll_add_customer;

    private AppExecutors appExecutors;
    private AppDatabase localDb;

    @Override
    public int setUpLayout() {
        return R.layout.activity_customer_select;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        sv_search = findViewById(R.id.sv_search);

        rv_customerList = findViewById(R.id.rv_customerlist);
        ll_back = findViewById(R.id.ll_back);

        ll_add_customer = findViewById(R.id.ll_add_customer);

        customerList = new ArrayList<>();
        progressDialog = new AppDialogs(this);


        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        customerListAdapter = new CustomerListAdapter(CustomerSelectActivity.this, customerList, new CustomerListAdapter.OnClickCustomer() {
            @Override
            public void onViewCustomer(CustomerEntity customer) {
                try{

                    Intent intent = new Intent();
                    intent.putExtra(Constants.CUSTOMER_ID,customer.getCustomerId());
                    intent.putExtra(Constants.CUSTOMER_NAME,customer.getCustomerName());
                    intent.putExtra(Constants.CUSTOMER_TAXID,customer.getTaxId());
                    intent.putExtra(Constants.CUSTOMER_PHONE,customer.getMobileNo());

                    setResult(RESULT_OK, intent);
                    finish();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onRemoveCustomer(CustomerEntity customer) {

            }
        });

        rv_customerList.setAdapter(customerListAdapter);

        sv_search.setFocusable(true);
        sv_search.requestFocusFromTouch();

        sv_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                customerListAdapter.getFilter().filter(newText);
                return false;
            }
        });

        //back press
        ll_back.setOnClickListener(view -> onBackPressed());


        ll_add_customer.setOnClickListener(view -> {

            //goto add customer activity
            Intent addCustomerIntent = new Intent(CustomerSelectActivity.this, AddCustomerActivity.class);
            startActivity(addCustomerIntent);

        });

    }

    @Override
    public void initObservers() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        //getCustomerList();
        getCustomerListFromDb();
    }

    /**
     * get customer list from db
     * */
    private void getCustomerListFromDb(){
        try {

            appExecutors.diskIO().execute(() -> {
                try {
                    List<CustomerEntity> tempList = localDb.customerDao().getAllCustomer();
                    if(tempList!=null && !tempList.isEmpty()){
                        customerList.clear();
                        customerList.addAll(tempList);
                        runOnUiThread(() -> customerListAdapter.notifyDataSetChanged());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*private void getCustomerList(){
        try {

            progressDialog.showProgressBar();

            ApiService api = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            Call<CustomerListResponse> call = api.getCustomerList();
            call.enqueue(new Callback<CustomerListResponse>() {
                @Override
                public void onResponse(Call<CustomerListResponse> call, Response<CustomerListResponse> response) {
                    Log.e("------------","res"+response.isSuccessful());
                    try{

                        progressDialog.hideProgressbar();

                        if(response.isSuccessful()){
                            CustomerListResponse customerListResp = response.body();
                            if(customerListResp!=null){
                                List<CustomerData> customerDataList = customerListResp.getMessage();
                                if(customerDataList!=null && customerDataList.size()>0){
                                    customerList.clear();
                                    customerList.addAll(customerDataList);
                                    customerListAdapter.notifyDataSetChanged();
                                }else {

                                }
                            }else {

                            }
                        }else {

                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<CustomerListResponse> call, Throwable t) {
                    Log.e("------------","failed");
                    progressDialog.hideProgressbar();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }*/
}