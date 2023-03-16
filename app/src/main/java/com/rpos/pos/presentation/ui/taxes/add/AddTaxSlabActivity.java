package com.rpos.pos.presentation.ui.taxes.add;


import android.util.Log;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.TaxSlabEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.tax.add.AddTaxMessage;
import com.rpos.pos.data.remote.dto.tax.add.AddTaxResponse;
import com.rpos.pos.data.remote.dto.tax.list.TaxData;
import com.rpos.pos.data.remote.dto.tax.list.TaxListResponse;
import com.rpos.pos.data.remote.dto.tax.list.TaxMessage;
import com.rpos.pos.domain.requestmodel.tax.add.AddTaxRequest;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTaxSlabActivity extends SharedActivity {

    private AppCompatEditText et_slabName,et_amount;
    private AppCompatButton btn_save;

    private AppDialogs progressDialog;
    private AppExecutors appExecutors;
    private AppDatabase localDb;

    private LinearLayout ll_back;

    @Override
    public int setUpLayout() {
        return R.layout.activity_add_tax_slab;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        ll_back = findViewById(R.id.ll_back);
        et_slabName = findViewById(R.id.et_tax_name);
        et_amount = findViewById(R.id.et_tax_amount);
        btn_save = findViewById(R.id.btn_save);
        progressDialog = new AppDialogs(this);

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        //BACK PRESS
        ll_back.setOnClickListener(view -> onBackPressed());

        //on save
        btn_save.setOnClickListener(view -> onClickSave());

    }

    @Override
    public void initObservers() {

    }

    private void onClickSave(){
        try {

            if(validate()){
                saveTaxApiCall();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveTaxApiCall(){
        try {

            showProgress();

            final String slabName = et_slabName.getText().toString();
            final String str_taxpercent= et_amount.getText().toString();

            AddTaxRequest params = new AddTaxRequest();
            params.setSlabName(slabName);
            params.setTaxPercentage(str_taxpercent);

            //value required while locally saving tax after api call success.
            float taxPercent = Float.parseFloat(str_taxpercent);

            ApiService api = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            Call<AddTaxResponse> call = api.addNewTaxSlab(params);
            call.enqueue(new Callback<AddTaxResponse>() {
                @Override
                public void onResponse(Call<AddTaxResponse> call, Response<AddTaxResponse> response) {
                    Log.e("-----------","APO"+response.isSuccessful());
                    hideProgress();
                    try {
                        //check response success
                        if(response.isSuccessful()){
                            //get response
                            AddTaxResponse taxListResponse = response.body();
                            if(taxListResponse!= null && taxListResponse.getMessage()!= null){
                                AddTaxMessage taxMessage = taxListResponse.getMessage();
                                if(taxMessage.getSuccess()){
                                    if(taxMessage.getData() !=null){
                                        saveTaxLocally(taxMessage.getData().getTaxSlabId(), slabName, taxPercent);
                                    }else {
                                        finish();
                                    }
                                    return;
                                }
                            }
                        }

                        showToast(getString(R.string.please_check_internet), AddTaxSlabActivity.this);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AddTaxResponse> call, Throwable t) {
                    t.printStackTrace();
                    hideProgress();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveTaxLocally(int tax_slab_id, String taxSlabName, float taxPercent){
        try {

            AppExecutors appExecutors = new AppExecutors();
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        TaxData tax = new TaxData();
                        tax.setTaxSlabId(tax_slab_id);
                        tax.setSlabName(taxSlabName);
                        tax.setTaxPercentage(taxPercent);

                        //add the new items
                        localDb.taxesDao().insertSingleTax(tax);

                        showSuccessInUiThread();

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
     * to show success in ui thread.
     * */
    private void showSuccessInUiThread(){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        AppDialogs appDialogs = new AppDialogs(AddTaxSlabActivity.this);
                        appDialogs.showCommonSuccessDialog(getString(R.string.tax_add_success), view -> finish());

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
    }

    /**
     * validate fields
     * */
    private boolean validate(){
        try {

            String slabName = et_slabName.getText().toString();
            String slabAmount = et_amount.getText().toString();

            if(slabName.isEmpty()){
                et_slabName.setError(getString(R.string.enter_name));
                return false;
            }

            if(slabAmount.isEmpty()){
                et_amount.setError(getString(R.string.enter_amount));
                return false;
            }

            return true;

        }catch (Exception e){
            return false;
        }
    }

    /**
     * to show and hide progress
     * */
    private void showProgress(){
        progressDialog.showProgressBar();
    }
    private void hideProgress(){
        progressDialog.hideProgressbar();
    }
}