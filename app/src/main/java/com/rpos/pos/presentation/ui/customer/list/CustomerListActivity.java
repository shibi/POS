package com.rpos.pos.presentation.ui.customer.list;

import android.content.Intent;
import android.widget.LinearLayout;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.RecyclerView;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.CustomerEntity;
import com.rpos.pos.data.local.entity.PaymentModeEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.Customer;
import com.rpos.pos.data.remote.dto.customer.list.CustomerData;
import com.rpos.pos.data.remote.dto.customer.list.CustomerListResponse;
import com.rpos.pos.domain.models.item.PickedItem;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.customer.addcustomer.AddCustomerActivity;
import com.rpos.pos.presentation.ui.customer.details.CustomerDetailsActivity;
import com.rpos.pos.presentation.ui.customer.list.adapter.CustomerListAdapter;
import com.rpos.pos.presentation.ui.paymentmodes.list.PaymentModeListActivity;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerListActivity extends SharedActivity {

    private List<CustomerEntity> customerList;
    private RecyclerView rv_customerList;
    private CustomerListAdapter customerListAdapter;
    private LinearLayout ll_back;
    private AppDialogs progressDialog;
    private LinearLayout ll_add_customer;
    private AppDialogs appDialogs;

    private AppExecutors appExecutors;
    private AppDatabase localDb;

    @Override
    public int setUpLayout() {
        return R.layout.activity_customer_list;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        rv_customerList = findViewById(R.id.rv_customerlist);
        ll_back = findViewById(R.id.ll_back);
        customerList = new ArrayList<>();
        progressDialog = new AppDialogs(this);
        ll_add_customer = findViewById(R.id.ll_add_customer);

        appDialogs = new AppDialogs(CustomerListActivity.this);

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        //adapter
        customerListAdapter = new CustomerListAdapter(CustomerListActivity.this, customerList, new CustomerListAdapter.OnClickCustomer() {
            @Override
            public void onViewCustomer(CustomerEntity customer) {
                gotoCustomerDetailsActivity(customer.getCustomerId());
            }

            @Override
            public void onRemoveCustomer(CustomerEntity customer) {

                promptDeleteConfirmation(customer);
            }
        });
        rv_customerList.setAdapter(customerListAdapter);

        //Back arrow press
        ll_back.setOnClickListener(view -> onBackPressed());

        //add customer
        ll_add_customer.setOnClickListener(view -> {
            try {

                gotoAddCustomerScreen();

            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //get customer list
        //getCustomerList();
        getCustomerListFromDb();

    }



    @Override
    public void initObservers() {

    }

    private void gotoAddCustomerScreen(){
        Intent addCustomerIntent = new Intent(CustomerListActivity.this, AddCustomerActivity.class);
        launchAddCustomerScreen.launch(addCustomerIntent);
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

    /**
     * prompt user to confirm deletion
     * */
    private void promptDeleteConfirmation(CustomerEntity customer){
        try {

            String message = getString(R.string.delete_confirmation);

            appDialogs = new AppDialogs(CustomerListActivity.this);
            appDialogs.showCommonDualActionAlertDialog(getString(R.string.delete_label), message, new AppDialogs.OnDualActionButtonClickListener() {
                @Override
                public void onClickPositive(String id) {
                    try {

                        appExecutors.diskIO().execute(() -> {
                            try {

                                localDb.customerDao().delete(customer);

                                for (int i=0;i < customerList.size();i++){
                                    if(customerList.get(i).getCustomerId().equals(customer.getCustomerId())){
                                        customerList.remove(i);
                                        refreshAfterRemove(i);
                                        break;
                                    }
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        });

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClickNegetive(String id) {

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * refresh adapter position after item remove
     * @param position position of the item removed
     * */
    private void refreshAfterRemove(final int position){
        try {

            runOnUiThread(() -> customerListAdapter.notifyItemRemoved(position));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private ActivityResultLauncher<Intent> launchAddCustomerScreen = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if(result.getResultCode() == RESULT_OK){
            getCustomerListFromDb();
        }
    });

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


    private void gotoCustomerDetailsActivity(String customer_ID){
        Intent intent = new Intent(CustomerListActivity.this, CustomerDetailsActivity.class);
        intent.putExtra(Constants.CUSTOMER_ID, customer_ID);
        startActivity(intent);
    }



}