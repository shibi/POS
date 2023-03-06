package com.rpos.pos.presentation.ui.supplier.selection;

import android.content.Intent;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
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
import com.rpos.pos.presentation.ui.supplier.lsit.SuppliersListActivity;
import com.rpos.pos.presentation.ui.supplier.lsit.adapter.SupplierListAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupplierSelectionActivity extends SharedActivity {

    private LinearLayout ll_back;
    private AppDialogs progressDialog;
    private LinearLayout ll_add_supplier;
    private RecyclerView rv_suppliers;
    private SearchView sv_searchItem;

    private AppExecutors appExecutors;
    private AppDatabase localDb;


    private SupplierListAdapter supplierListAdapter;
    private List<SuppliersData> suppliersDataList;

    @Override
    public int setUpLayout() {
        return R.layout.activity_supplier_selection;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        ll_back = findViewById(R.id.ll_back);
        ll_add_supplier = findViewById(R.id.ll_add_supplier);
        rv_suppliers = findViewById(R.id.rv_supplierlist);
        sv_searchItem = findViewById(R.id.sv_search);

        progressDialog = new AppDialogs(this);
        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        suppliersDataList = new ArrayList<>();
        supplierListAdapter = new SupplierListAdapter(suppliersDataList, new SupplierListAdapter.SupplierClickListener() {
            @Override
            public void onClickSupplier(SuppliersData supplier) {

                Intent intent = new Intent();
                intent.putExtra(Constants.SUPPLIER_ID, supplier.getSupplierId());
                intent.putExtra(Constants.SUPPLIER_NAME, supplier.getSupplierName());
                intent.putExtra(Constants.SUPPLIER_TAXID, supplier.getTaxId());

                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onRemove(SuppliersData supplier) {
                //No need to delete supplier from this activity
                //simply show the user that they have no access
                showToast(getString(R.string.access_denied));
            }
        });

        rv_suppliers.setAdapter(supplierListAdapter);


        //filter list on search
        sv_searchItem.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                supplierListAdapter.getFilter().filter(newText);
                return false;
            }
        });


        ll_add_supplier.setOnClickListener(view -> {
            try{

                gotoAddSupplierScreen();

            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //back press
        ll_back.setOnClickListener(view -> onBackPressed());

        //getSupplierListLocalDb();
        getSuppliersListApi();
    }

    @Override
    public void initObservers() {

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
                    showToast(getString(R.string.please_check_internet));
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

    private void gotoAddSupplierScreen(){
        Intent addSupplierIntent = new Intent(SupplierSelectionActivity.this, AddSuppliersActivity.class);
        launchAddSupplier.launch(addSupplierIntent);
    }


    private ActivityResultLauncher<Intent> launchAddSupplier = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){
                Intent data  = result.getData();
                if(data!=null){
                    Log.e("------------","supplier added");
                    int supplId = data.getIntExtra(Constants.SUPPLIER_ID, Constants.EMPTY_INT);
                    String supplierName = data.getStringExtra(Constants.SUPPLIER_NAME);
                    SupplierEntity supplierEntity = new SupplierEntity();
                    supplierEntity.setId(supplId);
                    supplierEntity.setSupplierName(supplierName);
                    suppliersDataList.add(convertEntity(supplierEntity));
                    supplierListAdapter.notifyItemInserted(suppliersDataList.size());

                }
            }
        }
    });



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