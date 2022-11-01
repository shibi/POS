package com.rpos.pos.presentation.ui.address.list;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.Config;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.address.list.AddressListMessage;
import com.rpos.pos.data.remote.dto.address.list.GetAddressListResponse;
import com.rpos.pos.presentation.ui.address.add.AddAddressActivity;
import com.rpos.pos.presentation.ui.address.list.adapter.AddressListAdapter;
import com.rpos.pos.presentation.ui.common.SharedActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressListActivity extends SharedActivity {

    private RecyclerView rv_address;
    private AddressListAdapter addressListAdapter;
    private AppCompatButton btn_add_address;
    private List<AddressListMessage> addressMessageList;


    @Override
    public int setUpLayout() {
        return R.layout.activity_address_list;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        rv_address = findViewById(R.id.rv_address_list);
        btn_add_address = findViewById(R.id.btn_add_address);

        addressMessageList = new ArrayList<>();

        addressListAdapter = new AddressListAdapter(addressMessageList);
        rv_address.setAdapter(addressListAdapter);



        //add address list
        btn_add_address.setOnClickListener(view -> gotoAddAddressScreen());

        //request address list from backend
        getAddressList();
    }

    private void gotoAddAddressScreen(){
        Intent addAddressIntent = new Intent(AddressListActivity.this, AddAddressActivity.class);
        startActivity(addAddressIntent);
    }

    @Override
    public void initObservers() {

    }

    /**
     * To get list of address
     * */
    private void getAddressList(){
        try {

            ApiService apiService = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            Call<GetAddressListResponse> call = apiService.getAddressList();
            call.enqueue(new Callback<GetAddressListResponse>() {
                @Override
                public void onResponse(Call<GetAddressListResponse> call, Response<GetAddressListResponse> response) {
                    Log.e("---------","res"+response.isSuccessful());
                    if(response.isSuccessful()){

                        GetAddressListResponse addressListResponse = response.body();
                        if(addressListResponse!=null && addressListResponse.getMessage()!=null && addressListResponse.getMessage().size()>0){

                            List<AddressListMessage> addressList = addressListResponse.getMessage();
                            addressMessageList.addAll(addressList);
                            addressListAdapter.notifyDataSetChanged();

                        }else {
                            showToast(getString(R.string.address_list_empty),AddressListActivity.this);
                        }

                    }else {
                        showToast(getString(R.string.address_list_empty),AddressListActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<GetAddressListResponse> call, Throwable t) {
                    Log.e("---------","failed");
                    showToast(getString(R.string.address_list_fail),AddressListActivity.this);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}