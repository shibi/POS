package com.rpos.pos.presentation.ui.paymentmodes.edit;

import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.PaymentModeEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.payment_modes.details.PaymentModeDetailsResponse;
import com.rpos.pos.data.remote.dto.payment_modes.details.PaymentModeMessage;
import com.rpos.pos.domain.requestmodel.payment.details.PaymentModeDetailsRequest;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.DateTimeUtils;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPaymentItemActivity extends SharedActivity {

    private LinearLayout ll_back;
    private AppCompatEditText et_paymodeName;
    private AppCompatButton btn_update;
    private RadioGroup rg_paytype;
    private String payment_type;
    private String str_payment_type;

    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private AppDialogs progressDialog;
    private AppDialogs appDialogs;

    private List<PaymentModeEntity> payModeList;
    private String paymentModeId;

    @Override
    public int setUpLayout() {
        return R.layout.activity_edit_payment_item;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initObservers() {

    }

    @Override
    public void initViews() {

        ll_back = findViewById(R.id.ll_back);
        et_paymodeName = findViewById(R.id.et_paymode_name);
        btn_update = findViewById(R.id.btn_save);
        rg_paytype = findViewById(R.id.rg_paytype);
        progressDialog = new AppDialogs(this);
        appDialogs = new AppDialogs(this);

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        payment_type = "";
        str_payment_type = "";
        paymentModeId = "";
        Intent data = getIntent();
        if(data!=null){
            paymentModeId = data.getStringExtra(Constants.PAYMENT_MODE_ID);
        }

        if(paymentModeId.isEmpty()){
            showToast(getString(R.string.invalid_item));
            return;
        }

        //radio group listener
        rg_paytype.setOnCheckedChangeListener(radioButtonListener);

        //on save
        btn_update.setOnClickListener(view -> onClickSave());

        //back press
        ll_back.setOnClickListener(view -> onBackPressed());

        //api call
        loadPaymentModeDetails();

    }

    RadioGroup.OnCheckedChangeListener radioButtonListener = (radioGroup, radioButton) -> {
        try {

            switch (radioButton)
            {
                case R.id.rb_cash:
                    payment_type = ""+ Constants.PAY_TYPE_CASH;
                    str_payment_type = Constants.PAY_TYPE_NAME_CASH;
                    break;
                case R.id.rb_bank:
                    payment_type = ""+ Constants.PAY_TYPE_ON_ACCOUNT;
                    str_payment_type = Constants.PAY_TYPE_NAME_BANK;
                    break;
                case R.id.rb_general:
                    payment_type = ""+ Constants.PAY_TYPE_GENERAL;
                    str_payment_type = Constants.PAY_TYPE_NAME_GENERAL;
                    break;
                case R.id.rb_phone:
                    payment_type = ""+ Constants.PAY_TYPE_PHONE;
                    str_payment_type = Constants.PAY_TYPE_NAME_PHONE;
                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    };

    /**
     * API
     * to get all payment modes list
     * */
    private void loadPaymentModeDetails(){
        try {

            showProgress();

            ApiService api = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY, Constants.API_SECRET);
            PaymentModeDetailsRequest params = new PaymentModeDetailsRequest();
            params.setPaymentModeId(paymentModeId);
            api.getPaymentModeItemDetails(params).enqueue(new Callback<PaymentModeDetailsResponse>() {

                @Override
                public void onResponse(Call<PaymentModeDetailsResponse> call, Response<PaymentModeDetailsResponse> response) {
                    try {

                        hideProgress();

                        if(response.isSuccessful()){

                            PaymentModeDetailsResponse paymentModeDetailsResponse = response.body();

                            if(paymentModeDetailsResponse!=null){

                                PaymentModeMessage paymentModeMessage = paymentModeDetailsResponse.getMessage();

                                if(paymentModeMessage!=null){

                                    if(paymentModeMessage.getPaymentModeId() != null && !paymentModeMessage.getPaymentModeId().isEmpty()){

                                        populateData(paymentModeMessage);

                                        return;
                                    }else {

                                        if(paymentModeMessage.getMessage()!=null){
                                            showToast(paymentModeMessage.getMessage());
                                            return;
                                        }
                                    }
                                }
                            }
                        }

                        showToast(getString(R.string.please_check_internet));

                    }catch (Exception e){
                        e.printStackTrace();
                        hideProgress();
                    }
                }

                @Override
                public void onFailure(Call<PaymentModeDetailsResponse> call, Throwable t) {
                    showToast(getString(R.string.please_check_internet));
                    hideProgress();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * display data in ui
     * @param paymentData received data from api
     * */
    private void populateData(PaymentModeMessage paymentData){
        try {

            et_paymodeName.setText(paymentData.getPaymentModeId());
            setRadioButtonSelectedForType(paymentData.getType());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * select radio button select for type
     * */
    private void setRadioButtonSelectedForType(String type){
        try {

            switch (type){

                case Constants.PAY_TYPE_NAME_CASH:
                        findViewById(R.id.rb_cash).setSelected(true);
                    break;

                case Constants.PAY_TYPE_NAME_BANK:
                        findViewById(R.id.rb_bank).setSelected(true);
                    break;

                case Constants.PAY_TYPE_NAME_GENERAL:
                        findViewById(R.id.rb_general).setSelected(true);
                    break;

                case Constants.PAY_TYPE_NAME_PHONE:
                        findViewById(R.id.rb_phone).setSelected(true);
                    break;
            }

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


            //show loading
            progressDialog.showProgressBar();

            //showSuccess();
            //TODO - Payment mode update api

        }catch (Exception e){
            e.printStackTrace();
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

    private void showProgress(){
        progressDialog.showProgressBar();
    }
    private void hideProgress(){
        progressDialog.hideProgressbar();
    }

    /**
     * to show message
     * */
    private void showToast(String msg){
        showToast(msg, EditPaymentItemActivity.this);
    }

}