package com.rpos.pos.presentation.ui.price_variations.pricelist.select;

import android.content.Intent;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.PriceListEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.price_variations.pricelist.list.PriceListAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectPriceListActivity extends SharedActivity {

    private LinearLayout ll_back;
    private AppDialogs progressDialog;

    private AppExecutors appExecutors;
    private AppDatabase localDb;

    private RecyclerView rv_priceList;
    private PriceListAdapter priceListAdapter;
    private List<PriceListEntity> priceListArray;

    private AppDialogs appDialogs;

    @Override
    public int setUpLayout() {
        return R.layout.activity_select_price_list;
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
        rv_priceList = findViewById(R.id.rv_priceList);

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        priceListArray = new ArrayList<>();
        priceListAdapter = new PriceListAdapter(SelectPriceListActivity.this, priceListArray, new PriceListAdapter.PriceListViewListener() {
            @Override
            public void onClickPriceList(PriceListEntity priceListEntity) {

                try {

                    Intent intent = new Intent();
                    intent.putExtra(Constants.PRICELIST_ID, priceListEntity.getId());
                    intent.putExtra(Constants.PRICELIST_NAME, priceListEntity.getPriceListName());
                    intent.putExtra(Constants.PRICELIST_TYPE, priceListEntity.getPriceType());
                    setResult(RESULT_OK, intent);
                    finish();

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onClickRemove(PriceListEntity priceListEntity) {
                try {

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        rv_priceList.setAdapter(priceListAdapter);


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
                                showToast(getString(R.string.empty_data), SelectPriceListActivity.this); });
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
}