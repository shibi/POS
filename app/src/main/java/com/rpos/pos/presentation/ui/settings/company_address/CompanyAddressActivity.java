package com.rpos.pos.presentation.ui.settings.company_address;

import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatEditText;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.CompanyAddressEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import java.util.List;

public class CompanyAddressActivity extends SharedActivity {

    private LinearLayout ll_back;
    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private AppDialogs progressDialog;
    private AppCompatEditText et_cmpnyNameEng,et_cmpnyNameArb,et_mobile,et_email,et_address;
    private LinearLayout ll_update;
    private boolean isSaveClicked;

    private CompanyAddressEntity companyAddressEntity;

    @Override
    public int setUpLayout() {
        return R.layout.activity_company_address;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        progressDialog = new AppDialogs(this);
        ll_back = findViewById(R.id.ll_back);
        progressDialog = new AppDialogs(CompanyAddressActivity.this);
        et_cmpnyNameEng = findViewById(R.id.et_companyNameEn);
        et_cmpnyNameArb = findViewById(R.id.et_companyNameAr);
        et_mobile = findViewById(R.id.et_mobile);
        et_email = findViewById(R.id.et_email);
        et_address = findViewById(R.id.et_address);
        ll_update = findViewById(R.id.ll_rightMenu);

        //thread pool
        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        isSaveClicked = false;

        ll_update.setOnClickListener(this::onClickSave);

        //get previously saved data
        getCompanyAddress();

        ll_back.setOnClickListener(view -> onBackPressed());

    }

    @Override
    public void initObservers() {

    }

    /**
     * on click save
     * */
    private void onClickSave(View view){
        try {

            if(isSaveClicked){
                return;
            }

            isSaveClicked = true;

            if(validate()){
                saveCompanyAddress();
            }else {
                isSaveClicked = false;
            }

        }catch (Exception e){
           e.printStackTrace();
           isSaveClicked= false;
        }
    }

    /**
     * validate entry fields
     * */
    private boolean validate() throws Exception {
        try {

            String companyNameEng, companyNameArb, mobile,email, address;
            companyNameEng = et_cmpnyNameEng.getText().toString();
            if(companyNameEng.isEmpty()){
                et_cmpnyNameEng.setError(getString(R.string.company_name_eng_requied));
                et_cmpnyNameEng.requestFocus();
                return false;
            }

            companyNameArb = et_cmpnyNameArb.getText().toString();
            if(companyNameArb.isEmpty()){
                et_cmpnyNameArb.setError(getString(R.string.company_name_in_ar_required));
                et_cmpnyNameArb.requestFocus();
                return false;
            }

            mobile = et_mobile.getText().toString();
            if(mobile.isEmpty()){
                et_mobile.setError(getString(R.string.mobile_required));
                et_mobile.requestFocus();
                return false;
            }

            email = et_email.getText().toString();
            if(email.isEmpty()){
                email = Constants.EMPTY;
            }

            address = et_address.getText().toString();
            if(address.isEmpty()){
                address = Constants.EMPTY;
            }

            if(companyAddressEntity== null){
                companyAddressEntity = new CompanyAddressEntity();
            }
            companyAddressEntity.setCompanyNameEng(companyNameEng);
            companyAddressEntity.setCompanyNameAr(companyNameArb);
            companyAddressEntity.setMobile(mobile);
            companyAddressEntity.setEmail(email);
            companyAddressEntity.setAddress(address);

            return true;
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * to get company address
     * */
    private void getCompanyAddress(){
        try {
            appExecutors.diskIO().execute(() -> {
                try {
                    List<CompanyAddressEntity> addressList = localDb.companyAddressDao().getCompanyDetails();
                    if(addressList == null || addressList.isEmpty()){
                        companyAddressEntity = null;
                    }else {
                        companyAddressEntity = addressList.get(0);
                        if(companyAddressEntity!=null){
                            updateUi();
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

    /**
     * save company address
     * */
    private void saveCompanyAddress(){
        try {

            progressDialog.showProgressBar();

            appExecutors.diskIO().execute(() -> {
                try {
                    if(companyAddressEntity!=null) {
                        localDb.companyAddressDao().deleteAll();
                        localDb.companyAddressDao().insert(companyAddressEntity);

                        runOnUiThread(() -> {
                            progressDialog.hideProgressbar();
                            AppDialogs appDialogs = new AppDialogs(CompanyAddressActivity.this);
                            appDialogs.showCommonAlertDialog(getString(R.string.update_success), view -> finish());
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    isSaveClicked = false;
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * update ui
     * */
    private void updateUi(){
        try {

            runOnUiThread(() -> {
                try {
                    et_cmpnyNameEng.setText(companyAddressEntity.getCompanyNameEng());
                    et_cmpnyNameArb.setText(companyAddressEntity.getCompanyNameAr());
                    et_mobile.setText(companyAddressEntity.getMobile());
                    et_email.setText(companyAddressEntity.getEmail());
                    et_address.setText(companyAddressEntity.getAddress());
                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}