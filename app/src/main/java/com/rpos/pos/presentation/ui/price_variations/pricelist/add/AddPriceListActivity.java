package com.rpos.pos.presentation.ui.price_variations.pricelist.add;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.appcompat.widget.AppCompatEditText;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.PriceListEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.presentation.ui.common.SharedActivity;

import java.util.List;

public class AddPriceListActivity extends SharedActivity {

    private LinearLayout ll_back;
    private LinearLayout ll_save;
    private AppCompatEditText et_priceListName,et_currencyName;
    private RadioGroup rg_type;


    private AppDialogs progressDialog;

    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private int priceListType;
    private String defaultCurrencySymbol;


    private PriceListEntity priceListEntity;

    @Override
    public int setUpLayout() {
        return R.layout.activity_add_price_list;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        //progress dialog
        progressDialog = new AppDialogs(this);

        rg_type = findViewById(R.id.rg_priceType);
        ll_back = findViewById(R.id.ll_back);
        ll_save = findViewById(R.id.ll_save);
        et_priceListName = findViewById(R.id.et_priceListName);
        et_currencyName = findViewById(R.id.et_currencyName);

        priceListType = Constants.EMPTY_INT;

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        defaultCurrencySymbol = SharedPrefHelper.getInstance(this).getDefaultCurrencySymbol();
        et_currencyName.setText(defaultCurrencySymbol);

        //on radio button click , set the price list type accordingly
        rg_type.setOnCheckedChangeListener((radioGroup, checkedId) -> {

            if(checkedId == R.id.rb_buying){  //on selecting the buy radio button, set type BUYING
                priceListType = Constants.BUYING;
            }else if(checkedId == R.id.rb_selling){  //on selecting the sell radio button, set type SELLING
                priceListType = Constants.SELLING;
            }
        });


        ll_save.setOnClickListener(view -> {
            try {

                if(validate()){
                    savePriceListItemToLocal();
                }else {
                    progressDialog.hideProgressbar();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //Back press
        ll_back.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void initObservers() {

    }


    private boolean validate(){
        try {

            progressDialog.showProgressBar();

            String name = et_priceListName.getText().toString();
            if(name.isEmpty()){
                et_priceListName.setError(getString(R.string.enter_name));
                et_priceListName.requestFocus();
                return false;
            }

            if(priceListType == Constants.EMPTY_INT){
                showToast(getString(R.string.select_type), AddPriceListActivity.this);
                return false;
            }

            priceListEntity = new PriceListEntity();
            priceListEntity.setPriceListName(name.trim());
            priceListEntity.setCurrencySymbol(et_currencyName.getText().toString());
            priceListEntity.setCountryCode(et_currencyName.getText().toString());
            priceListEntity.setPriceType(priceListType);
            priceListEntity.setEnabled(true);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * save to local
     * */
    private void savePriceListItemToLocal(){
        try {

            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        List<PriceListEntity> temp_allList = localDb.priceListDao().getAllPriceList();
                        if(temp_allList!=null){
                            final String priceListName = priceListEntity.getPriceListName().toLowerCase();
                            for (PriceListEntity priceListEntity : temp_allList){
                                if(priceListEntity.getPriceListName().toLowerCase().equals(priceListName)){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showToast(getString(R.string.name_exists), AddPriceListActivity.this);
                                            progressDialog.hideProgressbar();
                                        }
                                    });
                                    return;
                                }
                            }
                        }

                        localDb.priceListDao().insertItem(priceListEntity);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hideProgressbar();
                                AppDialogs appDialogs = new AppDialogs(AddPriceListActivity.this);
                                appDialogs.showCommonSuccessDialog(getString(R.string.price_list_add_success), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        setResult(RESULT_OK, new Intent());
                                        finish();
                                    }
                                });
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
}