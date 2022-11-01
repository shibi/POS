package com.rpos.pos.presentation.ui.customer.addcustomer;

import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatEditText;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.CustomerEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.customer.add.AddCustomerMessage;
import com.rpos.pos.data.remote.dto.customer.add.AddCustomerResponse;
import com.rpos.pos.domain.requestmodel.customer.add.AddCustomerRequest;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCustomerActivity extends SharedActivity {

    private AppDialogs progressDialog;
    private AppCompatEditText et_customerName,et_taxid,et_creditLimit,et_creditDays,et_loyalpid,et_mobile,et_email,et_address;
    private LinearLayout ll_back;
    private LinearLayout ll_submit;

    private AppExecutors appExecutors;
    private AppDatabase localDb;

    private List<CustomerEntity> customerList;
    private CustomerEntity customerEntity;

    @Override
    public int setUpLayout() {
        return R.layout.activity_add_customer;
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
        ll_submit = findViewById(R.id.ll_add_customer);

        customerList = new ArrayList<>();

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();


        //on save
        ll_submit.setOnClickListener(view -> {

            //onClickSave();  //Method api
            onSaveClick();
        });


        //back press
        ll_back.setOnClickListener(view -> onBackPressed());

        //get customer list
        getCustomerListFromDb();
    }

    @Override
    public void initObservers() {

    }

    /**
     * on save click
     * */
    private void onSaveClick(){
        try {

            //check fields are valid
            if(validate()){
                saveCustomerToLocalDb(customerEntity);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * get customer list from db
     * */
    private void getCustomerListFromDb(){
        try {

            appExecutors.diskIO().execute(() -> {
                try {
                    customerList = localDb.customerDao().getAllCustomer();
                }catch (Exception e){
                    e.printStackTrace();
                }
            });

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

                    //To check mobile number already exists,
                    //find customer with mobile number
                    CustomerEntity existingCustomer = localDb.customerDao().findCustomerWithMobile(customer.getMobileNo());
                    //check customer exists
                    if(existingCustomer!=null){
                        //show mobile number exists message
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showError(et_mobile, getString(R.string.mobile_exists));
                            }
                        });
                        return;
                    }

                    //N.B autoincrement is false , so
                    //need to get the id of last entry from table
                    //then increment id to save the new entry
                    int id;
                    if(customerList!=null && !customerList.isEmpty()){
                        String str_id = customerList.get(customerList.size()-1).getCustomerId();
                        id = Integer.parseInt(str_id);
                        //increment for next
                        id++;
                    }else {
                        id = 1;
                    }

                    customer.setCustomerId(""+id);
                    localDb.customerDao().insertCustomer(customer);
                    runOnUiThread(() -> showSuccess());

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

            AppDialogs appDialogs = new AppDialogs(AddCustomerActivity.this);
            appDialogs.showCommonSuccessDialog(getString(R.string.customer_add_success), view -> {
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
     * To validate fields before save
     * */
    private boolean validate(){
        try {

            String customerName = et_customerName.getText().toString().trim();
            String customerTaxid = et_taxid.getText().toString();
            String str_creditLimit = et_creditLimit.getText().toString();
            String str_creditDays = et_creditDays.getText().toString();
            String loyality_pid = et_loyalpid.getText().toString();
            String mobile = et_mobile.getText().toString().trim();
            String email = et_email.getText().toString().trim();
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

            customerEntity = new CustomerEntity();
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

    private void onClickSave(){
        try {

            String customerName = et_customerName.getText().toString();
            String customerTaxid = et_taxid.getText().toString();
            String str_creditLimit = et_creditLimit.getText().toString();
            String str_creditDays = et_creditDays.getText().toString();
            String loyality_pid = et_loyalpid.getText().toString();
            String mobile = et_mobile.getText().toString();
            String email = et_email.getText().toString();

            if(customerName.isEmpty()){
                showError(et_customerName, "Enter customer name");
                return;
            }

            if(customerTaxid.isEmpty()){
                showError(et_taxid, "Enter tax id");
                return;
            }

            if(str_creditLimit.isEmpty()){
                showError(et_creditLimit, "Enter credit limit");
                return;
            }

            if(str_creditDays.isEmpty()){
                showError(et_creditDays, "Enter credit days");
                return;
            }

            if(loyality_pid.isEmpty()){
                showError(et_loyalpid, "Enter Loyality program id");
                return;
            }

            if(mobile.isEmpty()){
                showError(et_mobile, "Enter mobile");
                return;
            }

            if(email.isEmpty()){
                showError(et_email, "Enter email");
                return;
            }


            int creditLimit = Integer.parseInt(str_creditLimit);
            int creditDays = Integer.parseInt(str_creditDays);

            addCustomer(customerName, customerTaxid,creditLimit,creditDays,loyality_pid,mobile,email);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showError(EditText inputField,String msg){
        inputField.setError(msg);
        inputField.requestFocus();
        showToast(msg);
    }

    private void showToast(String msg){
        showToast(msg, AddCustomerActivity.this);
    }

    private void addCustomer(String custName,String taxId,int creditLimit,int creditDays,String loyalityProgramId,String mobile,String email){
        try {

            progressDialog.showProgressBar();

            ApiService api = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY, Constants.API_SECRET);
            AddCustomerRequest request = new AddCustomerRequest();
            request.setCustomerName(custName);
            request.setTaxId(taxId);
            request.setCreditLimit(creditLimit);
            request.setCreditDays(creditDays);
            request.setLoyaltyProgram(loyalityProgramId);//"Test Loyalty Program");
            request.setMobileNo(mobile);
            request.setEmail(email);


            Call<AddCustomerResponse> call = api.addCustomer(request);
            call.enqueue(new Callback<AddCustomerResponse>() {
                @Override
                public void onResponse(Call<AddCustomerResponse> call, Response<AddCustomerResponse> response) {
                    Log.e("-------------","res"+response.isSuccessful());
                    progressDialog.hideProgressbar();
                    try {

                        if(response.isSuccessful()){

                            AddCustomerResponse addCustomerResponse = response.body();
                            if(addCustomerResponse!=null){
                                AddCustomerMessage addCustomerMessage = addCustomerResponse.getMessage();
                                if(addCustomerMessage!=null){

                                    if(addCustomerMessage.getSuccess()){
                                        showToast(getString(R.string.customer_add_success));
                                        finish();
                                    }else {
                                        showToast(addCustomerMessage.getMessage());
                                    }

                                }else {
                                    showToast(getString(R.string.cust_reg_failed));
                                }
                            }else {
                                showToast(getString(R.string.cust_reg_failed));
                            }

                        }else {
                            showToast(getString(R.string.cust_reg_failed));
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        showToast(getString(R.string.cust_reg_failed));
                    }
                }

                @Override
                public void onFailure(Call<AddCustomerResponse> call, Throwable t) {
                    Log.e("-------------","fail");
                    progressDialog.hideProgressbar();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}