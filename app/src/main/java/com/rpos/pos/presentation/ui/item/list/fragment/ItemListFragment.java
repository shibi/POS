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
import com.rpos.pos.data.remote.dto.items.delete.DeleteItemResponse;
import com.rpos.pos.data.remote.dto.items.delete.ItemDeleteMessage;
import com.rpos.pos.data.remote.dto.items.list.GetItemsListResponse;
import com.rpos.pos.data.remote.dto.items.list.ItemData;
import com.rpos.pos.domain.requestmodel.item.delete.DeleteItemRequest;
import com.rpos.pos.domain.requestmodel.item.getlist.GetItemListRequest;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.presentation.ui.common.SharedFragment;
import com.rpos.pos.presentation.ui.item.list.ItemActivity;
import com.rpos.pos.presentation.ui.item.list.adapter.ItemListAdapter;
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
                try {
                    Intent detailIntent = new Intent(getActivity(), ItemViewActivity.class);
                    detailIntent.putExtra(Constants.ITEM_ID, item.getItemId());
                    if(getActivity()!=null){
                        getActivity().startActivity(detailIntent);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onRemoveClick(ItemData item) {
                try {

                    String msg = getString(R.string.delete_confirmation);
                    appDialogs.showCommonDualActionAlertDialog(getString(R.string.delete_label), msg, new AppDialogs.OnDualActionButtonClickListener() {
                        @Override
                        public void onClickPositive(String id) {
                            deleteItemApicall(item.getItemId());
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

    private void deleteItemApicall(int itemId){
        try {

            showProgress();

            ApiService api = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            //request params
            DeleteItemRequest requestParams = new DeleteItemRequest();
            requestParams.setItemId(String.valueOf(itemId));
            Call<DeleteItemResponse> call = api.deleteItem(requestParams);
            call.enqueue(new Callback<DeleteItemResponse>() {
                @Override
                public void onResponse(Call<DeleteItemResponse> call, Response<DeleteItemResponse> response) {
                    try{

                        //hide progress
                        hideProgress();

                        if(response.isSuccessful()){
                            DeleteItemResponse deleteItemResponse = response.body();
                            if(deleteItemResponse!=null && deleteItemResponse.getMessage()!=null){

                                ItemDeleteMessage itemDeleteMessage = deleteItemResponse.getMessage();
                                if(itemDeleteMessage.getSuccess()){
                                    removeCategoryFromRecyclerView(itemId);
                                    showToast(getString(R.string.item_delete_success));
                                    return;
                                }
                                showToast(itemDeleteMessage.getMessage());
                                return;
                            }
                        }

                        showToast(getString(R.string.please_check_internet));

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<DeleteItemResponse> call, Throwable t) {
                    Log.e("----------","failed>"+t.getMessage());
                    hideProgress();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To remove category from recycler view
     * */
    private void removeCategoryFromRecyclerView(int selectedItemId){
        try {

            int listSize = itemDataList.size();
            ItemData itemData;

            if(listSize>0) {
                for (int i = 0; i < listSize; i++) {
                    itemData = itemDataList.get(i);
                    if (itemData.getItemId() == selectedItemId) {
                        itemDataList.remove(i);
                        itemListAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
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
                            if(itemList!=null && itemList.size()>0) {
                                //hide empty view
                                hideEmptyView();
                                //clear list
                                itemDataList.clear();
                                //
                                itemDataList.addAll(itemList);
                                itemListAdapter.notifyDataSetChanged();

                                //store items for later use
                                getCoreApp().setAllItemsList(itemList);

                                //All data set, stop proceeding
                                return;
                            }
                        }
                    }

                    //empty
                    showEmptyView();

                }

                @Override
                public void onFailure(Call<GetItemsListResponse> call, Throwable t) {
                    Log.e("----------","failed>"+t.getMessage());
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
