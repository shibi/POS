package com.rpos.pos.presentation.ui.taxes.view;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.gson.internal.$Gson$Preconditions;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.TaxSlabEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.tax.edit.EditTaxMessage;
import com.rpos.pos.data.remote.dto.tax.edit.EditTaxResponse;
import com.rpos.pos.data.remote.dto.tax.list.TaxData;
import com.rpos.pos.data.remote.dto.tax.list.TaxListResponse;
import com.rpos.pos.data.remote.dto.tax.list.TaxMessage;
import com.rpos.pos.domain.requestmodel.tax.edit.EditTaxRequest;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.taxes.add.AddTaxSlabActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaxViewActivity extends SharedActivity {

    private AppCompatEditText et_taxName,et_taxPercent;
    private AppCompatButton btn_update;
    private int taxId;
    private AppDialogs progressDialog;
    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private AppDialogs appDialogs;
    private LinearLayout ll_back;
    private TaxData selectedTaxItem;

    @Override
    public int setUpLayout() {
        return R.layout.activity_tax_view;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        ll_back = findViewById(R.id.ll_back);
        et_taxName = findViewById(R.id.et_tax_name);
        et_taxPercent = findViewById(R.id.et_tax_percent);
        btn_update = findViewById(R.id.btn_update);

        progressDialog = new AppDialogs(this);
        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();
        appDialogs = new AppDialogs(this);

        //default
        taxId = -1;

        Intent data = getIntent();
        if(data!=null){
            taxId = data.getIntExtra(Constants.TAX_ID,-1);
        }

        if(taxId == -1){
            showToast(getString(R.string.invalid_item));
            return;
        }


        //BACK PRESS
        ll_back.setOnClickListener(view -> onBackPressed());

        //update click
        btn_update.setOnClickListener(view -> {
            onUpdateClick();
        });

        getTaxDetails();
    }

    @Override
    public void initObservers() {

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * get tax details
     * */
    private void getTaxDetails(){
        try {

            appExecutors.diskIO().execute(() -> {
                try {

                    selectedTaxItem = localDb.taxesDao().getTaxDetailWithId(taxId);
                    if(selectedTaxItem!=null) {
                        updateUI();
                    }else {
                        showToast(getString(R.string.no_such_item));
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
     * to update data on ui
     * */
    private void updateUI(){
        try{

            runOnUiThread(() -> {
                et_taxName.setText(selectedTaxItem.getSlabName());
                et_taxPercent.setText(""+selectedTaxItem.getTaxPercentage());
            });

        }catch (Exception e){
            throw e;
        }
    }


    private void onUpdateClick(){
        try {

            if(validate()){
                updateTaxDetailsApiCall();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateTaxDetailsApiCall(){
        try {

            showProgress();

            String slabName = et_taxName.getText().toString();
            String slabAmount = et_taxPercent.getText().toString();

            ApiService api = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);

            EditTaxRequest params = new EditTaxRequest();
            params.setTaxSlabId(taxId); // this is important
            params.setSlabName(slabName);
            params.setTaxPercentage(slabAmount);

            Call<EditTaxResponse> call = api.editTaxDetails(params);
            call.enqueue(new Callback<EditTaxResponse>() {
                @Override
                public void onResponse(Call<EditTaxResponse> call, Response<EditTaxResponse> response) {
                    Log.e("-----------","APO"+response.isSuccessful());
                    hideProgress();
                    try {
                        //check response success
                        if(response.isSuccessful()){
                            //get response
                            EditTaxResponse taxListResponse = response.body();
                            if(taxListResponse!= null && taxListResponse.getMessage()!= null){
                                EditTaxMessage editTaxMessage = taxListResponse.getMessage();
                                if(editTaxMessage.getSuccess()){
                                    updateLocallySavedTaxItem(taxId, slabName, slabAmount);
                                }else {
                                    showToast(editTaxMessage.getMessage());
                                }
                                return;
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<EditTaxResponse> call, Throwable t) {
                    Log.e("-----------","failed");
                    hideProgress();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateLocallySavedTaxItem(int tax_id, String slabName, String str_taxPercent){
        try {


            float tax_amount = Float.parseFloat(str_taxPercent);

            selectedTaxItem.setTaxSlabId(tax_id);
            selectedTaxItem.setSlabName(slabName);
            selectedTaxItem.setTaxPercentage(tax_amount);

            //
            appExecutors.diskIO().execute(()->{
                localDb.taxesDao().insertSingleTax(selectedTaxItem);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.hideProgressbar();

                        AppDialogs appDialogs = new AppDialogs(TaxViewActivity.this);
                        appDialogs.showCommonSuccessDialog(getString(R.string.update_success), view -> finish());

                    }
                });
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * validate fields
     * */
    private boolean validate(){
        try {

            String slabName = et_taxName.getText().toString();
            String slabAmount = et_taxPercent.getText().toString();

            if(slabName.isEmpty()){
                et_taxName.setError(getString(R.string.enter_name));
                return false;
            }

            if(slabAmount.isEmpty()){
                et_taxPercent.setError(getString(R.string.enter_amount));
                return false;
            }

            return true;

        }catch (Exception e){
            return false;
        }
    }


    private void showToast(String msg){
        showToast(msg, this);
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