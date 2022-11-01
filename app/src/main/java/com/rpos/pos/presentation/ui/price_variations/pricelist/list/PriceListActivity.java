package com.rpos.pos.presentation.ui.price_variations.pricelist.list;

import android.content.Intent;
import android.widget.LinearLayout;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.PriceListEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.price_variations.pricelist.add.AddPriceListActivity;

import java.util.ArrayList;
import java.util.List;


public class PriceListActivity extends SharedActivity {

    private LinearLayout ll_back,ll_priceList_add;
    private AppDialogs progressDialog;

    private AppExecutors appExecutors;
    private AppDatabase localDb;

    private RecyclerView rv_priceList;
    private PriceListAdapter priceListAdapter;
    private List<PriceListEntity> priceListArray;

    private AppDialogs appDialogs;

    @Override
    public int setUpLayout() {
        return R.layout.activity_price_list;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        //progress dialog
        progressDialog = new AppDialogs(this);
        appDialogs = new AppDialogs(this);

        ll_back = findViewById(R.id.ll_back);
        ll_priceList_add = findViewById(R.id.ll_add_pricelist);
        rv_priceList = findViewById(R.id.rv_priceListItems);

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        priceListArray = new ArrayList<>();
        priceListAdapter = new PriceListAdapter(PriceListActivity.this, priceListArray, new PriceListAdapter.PriceListViewListener() {
            @Override
            public void onClickPriceList(PriceListEntity priceListEntity) {

            }

            @Override
            public void onClickRemove(PriceListEntity priceListEntity) {
                try {

                    String title = getString(R.string.alert);
                    String msg = getString(R.string.delete_confirmation);
                    appDialogs.showCommonDualActionAlertDialog(title, msg, new AppDialogs.OnDualActionButtonClickListener() {
                        @Override
                        public void onClickPositive(String id) {
                            deleteItem(priceListEntity);
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
        rv_priceList.setAdapter(priceListAdapter);


        //go to add
        ll_priceList_add.setOnClickListener(view -> gotoAddPriceListActivity());

        //BACK PRESS
        ll_back.setOnClickListener(view -> onBackPressed());


        getPriceListFromLocalDb();
    }

    @Override
    public void initObservers() {

    }

    private void getPriceListFromLocalDb(){
        try {

            progressDialog.showProgressBar();
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        List<PriceListEntity> tempList = localDb.priceListDao().getAllPriceList();
                        if(tempList!=null && tempList.size()> 0){

                            priceListArray.clear();
                            priceListArray.addAll(tempList);
                            runOnUiThread(() -> {
                                progressDialog.hideProgressbar();
                                priceListAdapter.notifyDataSetChanged();});

                        }else {
                            runOnUiThread(() -> {
                                progressDialog.hideProgressbar();
                                showToast(getString(R.string.empty_data), PriceListActivity.this); });
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


    private void deleteItem(PriceListEntity priceListEntity){
        try {

            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        localDb.priceListDao().deletePriceList(priceListEntity);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                removeItemFromArray(priceListEntity.getId());
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

    private void removeItemFromArray(int deletedId){
        try {

            for (int i=0;i< priceListArray.size();i++){
                if(priceListArray.get(i).getId() == deletedId){
                    priceListArray.remove(i);
                    priceListAdapter.notifyItemRemoved(i);
                    return;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private ActivityResultLauncher<Intent> launchAddPriceList = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){
                getPriceListFromLocalDb();
            }
        }
    });

    private void gotoAddPriceListActivity(){
        Intent addIntent = new Intent(PriceListActivity.this, AddPriceListActivity.class);
        launchAddPriceList.launch(addIntent);
    }
}