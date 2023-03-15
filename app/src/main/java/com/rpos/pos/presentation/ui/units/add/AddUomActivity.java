package com.rpos.pos.presentation.ui.units.add;


import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;

import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.uom.add.AddUomMessage;
import com.rpos.pos.data.remote.dto.uom.add.AddUomResponse;
import com.rpos.pos.data.remote.dto.uom.list.UomItem;

import com.rpos.pos.domain.requestmodel.uom.add.AddUomRequest;
import com.rpos.pos.domain.utils.AppDialogs;

import com.rpos.pos.presentation.ui.common.SharedActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUomActivity extends SharedActivity {

    private LinearLayout ll_back;
    private AppExecutors appExecutors;
    private AppCompatEditText et_uomName;
    private AppCompatButton btn_add;
    private AppDatabase localDb;

    @Override
    public int setUpLayout() {
        return R.layout.activity_add_uom;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        et_uomName = findViewById(R.id.et_uom_name);
        btn_add = findViewById(R.id.btn_save);
        ll_back = findViewById(R.id.ll_back);

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        //on save click
        btn_add.setOnClickListener(view -> onClickSave());

        //back press
        ll_back.setOnClickListener(view -> onBackPressed());

    }

    @Override
    public void initObservers() {

    }

    private void onClickSave(){
        try {

            //validate user entered name
            String uomName = et_uomName.getText().toString();
            if(uomName.isEmpty()){
                et_uomName.setError(getString(R.string.enter_name));
                et_uomName.requestFocus();
                return;
            }

            //api call to save new uom
            addNewUomApiCall(uomName);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * api call to add new uom
     * @param uomName name of the unit of measurement
     * */
    private void addNewUomApiCall(String uomName){
        try {

            ApiService apiService = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            AddUomRequest params = new AddUomRequest();
            params.setUomName(uomName);
            Call<AddUomResponse> call = apiService.addNewUom(params);
            call.enqueue(new Callback<AddUomResponse>() {
                @Override
                public void onResponse(Call<AddUomResponse> call, Response<AddUomResponse> response) {
                    try {

                        if(response.isSuccessful()){
                            AddUomResponse addUomResponse = response.body();
                            if(addUomResponse!=null && addUomResponse.getMessage()!=null){
                                AddUomMessage addUomMessage = addUomResponse.getMessage();
                                if(addUomMessage.getSuccess()){
                                    if(addUomMessage.getData()!=null){
                                        saveUomLocally(addUomMessage.getData().getUomId(), uomName);
                                    }else {
                                        finish();
                                    }
                                    return;
                                }
                            }
                        }

                        showToast(getString(R.string.please_check_internet), AddUomActivity.this);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AddUomResponse> call, Throwable t) {
                    t.printStackTrace();
                    showToast(getString(R.string.please_check_internet), AddUomActivity.this);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to save uom in device storage
     * @param uom_id id returned from saving uom in backend.
     * @param uom_name name of the uom
     * */
    private void saveUomLocally(int uom_id, String uom_name){
        try {

            UomItem newUom = new UomItem();
            newUom.setUomId(uom_id);
            newUom.setUomName(uom_name);

            appExecutors.diskIO().execute(() -> {

                localDb.uomDao().insertSingleUom(newUom);

                runOnUiThread(() -> {
                    AppDialogs appDialogs = new AppDialogs(AddUomActivity.this);
                    appDialogs.showCommonSuccessDialog(getString(R.string.uom_add_success), view -> finish());
                });
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}