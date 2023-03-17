package com.rpos.pos.presentation.ui.paymentmodes.add;

import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.PaymentModeEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.DateTimeUtils;
import com.rpos.pos.presentation.ui.common.SharedActivity;

import java.util.ArrayList;
import java.util.List;

public class AddPayModeActivity extends SharedActivity {

    private LinearLayout ll_back;
    private AppCompatEditText et_paymodeName;
    private AppCompatButton btn_save;
    private RadioGroup rg_paytype;
    private String payment_type;

    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private AppDialogs progressDialog;
    private AppDialogs appDialogs;

    private List<PaymentModeEntity> payModeList;


    @Override
    public int setUpLayout() {
        return R.layout.activity_add_pay_mode;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        ll_back = findViewById(R.id.ll_back);
        et_paymodeName = findViewById(R.id.et_paymode_name);
        btn_save = findViewById(R.id.btn_save);
        rg_paytype = findViewById(R.id.rg_paytype);
        progressDialog = new AppDialogs(this);
        appDialogs = new AppDialogs(this);

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        payment_type = "";


        //radio group listener
        rg_paytype.setOnCheckedChangeListener(radioButtonListener);


        //on save
        btn_save.setOnClickListener(view -> onClickSave());

        //back press
        ll_back.setOnClickListener(view -> onBackPressed());

        //load all pay methods at first
        //this list is used to check name duplication N.B
        loadAllPayModes();
    }

    @Override
    public void initObservers() {

    }

    RadioGroup.OnCheckedChangeListener radioButtonListener = (radioGroup, radioButton) -> {
        try {

            switch (radioButton)
            {
                case R.id.rb_cash:
                    payment_type = ""+ Constants.PAY_TYPE_CASH;
                    break;
                case R.id.rb_bank:
                    payment_type = ""+ Constants.PAY_TYPE_ON_ACCOUNT;
                    break;
                case R.id.rb_credit:
                    payment_type = ""+ Constants.PAY_TYPE_CREDIT_SALE;
                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    };



    /**
     * load all available methods
     * */
    private void loadAllPayModes(){
        try {

            appExecutors.diskIO().execute(() -> {
                payModeList = localDb.paymentModeDao().getAllPaymentModeList();
                //check to make sure list is not null
                //null safety check
                if(payModeList == null){
                    payModeList = new ArrayList<>();
                }

            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * on save, validate fields , then save locally
     * */
    private void onClickSave(){
        try {

            //validate names and id
            String paymentName = et_paymodeName.getText().toString();
            if(paymentName.isEmpty()){
                et_paymodeName.setError(getString(R.string.enter_name));
                return;
            }

            if(payment_type.isEmpty()){
                showToast(getString(R.string.pay_type));
                return;
            }

            String nameExists = checkNameExists(paymentName);
            if(nameExists!=null){
                String alertMessage = "'"+ nameExists+"'"+" \n"+getString(R.string.name_exists);
                appDialogs.showCommonSingleAlertDialog(getString(R.string.alert), alertMessage, view -> {

                });
                return;
            }

            //show loading
            progressDialog.showProgressBar();

            String today = DateTimeUtils.getCurrentDateTime();

            final PaymentModeEntity paymModeEntity = new PaymentModeEntity();
            paymModeEntity.setPaymentModeName(paymentName);
            paymModeEntity.setType(payment_type);
            paymModeEntity.setStatus(Constants.ACTIVE);
            paymModeEntity.setCreatedOn(today);



            appExecutors.diskIO().execute(() -> {
                try {

                    localDb.paymentModeDao().insertPaymentMode(paymModeEntity);
                    showSuccess();

                }catch (Exception e){
                    e.printStackTrace();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * To check whether the name already exists
     * */
    private String checkNameExists(String newName){
        try {

            String formatedName;
            for (int i=0;i<payModeList.size();i++){
                formatedName = payModeList.get(i).getPaymentModeName();
                if(formatedName.toLowerCase().equals(newName.toLowerCase())){
                    return formatedName;
                }
            }

            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * to show success dialog and
     * to redirect to next screen
     * */
    private void showSuccess(){

        runOnUiThread(() -> {

            progressDialog.hideProgressbar();
            appDialogs.showCommonSuccessDialog(getString(R.string.pay_mode_save_success), view -> finish());

        });
    }


    /**
     * to show message
     * */
    private void showToast(String msg){
        showToast(msg, AddPayModeActivity.this);
    }
}