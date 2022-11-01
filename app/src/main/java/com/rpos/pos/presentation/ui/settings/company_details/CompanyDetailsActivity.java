package com.rpos.pos.presentation.ui.settings.company_details;

import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.CompanyEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;

import java.util.List;

public class CompanyDetailsActivity extends SharedActivity {

    private LinearLayout ll_back;
    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private CompanyEntity savedCompanyEntity;
    private AppDialogs progressDialog;
    private AppCompatEditText et_cmpnyNameEng,et_cmpnyNameArb,et_taxNumber;
    private AppCompatButton btn_update;

    @Override
    public int setUpLayout() {
        return R.layout.activity_company_details;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        ll_back = findViewById(R.id.ll_back);
        progressDialog = new AppDialogs(CompanyDetailsActivity.this);
        et_cmpnyNameEng = findViewById(R.id.et_companyNameEn);
        et_cmpnyNameArb = findViewById(R.id.et_companyNameAr);
        et_taxNumber = findViewById(R.id.et_taxNumber);
        btn_update = findViewById(R.id.btn_update);

        //
        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        //back press
        ll_back.setOnClickListener(view ->  onBackPressed());

        //check whether saved details available
        getSavedCompanyDetails();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validateFields()){
                    //save current details
                    saveCompanyDetailsToLocal();
                }

            }
        });

    }

    @Override
    public void initObservers() {

    }

    /**
     * to check any data is saved
     * */
    private void getSavedCompanyDetails(){
        try {

            progressDialog.showProgressBar();

            appExecutors.diskIO().execute(() -> {
                try {

                    List<CompanyEntity> companyEntityList = localDb.companyDetailsDao().getAllCompanyDetails();
                    if(companyEntityList!=null && companyEntityList.size()>0){
                        savedCompanyEntity = companyEntityList.get(0);
                        populateData(savedCompanyEntity);
                    }else {
                        savedCompanyEntity = null;
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hideProgressbar();
                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to validate user input fields
     * */
    private boolean validateFields(){
        try {

            String companyNameEng = et_cmpnyNameEng.getText().toString();
            String companyNameArb = et_cmpnyNameArb.getText().toString();
            String taxNumber = et_taxNumber.getText().toString();
            if(companyNameEng.isEmpty()){
                et_cmpnyNameEng.setError(getString(R.string.company_name_eng_requied));
                et_cmpnyNameEng.requestFocus();
                return false;
            }

            if(companyNameArb.isEmpty()){
                et_cmpnyNameArb.setError(getString(R.string.company_name_in_ar_required));
                et_cmpnyNameArb.requestFocus();
                return false;
            }

            if(taxNumber.isEmpty()){
                et_taxNumber.setError(getString(R.string.tax_number_required));
                et_taxNumber.requestFocus();
                return false;
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * */
    private void saveCompanyDetailsToLocal(){
        try {

            progressDialog.showProgressBar();

            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        if(savedCompanyEntity==null){
                            savedCompanyEntity = new CompanyEntity();
                        }

                        savedCompanyEntity.setCompanyNameInEng(et_cmpnyNameEng.getText().toString());
                        savedCompanyEntity.setCompanyNameInArb(et_cmpnyNameArb.getText().toString());
                        savedCompanyEntity.setTaxNumber(et_taxNumber.getText().toString());

                        //save the data
                        localDb.companyDetailsDao().insertDetails(savedCompanyEntity);


                        //hide progress bar
                        runOnUiThread(() -> {
                            progressDialog.hideProgressbar();
                            AppDialogs appDialogs = new AppDialogs(CompanyDetailsActivity.this);
                            appDialogs.showCommonSuccessDialog(getString(R.string.data_updated), view -> finish());

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

    private void populateData(CompanyEntity companyEntity){
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    et_cmpnyNameEng.setText(companyEntity.getCompanyNameInEng());
                    et_cmpnyNameArb.setText(companyEntity.getCompanyNameInArb());
                    et_taxNumber.setText(companyEntity.getTaxNumber());
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}