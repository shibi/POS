package com.rpos.pos.presentation.ui.supplier.lsit;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.SupplierEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.suppliers.list.GetSuppliersListResponse;
import com.rpos.pos.data.remote.dto.suppliers.list.SuppliersData;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.supplier.add.AddSuppliersActivity;
import com.rpos.pos.presentation.ui.supplier.lsit.adapter.SupplierListAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuppliersListActivity extends SharedActivity {

    private RecyclerView rv_suppliers;
    private SupplierListAdapter supplierListAdapter;
    private List<SuppliersData> suppliersDataList;
    private LinearLayout ll_back;
    private LinearLayout ll_add_supplier;

    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private AppDialogs progressDialog;
    private AppDialogs appDialogs;


    @Override
    public int setUpLayout() {
        return R.layout.activity_suppliers_list;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        rv_suppliers = findViewById(R.id.rv_suppliers);
        ll_back = findViewById(R.id.ll_back);
        ll_add_supplier = findViewById(R.id.ll_add_supplier);

        appDialogs = new AppDialogs(this);
        progressDialog = new AppDialogs(this);

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        suppliersDataList = new ArrayList<>();
        supplierListAdapter = new SupplierListAdapter(suppliersDataList, new SupplierListAdapter.SupplierClickListener() {
            @Override
            public void onClickSupplier(SuppliersData supplier) {
                // Do nothing
            }

            @Override
            public void onRemove(SuppliersData supplier) {
                try {

                    appDialogs.showCommonDualActionAlertDialog(getString(R.string.alert), getString(R.string.delete_confirmation), new AppDialogs.OnDualActionButtonClickListener() {
                        @Override
                        public void onClickPositive(String id) {
                            deleteSupplier(supplier);
                        }

                        @Override
                        public void onClickNegetive(String id) {

                        }
                    });


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        rv_suppliers.setAdapter(supplierListAdapter);

        ll_add_supplier.setOnClickListener(view -> {
            try{

                gotoAddSupplierScreen();

            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //back press
        ll_back.setOnClickListener(view -> onBackPressed());

        //get suppliers
        getSuppliersListApi();

    }

    @Override
    public void initObservers() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getSuppliersListApi(){
        try {

            ApiService apiService = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            Call<GetSuppliersListResponse> call = apiService.getSuppliersList();
            call.enqueue(new Callback<GetSuppliersListResponse>() {
                @Override
                public void onResponse(Call<GetSuppliersListResponse> call, Response<GetSuppliersListResponse> response) {
                    try{

                        Log.e("---------------","res"+response.isSuccessful());
                        if(response.isSuccessful()){
                            GetSuppliersListResponse suppliersListResponse = response.body();
                            if(suppliersListResponse!=null){
                                List<SuppliersData> suppDataList = suppliersListResponse.getMessage();

                                if(suppDataList!=null && suppDataList.size()>0) {

                                    suppliersDataList.clear();
                                    suppliersDataList.addAll(suppDataList);
                                    supplierListAdapter.notifyDataSetChanged();
                                    return;
                                }
                            }
                        }

                        showToast(getString(R.string.empty_data));

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<GetSuppliersListResponse> call, Throwable t) {
                    Log.e("---------------","failed");
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void getSupplierListLocalDb(){
        try {

            progressDialog.showProgressBar();
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        List<SupplierEntity> supplierEntities = localDb.supplierDao().getAllSuppliers();
                        if(supplierEntities!=null) {

                            suppliersDataList.clear();
                            for (SupplierEntity entity:supplierEntities){
                                suppliersDataList.add(convertEntity(entity));
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hideProgressbar();
                                    supplierListAdapter.notifyDataSetChanged();
                                }
                            });

                        }else {
                            showToast(getString(R.string.empty_data));
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void deleteSupplier(SuppliersData suppliersData){
        try {
            SupplierEntity supplierEntity = new SupplierEntity();
            supplierEntity.setId(Integer.parseInt(suppliersData.getSupplierId()));
            int removePos = -1;

            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    int removePos = -1;
                    localDb.supplierDao().delete(supplierEntity);
                    for (int i=0;i< suppliersDataList.size(); i++){
                        if(suppliersDataList.get(i).getSupplierId().equals(suppliersData.getSupplierId())){
                            suppliersDataList.remove(i);
                            removePos = i;
                            break;
                        }
                    }

                    if(removePos >-1) {
                        final int pos_to_remove = removePos;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                supplierListAdapter.notifyItemRemoved(pos_to_remove);
                            }
                        });
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void gotoAddSupplierScreen(){
        Intent addSupplierIntent = new Intent(SuppliersListActivity.this, AddSuppliersActivity.class);
        startActivity(addSupplierIntent);
    }

    private void showToast(String msg){
        showToast(msg,this);
    }

    private SuppliersData convertEntity(SupplierEntity entity){
        try {

            SuppliersData suppliersData = new SuppliersData();
            suppliersData.setSupplierId(""+entity.getId());
            suppliersData.setSupplierName(entity.getSupplierName());
            suppliersData.setTaxId(entity.getTaxId());

            return suppliersData;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}