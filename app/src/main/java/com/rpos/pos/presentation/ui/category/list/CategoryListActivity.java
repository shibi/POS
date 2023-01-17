package com.rpos.pos.presentation.ui.category.list;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.CategoryEntity;
import com.rpos.pos.data.local.entity.ItemEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.category.list.CategoryItem;
import com.rpos.pos.data.remote.dto.category.list.GetCategoryResponse;
import com.rpos.pos.data.remote.dto.items.list.ItemData;
import com.rpos.pos.domain.models.item.PickedItem;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.ConverterFactory;
import com.rpos.pos.presentation.ui.category.add.AddCategoryActivity;
import com.rpos.pos.presentation.ui.category.list.adapter.CategoryListAdapter;
import com.rpos.pos.presentation.ui.category.view.CategoryViewActivity;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.sales.order.create.CreateOrderActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryListActivity extends SharedActivity {

    private RecyclerView rv_category;
    private LinearLayout ll_back, ll_add_category;
    private CategoryListAdapter categoryListAdapter;
    private ArrayList<CategoryItem> categoryItemsArray;
    private AppDialogs progressDialog;
    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private View viewEmpty;
    private AppDialogs appDialogs;

    @Override
    public int setUpLayout() {
        return R.layout.activity_category_list;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        ll_back = findViewById(R.id.ll_back);
        rv_category = findViewById(R.id.rv_category);
        categoryItemsArray = new ArrayList<>();
        progressDialog = new AppDialogs(this);
        viewEmpty = findViewById(R.id.view_empty);
        ll_add_category = findViewById(R.id.ll_add_category);
        appDialogs = new AppDialogs(this);

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        categoryListAdapter = new CategoryListAdapter(categoryItemsArray, new CategoryListAdapter.CategoryClickListener() {
            @Override
            public void onClickCategoryItem(CategoryItem category) {
                gotoCategoryViewScreen(category.getCategoryId(), category.getCategory());
            }

            @Override
            public void onRemoveClick(CategoryItem category) {

                String message = getString(R.string.delete_confirmation);
                appDialogs.showCommonDualActionAlertDialog(getString(R.string.delete_label), message, new AppDialogs.OnDualActionButtonClickListener() {
                    @Override
                    public void onClickPositive(String id) {
                        //delete item from db
                        deleteCategory(category.getCategoryId());
                    }

                    @Override
                    public void onClickNegetive(String id) {
                        //Do nothing
                    }
                });
            }
        });

        rv_category.setAdapter(categoryListAdapter);

        //back press
        ll_back.setOnClickListener(view -> onBackPressed());

        //Add category
        ll_add_category.setOnClickListener(this::gotoAddCategoryScreen);

        //api to get all category list
        getCategoryListApi();
        //getCategoryFromLocalDb();

    }

    @Override
    public void initObservers() {

    }


    /**
     * Get saved category from local db
     * */
    private void getCategoryFromLocalDb(){
        try{

            appExecutors.diskIO().execute(() -> {
                try{

                    List<CategoryEntity> savedCategory = localDb.categoryDao().getAllCategory();
                    if(savedCategory!=null && !savedCategory.isEmpty()) {
                        processCategoryList(savedCategory);
                    }else {
                        runOnUiThread(() -> showEmptyList());
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  delete category
     *  verify category usage before
     * */
    private void deleteCategory(String categoryId){
        try {

            appExecutors.diskIO().execute(() -> {
                try {
                    //verify category id
                    ItemEntity categoryLinkedItem = checkAnyItemUsingCategory(categoryId);
                    if(categoryLinkedItem !=null){
                        //item exists with provided category
                        runOnUiThread(() -> {
                            //Notify user
                            String msg = getString(R.string.delete_category_linked)+categoryLinkedItem.getItemName();
                            appDialogs.showCommonAlertDialog(msg, null);
                        });
                        return;
                    }

                    localDb.categoryDao().deleteCategoryWithId(categoryId);
                    removeCategoryFromRecyclerView(categoryId);
                    showToast(getString(R.string.category_delete_success));
                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * check any item is linked to this category provided
     * @param categoryId id of category selected for delete
     * */
    private ItemEntity checkAnyItemUsingCategory(String categoryId){
        //get all items with provided category from local db
        List<ItemEntity> itemsList = localDb.itemDao().getAllItemWithProvidedCategory(categoryId);
        //check if it is null
        if(itemsList == null || itemsList.isEmpty()){
            return null;
        }else {
            //return the first item
            return itemsList.get(0);
        }
    }

    /**
     * To find the category and update name change
     * then refresh adapter to reflect change
     * */
    private void findCategoryAndRefresh(String cateId,String cateName){
        try {
            //check id is valid
            if(cateId!=null && !cateId.isEmpty()){
                //loop through and find the category with id
                for (int i=0; i<categoryItemsArray.size(); i++){
                    //match id to find category
                    if(categoryItemsArray.get(i).getCategoryId().equals(cateId)){
                        //update category name in the list
                        categoryItemsArray.get(i).setCategory(cateName);
                        //refresh adapter
                        categoryListAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void processCategoryList(List<CategoryEntity> savedCategory){
        try {

            //clear previous items
            categoryItemsArray.clear();

            CategoryItem categoryItem;
            CategoryEntity categorySaved;
            for (int i =0;i< savedCategory.size();i++){
                categoryItem = new CategoryItem();
                categorySaved = savedCategory.get(i);

                categoryItem.setCategoryId(categorySaved.getCategoryId());
                categoryItem.setCategory(categorySaved.getCategoryName());
                categoryItemsArray.add(categoryItem);
            }

            runOnUiThread(() -> {
                try {
                    hideEmptyView();
                    categoryListAdapter.notifyDataSetChanged();

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
    private void removeCategoryFromRecyclerView(String catId){
        try {

            if(catId==null || catId.isEmpty()){
                showToast(getString(R.string.no_such_category));
                return;
            }

            int listSize = categoryItemsArray.size();
            CategoryItem _category;
            int removePos = 0;
            if(listSize>0) {
                for (int i = 0; i < listSize; i++) {
                    _category = categoryItemsArray.get(i);
                    if (_category.getCategoryId().equals(catId)) {
                        categoryItemsArray.remove(i);
                        removePos = i;
                        break;
                    }
                }

                final int removeRefPos = removePos;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            categoryListAdapter.notifyItemRemoved(removeRefPos);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to get category list
     * */
    private void getCategoryListApi(){
        try {

            showProgress();

            ApiService api = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            Call<GetCategoryResponse> call = api.getCategoryList();
            call.enqueue(new Callback<GetCategoryResponse>() {
                @Override
                public void onResponse(Call<GetCategoryResponse> call, Response<GetCategoryResponse> response) {
                    Log.e("-----------","APO"+response.isSuccessful());
                    hideProgress();
                    try {

                        //check response success
                        if(response.isSuccessful()){
                            //get response
                            GetCategoryResponse getCategoryResponse = response.body();
                            if(getCategoryResponse!=null){
                                //get the category list from response
                                List<CategoryItem> categ_list = getCategoryResponse.getMessage();
                                //check list is valid
                                if(categ_list!=null && categ_list.size()>0){
                                    //clear previous list items and add the new list
                                    categoryItemsArray.clear();
                                    categoryItemsArray.addAll(categ_list);
                                    categoryListAdapter.notifyDataSetChanged();

                                    //save the categories in room db for later use
                                    saveCategoryLocally(categ_list);

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
                public void onFailure(Call<GetCategoryResponse> call, Throwable t) {
                    Log.e("-----------","failed");
                    hideProgress();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to save the categories in response to room db
     * */
    private void saveCategoryLocally(List<CategoryItem> catList){
        try {

            AppExecutors appExecutors = new AppExecutors();
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        //clear the previous items
                        localDb.categoryDao().deleteAll();

                        List<CategoryEntity> entityList = new ArrayList<>();
                        for (CategoryItem category: catList) {
                            entityList.add(ConverterFactory.convertToCategoryEntity(category));
                        }

                        //add items, if there any
                        if(entityList.size()>0) {
                            //add the new items
                            localDb.categoryDao().insertCategoryList(entityList);
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

    private ActivityResultLauncher<Intent> viewCategoryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){
                Intent data = result.getData();
                if(data!=null){
                    String cateId = data.getStringExtra(Constants.CATEGORY_ID);
                    String catName = data.getStringExtra(Constants.CATEGORY_NAME);
                    //To immediately reflect the updated name change in category list item
                    // We need to find the item in list and update name. Then refresh adapter
                    findCategoryAndRefresh(cateId, catName);
                }
            }
        }
    });

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    private ActivityResultLauncher<Intent> addCategoryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        //Intent data = result.getData();

                        getCategoryListApi();    // API
                        //getCategoryFromLocalDb();  // ROOM DB
                    }
                }});

    /**
     * goto category details view
     * */
    private void gotoCategoryViewScreen(String categoryId, String categoryName){
        Intent categoryViewIntent = new Intent(this, CategoryViewActivity.class);
        categoryViewIntent.putExtra(Constants.CATEGORY_ID, categoryId);
        categoryViewIntent.putExtra(Constants.CATEGORY_NAME, categoryName);
        viewCategoryLauncher.launch(categoryViewIntent);
    }

    /**
     * goto category add screen
     * */
    private void gotoAddCategoryScreen(View view){
        Intent addCategoryIntent = new Intent(this, AddCategoryActivity.class);
        addCategoryActivityResultLauncher.launch(addCategoryIntent);
    }

    /**
     * to show toast in main thread
     * */
    private void showToast(String msg){
        runOnUiThread(() -> showToast(msg, CategoryListActivity.this));
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