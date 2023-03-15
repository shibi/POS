package com.rpos.pos.presentation.ui.taxes.list;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.tax.list.TaxData;
import com.rpos.pos.data.remote.dto.tax.list.TaxListResponse;
import com.rpos.pos.data.remote.dto.tax.list.TaxMessage;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.taxes.TaxesAdapter;
import com.rpos.pos.presentation.ui.taxes.add.AddTaxSlabActivity;
import com.rpos.pos.presentation.ui.taxes.view.TaxViewActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaxesActivity extends SharedActivity implements TaxesAdapter.TaxClickListener {

    private RecyclerView rv_taxes;
    private LinearLayout ll_back,ll_add_taxes;
    private TaxesAdapter taxesAdapter;
    private List<TaxData> taxDataList;

    private AppDialogs progressDialog;
    private AppDialogs appDialogs;

    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private SharedPrefHelper sharedPref;
    private View viewEmpty;

    private int defaultTaxid;

    @Override
    public int setUpLayout() {
        return R.layout.activity_taxes;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        ll_back = findViewById(R.id.ll_back);
        ll_add_taxes = findViewById(R.id.ll_rightMenu);
        rv_taxes = findViewById(R.id.rv_taxes);
        viewEmpty = findViewById(R.id.view_empty);

        taxDataList = new ArrayList<>();
        taxesAdapter = new TaxesAdapter(TaxesActivity.this, taxDataList, this);
        rv_taxes.setAdapter(taxesAdapter);

        progressDialog = new AppDialogs(this);
        appDialogs = new AppDialogs(this);
        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        sharedPref = SharedPrefHelper.getInstance(TaxesActivity.this);

        defaultTaxid = sharedPref.getDefaultTaxId();


        //BACK PRESS
        ll_back.setOnClickListener(view -> onBackPressed());

        //
        ll_add_taxes.setOnClickListener(view -> {
            try {
                gotoAddTaxesActivity();
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        getAllTaxSlabListAPI();

    }

    @Override
    public void initObservers() {

    }

    /**
     * Api to get all tax slabs
     * */
    private void getAllTaxSlabListAPI(){
        try {

            showProgress();
            showEmptyList();

            ApiService api = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            Call<TaxListResponse> call = api.getAllTaxSlabList();
            call.enqueue(new Callback<TaxListResponse>() {
                @Override
                public void onResponse(Call<TaxListResponse> call, Response<TaxListResponse> response) {
                    Log.e("-----------","APO"+response.isSuccessful());
                    hideProgress();
                    try {
                        //check response success
                        if(response.isSuccessful()){
                            //get response
                            TaxListResponse taxListResponse = response.body();
                            if(taxListResponse!= null && taxListResponse.getMessage()!= null){
                                TaxMessage taxMessage = taxListResponse.getMessage();
                                if(taxMessage.getSuccess()){
                                    List<TaxData> tempList = taxMessage.getData();
                                    if(tempList!=null && !tempList.isEmpty()){
                                        taxDataList.clear();
                                        taxDataList.addAll(taxMessage.getData());
                                        taxesAdapter.notifyDataSetChanged();

                                        saveTaxListLocally(tempList);

                                        hideEmptyView();
                                        return;
                                    }
                                }
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<TaxListResponse> call, Throwable t) {
                    Log.e("-----------","failed");
                    hideProgress();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onSelectTaxDefault(int taxid, String slabName, float taxRate) {
        try {

            SharedPrefHelper sharedPrefHelper = SharedPrefHelper.getInstance(TaxesActivity.this);
            sharedPrefHelper.saveDefaultTax(taxid,slabName,taxRate);
            defaultTaxid = taxid;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClickTaxView(int taxid) {
        try {

            //on click goto view screen
            gotoTaxViewScreen(taxid);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClickRemove(TaxData tax) {

        String message = getString(R.string.delete_confirmation);
        appDialogs.showCommonDualActionAlertDialog(getString(R.string.delete_label), message, new AppDialogs.OnDualActionButtonClickListener() {
            @Override
            public void onClickPositive(String id) {
                //delete tax from db
                deleteTaxItemLocally(tax);
            }

            @Override
            public void onClickNegetive(String id) {
                //Do nothing
            }
        });


    }

    private void saveTaxListLocally(List<TaxData> taxList){
        try {

            AppExecutors appExecutors = new AppExecutors();
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        //clear the previous items
                        localDb.taxesDao().deleteTaxTable();

                        //add the new items
                        localDb.taxesDao().insertTax(taxList);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to remove item from db and array then update list adapter
     * */
    private void deleteTaxItemLocally(TaxData tax_item){
        try {

            showProgress();

            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    //removing item from db
                    localDb.taxesDao().deleteSingleTax(tax_item);

                    //using ui thread to update list changes
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //to remove item from array and update adapter
                                removeItemFromArray(tax_item.getTaxSlabId());
                                hideProgress();

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to remove item from array and update adapter
     * */
    private void removeItemFromArray(int taxItemId){
        try {

            //default initialization to check value updated.
            int removedPosition = -1;

            //find the item to remove
            for (int i=0; i < taxDataList.size(); i++){
                if(taxDataList.get(i).getTaxSlabId() == taxItemId){
                    taxDataList.remove(i);
                    removedPosition = i;
                    break;
                }
            }

            //update updater only if remove position is not -1
            if(removedPosition!=-1){
                taxesAdapter.notifyItemRemoved(removedPosition);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void gotoAddTaxesActivity(){
        Intent intent = new Intent(this, AddTaxSlabActivity.class);
        startActivity(intent);
    }

    private void gotoTaxViewScreen(int taxid){
        Intent intent = new Intent(this, TaxViewActivity.class);
        intent.putExtra(Constants.TAX_ID, taxid);
        startActivity(intent);
    }


    /**
     * to show empty view
     * */
    private void showEmptyList(){
        viewEmpty.setVisibility(View.VISIBLE);
    }

    /**
     * to hide empty view
     * */
    private void hideEmptyView(){
        viewEmpty.setVisibility(View.GONE);
    }

    /**
     * to show and hide progress
     * */
    private void showProgress(){
        progressDialog.showProgressBar();
    }
    private void hideProgress(){
        progressDialog.hideProgressbar();
    }

}