package com.rpos.pos.presentation.ui.category.view;


import android.content.Intent;
import android.util.Log;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.CategoryEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.category.details.CategoryDetail;
import com.rpos.pos.data.remote.dto.category.details.CategoryDetailsResponse;
import com.rpos.pos.domain.requestmodel.category.details.CategoryDetailsRequest;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryViewActivity extends SharedActivity {

    private String categoryId;
    private AppCompatEditText et_categoryName;
    private AppCompatButton btn_update;
    private LinearLayout ll_back;

    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private CategoryEntity savedCategoryEntity;


    @Override
    public int setUpLayout() {
        return R.layout.activity_category_view;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        Intent intent = getIntent();
        if(intent!=null){
            categoryId = intent.getStringExtra(Constants.CATEGORY_ID);
            if(categoryId == null || categoryId.isEmpty()){
                showToast("Invalid category id. Please retry",this);
                return;
            }
        }

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        et_categoryName = findViewById(R.id.et_category_name);
        btn_update = findViewById(R.id.btn_update);
        ll_back = findViewById(R.id.ll_back);

        //get category details
        getCategoryFromLocalDb(categoryId);
        //getCategoryDetails(categoryId);


        //update click
        btn_update.setOnClickListener(view -> updateClick());


        //back press
        ll_back.setOnClickListener(view -> onBackPressed());

    }

    @Override
    public void initObservers() {

    }

    /**
     * get the category details from local db
     * */
    private void getCategoryFromLocalDb(String _categoryId){
        try {

            appExecutors.diskIO().execute(() -> {
                try {

                    CategoryEntity categoryEntity =  localDb.categoryDao().getCategoryWithId(_categoryId);

                    savedCategoryEntity = categoryEntity;

                    //show data in ui
                    populateFields(categoryEntity);

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * show name in the field with main thread
     * */
    private void populateFields(CategoryEntity category){
        try {

            runOnUiThread(() -> et_categoryName.setText(category.getCategoryName()));

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * on update click, check name valid and then update
     * */
    private void updateClick(){
        try {

            String categoryName = et_categoryName.getText().toString();
            if(categoryName.isEmpty()){
                showToast(getString(R.string.enter_category_name));
                et_categoryName.setError(getString(R.string.enter_category_name));
                return;
            }

            if(savedCategoryEntity!=null){
                savedCategoryEntity.setCategoryName(categoryName);

                appExecutors.diskIO().execute(() -> {
                    try {

                        localDb.categoryDao().insertCategory(savedCategoryEntity);
                        runOnUiThread(() -> {
                            //show dialog box
                            AppDialogs appDialogs = new AppDialogs(CategoryViewActivity.this);
                            appDialogs.showCommonSuccessDialog(getString(R.string.cate_update_success), view -> sendResultBack());
                        });

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                });

            }else {

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendResultBack(){
        try {

            String categoryName = et_categoryName.getText().toString().trim();

            Intent intent = new Intent();
            intent.putExtra(Constants.CATEGORY_ID, categoryId);
            intent.putExtra(Constants.CATEGORY_NAME, categoryName);
            setResult(RESULT_OK, intent);
            finish();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getCategoryDetails(String categoryId){
        try {

            ApiService api = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            CategoryDetailsRequest request = new CategoryDetailsRequest();
            request.setCategoryId(categoryId);
            Call<CategoryDetailsResponse> call = api.getCategoryDetails(request);
            call.enqueue(new Callback<CategoryDetailsResponse>() {
                @Override
                public void onResponse(Call<CategoryDetailsResponse> call, Response<CategoryDetailsResponse> response) {
                    Log.e("----------","re"+response.isSuccessful());
                    if(response.isSuccessful()){
                        CategoryDetailsResponse categoryDetailsResponse = response.body();
                        if(categoryDetailsResponse!=null){
                            List<CategoryDetail> list = categoryDetailsResponse.getMessage();
                            if(list!=null && list.size()>0){

                                CategoryDetail categoryDetail = list.get(0);
                                et_categoryName.setText(categoryDetail.getCategory());

                            }else {
                                showToast(getString(R.string.empty_data));
                            }
                        }else {
                            showToast(getString(R.string.empty_data));
                        }
                    }else {
                        showToast(getString(R.string.empty_data));
                    }
                }

                @Override
                public void onFailure(Call<CategoryDetailsResponse> call, Throwable t) {
                    Log.e("----------","failed");
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showToast(String msg){
        showToast(msg,CategoryViewActivity.this);
    }
}