package com.rpos.pos.presentation.ui.category.add;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.CategoryEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.category.add.AddCategoryMessage;
import com.rpos.pos.data.remote.dto.category.add.AddCategoryResponse;
import com.rpos.pos.domain.requestmodel.category.add.AddCategoryRequest;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.DateTimeUtils;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCategoryActivity extends SharedActivity {

    private AppCompatEditText et_catName;
    private AppCompatButton btn_save;
    private AppDialogs progressDialog;
    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private LinearLayout ll_back;

    @Override
    public int setUpLayout() {
        return R.layout.activity_add_category;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        et_catName = findViewById(R.id.et_category_name);
        btn_save = findViewById(R.id.btn_save);
        progressDialog = new AppDialogs(this);
        ll_back = findViewById(R.id.ll_back);

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();


        btn_save.setOnClickListener(view -> {
            try{

                onClickSave();

            }catch (Exception e){
                e.printStackTrace();
            }
        });


        //back click
        ll_back.setOnClickListener(view -> onBackPressed());

    }

    @Override
    public void initObservers() {

    }

    private void onClickSave(){
        try {

            String categoryName = et_catName.getText().toString().trim();
            if(categoryName.isEmpty()){
                showToast(getString(R.string.enter_category));
                return;
            }

            //save to local db
            saveCategoryToLocalDb(categoryName);

            //send to backend API
           // saveNewCategory(categoryName);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To save category in local db
     * */
    private void saveCategoryToLocalDb(String categoryName){
        try {
            progressDialog.showProgressBar();
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        //get current date for saving
                        String dateNow = DateTimeUtils.getCurrentDateTime();
                        //create new category for saving
                        CategoryEntity newCategory = new CategoryEntity();
                        newCategory.setCategoryId(categoryName);
                        newCategory.setCategoryName(categoryName);
                        newCategory.setCategoryStatus("active");
                        newCategory.setCreatedOn(dateNow);

                        //check whether category exists with same id in local db
                        CategoryEntity categoryExist = localDb.categoryDao().getCategoryWithId(newCategory.getCategoryId());
                        //if null, no category exists with same id
                        if(categoryExist == null){
                            //add new category
                            localDb.categoryDao().insertCategory(newCategory);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    progressDialog.hideProgressbar();
                                    //show success dialog
                                    addCategorySuccess();
                                }
                            });

                        }else {
                            //use ui thread to show toast
                            runOnUiThread(() -> {
                                progressDialog.hideProgressbar();
                                showToast(getString(R.string.categoryExists));
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

    private void saveNewCategory(String categoryName){
        try {

            progressDialog.showProgressBar();

            ApiService api = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            AddCategoryRequest request = new AddCategoryRequest();
            request.setCategory(categoryName);
            Call<AddCategoryResponse> call = api.addCategory(request);
            call.enqueue(new Callback<AddCategoryResponse>() {
                @Override
                public void onResponse(Call<AddCategoryResponse> call, Response<AddCategoryResponse> response) {
                    progressDialog.hideProgressbar();
                    Log.e("------------","res"+response.isSuccessful());
                    if(response.isSuccessful()){
                        AddCategoryResponse addCategoryResponse = response.body();
                        if(addCategoryResponse!=null){
                            AddCategoryMessage addCategoryMessage = addCategoryResponse.getMessage();

                            if(addCategoryMessage!=null && addCategoryMessage.getSuccess()){
                                //success
                               addCategorySuccess();

                            }else {
                                showToast(getString(R.string.add_category_failed));
                            }
                        }else {
                            showToast(getString(R.string.add_category_failed));
                        }
                    }else {
                        showToast(getString(R.string.add_category_failed));
                    }

                }

                @Override
                public void onFailure(Call<AddCategoryResponse> call, Throwable t) {
                    Log.e("------------","failed");
                    progressDialog.hideProgressbar();
                    showToast(getString(R.string.add_category_failed));
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addCategorySuccess(){
        try {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    showToast(getString(R.string.category_added_success));

                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showToast(String msg){
        showToast(msg,AddCategoryActivity.this);
    }

}