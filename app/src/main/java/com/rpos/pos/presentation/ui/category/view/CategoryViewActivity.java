package com.rpos.pos.presentation.ui.category.view;


import android.content.Intent;
import android.util.Log;
import android.view.View;
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

    private int categoryId;
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

        et_categoryName = findViewById(R.id.et_category_name);
        btn_update = findViewById(R.id.btn_update);
        ll_back = findViewById(R.id.ll_back);


        Intent intent = getIntent();
        if(intent!=null){
            categoryId = intent.getIntExtra(Constants.CATEGORY_ID, Constants.EMPTY_INT);
            String categoryName = intent.getStringExtra(Constants.CATEGORY_NAME);

            if(categoryId  == Constants.EMPTY_INT){
                showToast("Invalid category id. Please retry",this);
                return;
            }

            et_categoryName.setText(categoryName);
        }

        //CURRENTLY THERE IS NO CATEGORY UPDATE API, SO DISABLE EDIT MODE
        et_categoryName.setClickable(false);
        et_categoryName.setFocusable(false);

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        //HIDE UPDATE BTN, SINCE THERE IS NO UPDATE API
        btn_update.setEnabled(false);
        btn_update.setVisibility(View.GONE);
        //btn_update.setOnClickListener(view -> updateClick());

        //back press
        ll_back.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void initObservers() {

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

    private void showToast(String msg){
        showToast(msg,CategoryViewActivity.this);
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

                        //localDb.categoryDao().insertCategory(savedCategoryEntity);
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


}