package com.rpos.pos.presentation.ui.taxes.add;


import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.TaxSlabEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;

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
                saveTax();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveTax(){
        try {

            progressDialog.showProgressBar();

            String name = et_slabName.getText().toString();
            String str_amount= et_amount.getText().toString();
            float amount = Float.parseFloat(str_amount);

            TaxSlabEntity taxSlab = new TaxSlabEntity();
            taxSlab.setTaxSlabName(name);
            taxSlab.setSlabAmount(amount);

            //
            appExecutors.diskIO().execute(()->{
                localDb.taxesDao().insertTaxes(taxSlab);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.hideProgressbar();

                        AppDialogs appDialogs = new AppDialogs(AddTaxSlabActivity.this);
                        appDialogs.showCommonSuccessDialog(getString(R.string.tax_add_success), view -> finish());

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
}