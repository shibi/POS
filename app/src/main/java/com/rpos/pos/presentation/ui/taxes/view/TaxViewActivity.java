package com.rpos.pos.presentation.ui.taxes.view;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.TaxSlabEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.taxes.add.AddTaxSlabActivity;

public class TaxViewActivity extends SharedActivity {

    private AppCompatEditText et_taxName,et_taxPercent;
    private AppCompatButton btn_update;
    private int taxId;
    private AppDialogs progressDialog;
    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private AppDialogs appDialogs;
    private LinearLayout ll_back;
    private TaxSlabEntity selectedTaxItem;

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
    }

    @Override
    public void initObservers() {

    }


    @Override
    protected void onResume() {
        super.onResume();

        getTaxDetails();
    }

    /**
     * get tax details
     * */
    private void getTaxDetails(){
        try {

            appExecutors.diskIO().execute(() -> {
                try {

                    selectedTaxItem = localDb.taxesDao().getTaxDetailsWithId(taxId);
                    updateUI();

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
                et_taxName.setText(selectedTaxItem.getTaxSlabName());
                et_taxPercent.setText(""+selectedTaxItem.getSlabAmount());
            });

        }catch (Exception e){
            throw e;
        }
    }


    private void onUpdateClick(){
        try {

            if(validate()){
                saveTax();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveTax(){
        try {

            progressDialog.showProgressBar();

            String name = et_taxName.getText().toString();
            String str_amount= et_taxPercent.getText().toString();
            float amount = Float.parseFloat(str_amount);

            selectedTaxItem.setTaxSlabName(name);
            selectedTaxItem.setSlabAmount(amount);

            //
            appExecutors.diskIO().execute(()->{
                localDb.taxesDao().insertTaxes(selectedTaxItem);

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
}