package com.rpos.pos.presentation.ui.units.list;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.uom.list.GetUomListResponse;
import com.rpos.pos.data.remote.dto.uom.list.UomItem;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.units.add.AddUomActivity;
import com.rpos.pos.presentation.ui.units.list.adapter.UomListAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UOMListActivity extends SharedActivity {

    private LinearLayout ll_back,ll_add_uom;
    private RecyclerView rv_uomlist;

    private AppExecutors appExecutors;
    private AppDatabase localDb;

    private List<UomItem> uomItemList;
    private UomListAdapter uomListAdapter;

    private View viewEmpty;

    @Override
    public int setUpLayout() {
        return R.layout.activity_uom_list;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        rv_uomlist = findViewById(R.id.rv_uomlist);
        ll_back = findViewById(R.id.ll_back);
        ll_add_uom = findViewById(R.id.ll_add_units);

        viewEmpty = findViewById(R.id.view_empty);


        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();


        uomItemList = new ArrayList<>();
        uomListAdapter = new UomListAdapter(uomItemList, item -> {

            //let user confirm deletion
            promptDeleteConfirmation(item);

        });

        rv_uomlist.setAdapter(uomListAdapter);

        //add uom
        ll_add_uom.setOnClickListener(view -> gotoAddUomActivity());

        //back press
        ll_back.setOnClickListener(view -> onBackPressed());

    }

    @Override
    public void initObservers() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        //get saved uom list
        //getUomListFromDb();
        getAllUOMList();

    }

    /**
     * To fetch locally saved uom list
     * if no data found, then fetch data from api and save locally
     * */
    private void getUomListFromDb(){
        try {

            AppExecutors uomExecutors = new AppExecutors();

            uomExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        List<UomItem> savedUomsList = localDb.uomDao().getAllUnitsOfMessurements();
                        if(savedUomsList!=null && savedUomsList.size()>0) {
                            uomItemList.clear();
                            uomItemList.addAll(savedUomsList);
                            runOnUiThread(() -> uomListAdapter.notifyDataSetChanged());

                        }else {

                            runOnUiThread(() -> {
                                try{
                                    //if there is no uom data fetch it from API
                                    getAllUOMList();

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            });
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

    /**
     * To get all uom values
     * */
    private void getAllUOMList(){
        try {

            ApiService apiService = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            Call<GetUomListResponse> call = apiService.getUOMList();
            call.enqueue(new Callback<GetUomListResponse>() {
                @Override
                public void onResponse(Call<GetUomListResponse> call, Response<GetUomListResponse> response) {
                    try {

                        if(response.isSuccessful()){

                            GetUomListResponse getUomListResponse = response.body();
                            if(getUomListResponse!=null){

                                List<UomItem> dataList = getUomListResponse.getMessage();
                                if(dataList!=null && dataList.size()>0){

                                    uomItemList.clear();
                                    uomItemList.addAll(dataList);
                                    uomListAdapter.notifyDataSetChanged();

                                    hideEmptyView();

                                    AppExecutors appExecutors1 = new AppExecutors();
                                    appExecutors1.diskIO().execute(() -> {
                                        try {

                                            localDb.uomDao().deleteAll();

                                            for (int i=0;i<dataList.size();i++){
                                                dataList.get(i).setUomId(""+i);
                                            }
                                            localDb.uomDao().insertUom(dataList);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    });

                                    return;
                                }
                            }
                        }

                        showEmptyList();


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<GetUomListResponse> call, Throwable t) {
                   t.printStackTrace();
                   showEmptyList();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * prompt dialog to let user confirm deletion
     * @param item uom item to delete
     * */
    private void promptDeleteConfirmation(UomItem item){
        try {

            String message = getString(R.string.delete_confirmation);

            AppDialogs appDialogs = new AppDialogs(UOMListActivity.this);
            appDialogs.showCommonDualActionAlertDialog(getString(R.string.delete_label), message, new AppDialogs.OnDualActionButtonClickListener() {
                @Override
                public void onClickPositive(String id) {
                    try {

                        appExecutors.diskIO().execute(() -> {
                            try {

                                localDb.uomDao().delete(item);

                                for (int i=0;i<uomItemList.size();i++){
                                    if(uomItemList.get(i).getUomId().equals(item.getUomId())){
                                        uomItemList.remove(i);
                                        refreshAfterRemove(i);
                                        break;
                                    }
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        });

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClickNegetive(String id) {

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * refresh adapter position after item remove
     * @param position position of the item removed
     * */
    private void refreshAfterRemove(final int position){
        try {

            runOnUiThread(() -> uomListAdapter.notifyItemRemoved(position));

        }catch (Exception e){
            e.printStackTrace();
        }
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
     * To goto add uom screen
     * */
    private void gotoAddUomActivity(){
        Intent addUOMIntent = new Intent(this, AddUomActivity.class);
        startActivity(addUOMIntent);
    }
}