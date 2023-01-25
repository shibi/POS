package com.rpos.pos.presentation.ui.customer.details;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatEditText;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.CustomerEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.customer.details.CreditLimit;
import com.rpos.pos.data.remote.dto.customer.details.CustomerDetailsResponse;
import com.rpos.pos.data.remote.dto.customer.list.CustomerData;
import com.rpos.pos.domain.requestmodel.customer.details.CustomerDetailsRequest;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerDetailsActivity extends SharedActivity {

    private AppDialogs progressDialog;
    private AppCompatEditText et_customerName,et_taxid,et_creditLimit,et_creditDays,et_loyalpid,et_mobile,et_email,et_address;
    private String customerId;

    private LinearLayout ll_back;
    private LinearLayout ll_update;

    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private CustomerEntity customerEntity;

    @Override
    public int setUpLayout() {
        return R.layout.activity_customer_details;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        progressDialog = new AppDialogs(this);

        et_customerName = findViewById(R.id.et_customerName);
        et_taxid = findViewById(R.id.et_taxid);
        et_creditLimit = findViewById(R.id.et_creditlimit);
        et_creditDays = findViewById(R.id.et_creditDays);
        et_loyalpid = findViewById(R.id.et_loyalityPid);
        et_mobile = findViewById(R.id.et_mobile);
        et_email = findViewById(R.id.et_email);
        et_address = findViewById(R.id.et_address);

        ll_back = findViewById(R.id.ll_back);
        ll_update = findViewById(R.id.ll_update);

        customerId = "";
        Intent intent = getIntent();
        if(intent!=null){
            customerId = intent.getStringExtra(Constants.CUSTOMER_ID);
        }

        if(customerId==null || customerId.isEmpty()){
            customerId= "";
            showToast(getString(R.string.invalid_customer));
            return;
        }

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();


        //back press
        ll_back.setOnClickListener(view -> onBackPressed());

        ll_update.setOnClickListener(view -> {
            onSaveClick();
        });

        getCustomerDetails(customerId);
        //getCustomerDetailsFromLocalDb(customerId);

    }

    @Override
    public void initObservers() {

    }

    /**
     * get the details of the customer
     */
    private void getCustomerDetailsFromLocalDb(String custId){
        try {

            appExecutors.diskIO().execute(() -> {
                try {

                    customerEntity = localDb.customerDao().getCustomerWithId(custId);
                    updateUi();

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To validate fields before save
     * */
    private boolean validate(){
        try {

            String customerName = et_customerName.getText().toString();
            String customerTaxid = et_taxid.getText().toString();
            String str_creditLimit = et_creditLimit.getText().toString();
            String str_creditDays = et_creditDays.getText().toString();
            String loyality_pid = et_loyalpid.getText().toString();
            String mobile = et_mobile.getText().toString();
            String email = et_email.getText().toString();
            String address = et_address.getText().toString();

            if(customerName.isEmpty()){
                showError(et_customerName, "Enter customer name");
                return false;
            }

            if(customerTaxid.isEmpty()){
                customerTaxid = "EMPTY";
            }


            /*if(str_creditLimit.isEmpty()){
                showError(et_creditLimit, "Enter credit limit");
                return false;
            }

            if(str_creditDays.isEmpty()){
                showError(et_creditDays, "Enter credit days");
                return false;
            }*/

            int creditLimit = 0;
            if(!str_creditLimit.isEmpty()) {
                creditLimit = Integer.parseInt(str_creditLimit);
            }

            int creditDays = 0;
            if(!str_creditDays.isEmpty()){
                creditDays = Integer.parseInt(str_creditDays);
            }



            loyality_pid = "Test loyalty Program";
            /*if(loyality_pid.isEmpty()){
                showError(et_loyalpid, "Enter Loyality program id");
                return false;
            }*/

            if(mobile.isEmpty()){
                showError(et_mobile, "Enter mobile");
                return false;
            }

            if(mobile.length()<9){
                showError(et_mobile, "Enter valid mobile");
                return false;
            }

            if(email.isEmpty()){
                email = "empty@gmail.com";
            }

            if(address.isEmpty()){
                address = Constants.NONE;
            }

            customerEntity.setCustomerName(customerName);
            customerEntity.setTaxId(customerTaxid);
            customerEntity.setMobileNo(mobile);
            customerEntity.setCreditLimit(creditLimit);
            customerEntity.setCreditDays(creditDays);
            customerEntity.setLoyaltyProgram(loyality_pid);
            customerEntity.setEmail(email);
            customerEntity.setAddress(address);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void showError(EditText inputField, String msg){
        inputField.setError(msg);
        inputField.requestFocus();
        showToast(msg);
    }

    /**
     * Details data from api
     * */
    private void getCustomerDetails(String customerId){
        try{

            progressDialog.showProgressBar();

            ApiService apiService = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            CustomerDetailsRequest params = new CustomerDetailsRequest();
            params.setCustomerId(customerId);

            Call<CustomerDetailsResponse> call = apiService.getCustomerDetails(params);
            call.enqueue(new Callback<CustomerDetailsResponse>() {
                @Override
                public void onResponse(Call<CustomerDetailsResponse> call, Response<CustomerDetailsResponse> response) {
                    try {

                        progressDialog.hideProgressbar();

                        if(response.isSuccessful()) {

                            CustomerDetailsResponse customerDetailsResponse = response.body();

                            if (customerDetailsResponse != null) {
                                CustomerData customerData = customerDetailsResponse.getMessage();
                                if (customerData != null) {
                                    et_customerName.setText(customerData.getCustomerName());
                                    et_taxid.setText(customerData.getTaxId());

                                    et_mobile.setVisibility(View.GONE);
                                    et_creditLimit.setVisibility(View.GONE);
                                    et_creditDays.setVisibility(View.GONE);
                                    et_email.setVisibility(View.GONE);
                                    et_address.setVisibility(View.GONE);

                                    /*List<CreditLimit> creditLimitList = customerData.getCreditLimit();
                                    if (creditLimitList != null && creditLimitList.size() > 0) {
                                        CreditLimit creditDetails = creditLimitList.get(0);
                                        et_creditLimit.setText("" + creditDetails.getCreditLimit());
                                        et_creditDays.setText("" + creditDetails.getCreditDays());
                                    }*/
                                    return;
                                }
                            }
                        }

                        showToast(getString(R.string.customer_details_failed));

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<CustomerDetailsResponse> call, Throwable t) {
                    showToast(getString(R.string.customer_details_failed));
                    progressDialog.hideProgressbar();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * on save click
     * */
    private void onSaveClick(){
        try {

            if(customerEntity ==null){
                showToast(getString(R.string.invalid_deteail_refresh));
                return;
            }

            //check fields are valid
            if(validate()){
                saveCustomerToLocalDb(customerEntity);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * save customer
     * */
    private void saveCustomerToLocalDb(CustomerEntity customer){
        try {

            appExecutors.diskIO().execute(() -> {
                try {

                    localDb.customerDao().insertCustomer(customer);
                    runOnUiThread(this::showSuccess);

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showSuccess(){
        try {

            AppDialogs appDialogs = new AppDialogs(CustomerDetailsActivity.this);
            appDialogs.showCommonSuccessDialog(getString(R.string.customer_update_success), view -> {
                try{

                    setResult(RESULT_OK);
                    finish();

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to update details in ui
     * using ui thread
     * */
    private void updateUi(){
        try {

            runOnUiThread(() -> {
                try {

                    et_customerName.setText(customerEntity.getCustomerName());
                    et_mobile.setText(customerEntity.getMobileNo());
                    et_email.setText(customerEntity.getEmail());
                    et_taxid.setText(customerEntity.getTaxId());
                    et_creditLimit.setText(""+customerEntity.getCreditLimit());
                    et_creditDays.setText(""+customerEntity.getCreditDays());
                    et_loyalpid.setText(customerEntity.getLoyaltyProgram());
                    et_address.setText(customerEntity.getAddress());

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showToast(String msg){
        showToast(msg, this);
    }

}