package com.rpos.pos.presentation.ui.item.list.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.ItemEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.category.list.CategoryItem;
import com.rpos.pos.data.remote.dto.items.list.GetItemsListResponse;
import com.rpos.pos.data.remote.dto.items.list.ItemData;
import com.rpos.pos.domain.requestmodel.item.getlist.GetItemListRequest;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.presentation.ui.category.list.CategoryListActivity;
import com.rpos.pos.presentation.ui.common.SharedFragment;
import com.rpos.pos.presentation.ui.item.list.ItemActivity;
import com.rpos.pos.presentation.ui.item.list.adapter.ItemListAdapter;
import com.rpos.pos.presentation.ui.item.select.ItemSelectActivity;
import com.rpos.pos.presentation.ui.item.view.ItemViewActivity;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ItemListFragment extends SharedFragment {


    private LinearLayout ll_back,ll_add;
    private RecyclerView rv_itemList;
    private List<ItemData> itemDataList;
    private ItemListAdapter itemListAdapter;
    private AppDialogs progressDialog;
    private View viewEmpty;
    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private AppDialogs appDialogs;


    public ItemListFragment(){

    }

    @Override
    protected int setContentLayout() {
        return R.layout.fragment_item_list;
    }

    @Override
    protected void onCreateView(View getView) {

        ll_back = getView.findViewById(R.id.ll_back);
        rv_itemList = getView.findViewById(R.id.rv_itemlist);
        progressDialog = new AppDialogs(getContext());
        itemDataList = new ArrayList<>();
        viewEmpty = getView.findViewById(R.id.view_empty);
        ll_add = getView.findViewById(R.id.ll_add_items);

        appDialogs = new AppDialogs(getContext());

        appExecutors = new AppExecutors();
        localDb = ((CoreApp)getActivity().getApplication()).getLocalDb();

        //Need currency details to show in each item,
        //N.B : - STOP here if NO default currency is selected
        String defaultCurrencySymbol = ((ItemActivity)getActivity()).getDefaultCurrency();
        if(defaultCurrencySymbol == null || defaultCurrencySymbol.equals(Constants.NONE)){
            AppDialogs appDialogs = new AppDialogs(getContext());
            appDialogs.showCommonAlertDialog("Default currency is not selected. Go to settings and select currency", view -> getActivity().finish());
            return;
        }

        itemListAdapter = new ItemListAdapter(itemDataList, getContext(), defaultCurrencySymbol, new ItemListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ItemData item) {
                Intent detailIntent = new Intent(getActivity(), ItemViewActivity.class);
                detailIntent.putExtra(Constants.ITEM_ID, item.getItemId());
                getActivity().startActivity(detailIntent);
            }

            @Override
            public void onRemoveClick(ItemData item) {
                try {

                    String msg = getString(R.string.delete_confirmation);
                    appDialogs.showCommonDualActionAlertDialog(getString(R.string.delete_label), msg, new AppDialogs.OnDualActionButtonClickListener() {
                        @Override
                        public void onClickPositive(String id) {
                            deleteItem(item.getItemId());
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
        rv_itemList.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_itemList.setAdapter(itemListAdapter);

        //add option click
        ll_add.setOnClickListener(view -> {
            try {

                ((ItemActivity)getActivity()).gotoAddItemActivity();

            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //BACK PRESS
        ll_back.setOnClickListener(view -> getActivity().onBackPressed());

        //get items list
        //getItemsList();

    }

    @Override
    protected void initViewModels() {

    }

    @Override
    protected void initObservers() {

    }

    @Override
    public void onResume() {
        super.onResume();

        try {

            if(itemListAdapter!=null) {
                //getItemsFromLocalDB();
                getItemsListApiCall();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getItemsFromLocalDB(){
        try {

            appExecutors.diskIO().execute(() -> {
                try {

                    List<ItemEntity> savedItemList = localDb.itemDao().getAllItems();
                    List<ItemData> formatedList = new ArrayList<>();
                    ItemData item;
                    for (int i=0;i<savedItemList.size();i++){
                        item = new ItemData();
                        item.convertFromItemEntity(savedItemList.get(i));
                        formatedList.add(item);
                    }

                    itemDataList.clear();
                    itemDataList.addAll(formatedList);
                    getActivity().runOnUiThread(() -> {

                        itemListAdapter.notifyDataSetChanged();
                        if(itemDataList.size() > 0){
                            hideEmptyView();
                        }else {
                            showEmptyView();
                        }

                    });

                }catch (Exception e){
                    e.printStackTrace();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * find item with id and delete
     * */
    private void deleteItem(String str_itemId){
        try {

            appExecutors.diskIO().execute(() -> {
                try {

                    int item_id = Integer.parseInt(str_itemId);



                    localDb.itemDao().deleteItemWithId(item_id);
                    removeCategoryFromRecyclerView(str_itemId);

                    //show message
                    getActivity().runOnUiThread(() -> showToast(getString(R.string.item_delete_success)));


                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To remove category from recycler view
     * */
    private void removeCategoryFromRecyclerView(String selectedItemId){
        try {

            int listSize = itemDataList.size();
            ItemData itemData;
            int removePos = 0;

            if(listSize>0) {
                for (int i = 0; i < listSize; i++) {
                    itemData = itemDataList.get(i);
                    if (itemData.getItemId().equals(selectedItemId)) {
                        itemDataList.remove(i);
                        removePos = i;
                        break;
                    }
                }

                final int removeRefPos = removePos;
                getActivity().runOnUiThread(() -> {
                    try {

                        itemListAdapter.notifyItemRemoved(removeRefPos);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                });

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To get items list  API
     * */
    private void getItemsListApiCall(){
        try {

            showProgress();

            String userId = SharedPrefHelper.getInstance(getContext()).getUserId();
            if(userId.isEmpty()){
                showToast(getString(R.string.invalid_userid));
                hideProgress();
                return;
            }


            ApiService api = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            //request params
            GetItemListRequest requestParams = new GetItemListRequest();
            requestParams.setUserId(userId);
            Call<GetItemsListResponse> call = api.getItemsList(requestParams);
            call.enqueue(new Callback<GetItemsListResponse>() {
                @Override
                public void onResponse(Call<GetItemsListResponse> call, Response<GetItemsListResponse> response) {
                    hideProgress();
                    Log.e("----------","res"+response.isSuccessful());
                    if(response.isSuccessful()){
                        GetItemsListResponse itemsListResponse = response.body();
                        if(itemsListResponse!=null){
                            List<ItemData> itemList = itemsListResponse.getMessage();
                            if(itemList!=null && itemList.size()>0){
                                //hide empty view
                                hideEmptyView();
                                //clear list
                                itemDataList.clear();
                                //
                                itemDataList.addAll(itemList);
                                itemListAdapter.notifyDataSetChanged();

                            }else {
                                //empty
                                showEmptyView();
                            }
                        }else {
                            //empty
                            showEmptyView();
                        }
                    }else {
                        //empty
                        showEmptyView();
                    }
                }

                @Override
                public void onFailure(Call<GetItemsListResponse> call, Throwable t) {
                    Log.e("----------","failed");
                    hideProgress();
                    //empty
                    showEmptyView();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showProgress(){
        progressDialog.showProgressBar();
    }
    private void hideProgress(){
        progressDialog.hideProgressbar();
    }

    private void showEmptyView(){
        viewEmpty.setVisibility(View.VISIBLE);
    }
    private void hideEmptyView(){
        viewEmpty.setVisibility(View.GONE);
    }

}
