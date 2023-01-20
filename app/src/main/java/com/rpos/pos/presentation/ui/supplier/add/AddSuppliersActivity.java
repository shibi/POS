package com.rpos.pos.presentation.ui.supplier.add;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatEditText;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.SupplierEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.suppliers.add.AddSupplierMessage;
import com.rpos.pos.data.remote.dto.suppliers.add.AddSupplierResponse;
import com.rpos.pos.domain.requestmodel.supplier.add.AddSupplierRequest;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSuppliersActivity extends SharedActivity {


    private AppCompatEditText et_supplierName,et_taxId,et_creditLimit,et_rate,et_address;
    private LinearLayout ll_back;
    private LinearLayout ll_save;

    private SupplierEntity supplierEntity;

    private AppDatabase localDb;
    private AppExecutors appExecutors;

    //loading view
    private AppDialogs progressDialog;

    @Override
    public int setUpLayout() {
        return R.layout.activity_add_suppliers;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        progressDialog = new AppDialogs(this);

        et_supplierName = findViewById(R.id.et_supplierName);
        et_taxId = findViewById(R.id.et_taxid);
        et_creditLimit = findViewById(R.id.et_creditlimit);
        et_rate = findViewById(R.id.et_rate);
        et_address = findViewById(R.id.et_address);
        ll_save = findViewById(R.id.ll_save);
        ll_back = findViewById(R.id.ll_back);

        localDb = getCoreApp().getLocalDb();
        appExecutors = new AppExecutors();

        supplierEntity = new SupplierEntity();

        //on save click
        //validate user entered values ,then update to server
        ll_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    //validate user inputs
                    if(validateFields()){
                        //if valid ,
                        // call the api to add new supplier
                        addSupplier(et_supplierName.getText().toString().trim(),
                                et_taxId.getText().toString().trim(),et_creditLimit.getText().toString(),
                                et_rate.getText().toString(), et_address.getText().toString());
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //back press
        ll_back.setOnClickListener(view -> onBackPressed());

    }

    @Override
    public void initObservers() {

    }

    /**
     * api call to save new supplier to backend.
     * Not all fields are mandatory. Suppler name is required.
     * */
    private void addSupplier(String supplierName, String taxId, String creditLimit, String rate, String address){
        try {

            //show progress
            progressDialog.showProgressBar();

            //api generate
            ApiService apiService = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY, Constants.API_SECRET);
            AddSupplierRequest params = new AddSupplierRequest();
            params.setSupplierName(supplierName);
            params.setTaxId(taxId);
            //OPTIONAL FIELDS

            Call<AddSupplierResponse> call = apiService.addSupplier(params);
            call.enqueue(new Callback<AddSupplierResponse>() {
                @Override
                public void onResponse(Call<AddSupplierResponse> call, Response<AddSupplierResponse> response) {
                    try {

                        progressDialog.hideProgressbar();

                        if(response.isSuccessful()){

                            AddSupplierResponse addSupplierResponse = response.body();
                            if(addSupplierResponse!=null) {
                                AddSupplierMessage addSupplierMessage = addSupplierResponse.getMessage();
                                if (addSupplierMessage != null) {
                                    if (addSupplierMessage.getSuccess()) {
                                        showToast(getString(R.string.supplier_added_successfully));
                                        finish();
                                        return;
                                    }
                                }
                            }
                        }

                        //show failed message if no condition met
                        showToast(getString(R.string.supplier_add_failed));

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AddSupplierResponse> call, Throwable t) {
                    progressDialog.hideProgressbar();
                    showToast(getString(R.string.supplier_add_failed));
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * to verify user entered values
     * if all user inputs are valid , returns true
     * if any issues found, then returns false at the point of issue
     * */
    private boolean validateFields(){
        try {

            String supplierName = et_supplierName.getText().toString().trim();
            String taxId = et_taxId.getText().toString();
            String creditLimit = et_creditLimit.getText().toString();
            String rate = et_rate.getText().toString();

            float fl_creditLim;
            float fl_rate;

            if(supplierName.isEmpty()){
                showError(et_supplierName, getString(R.string.enter_name));
                return false;
            }

           /* if(taxId.isEmpty()){
                showError(et_taxId, getString(R.string.enter_tax_id));
                return false;
            }*/

            if(creditLimit.isEmpty()){
                //showError(et_creditLimit, getString(R.string.enter_credit_limit));
                //return false;
                fl_creditLim = 0.0f;
            }else {
                fl_creditLim = Float.parseFloat(creditLimit);
            }

            if(rate.isEmpty()){
                //showError(et_rate, getString(R.string.enter_rate));
                //return false;
                fl_rate = 0.0f;
            }else {
                fl_rate = Float.parseFloat(rate);
            }



            supplierEntity.setSupplierName(supplierName);
            supplierEntity.setTaxId(taxId);
            supplierEntity.setCreaditLimit(fl_creditLim);
            supplierEntity.setRate(fl_rate);
            supplierEntity.setAddress(et_address.getText().toString());


            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void showError(AppCompatEditText inputField, String err_msg){
        inputField.setError(err_msg);
        inputField.requestFocus();
    }


    private void addSupplierToLocalDb(){
        try {

            progressDialog.showProgressBar();
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        final long supplierId = localDb.supplierDao().insertSupplier(supplierEntity);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hideProgressbar();


                            }
                        });

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to show success dialog
     * on dismiss the dialog , will
     * */
    private void showSuccessDialog(long _supplierId){

        AppDialogs appDialogs = new AppDialogs(AddSuppliersActivity.this);
        appDialogs.showCommonSuccessDialog(getString(R.string.supplier_add_success), view -> sendResult(_supplierId, supplierEntity.getSupplierName()));

    }

    private void sendResult(long sid,String supplierName){
        try {

            Intent intent = new Intent();
            intent.putExtra(Constants.SUPPLIER_ID, ((int)sid));
            intent.putExtra(Constants.SUPPLIER_NAME, supplierName);
            setResult(RESULT_OK, intent);

            finish();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showToast(String msg){
        showToast(msg, this);
    }
}