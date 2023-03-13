package com.rpos.pos.presentation.ui.units.add;

import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.remote.dto.uom.list.UomItem;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import java.util.ArrayList;
import java.util.List;

public class AddUomActivity extends SharedActivity {

    private LinearLayout ll_back;
    private AppExecutors appExecutors;
    private AppCompatEditText et_uomName;
    private AppCompatButton btn_add;

    private AppDatabase localDb;
    private List<UomItem> uomItemList;

    @Override
    public int setUpLayout() {
        return R.layout.activity_add_uom;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        et_uomName = findViewById(R.id.et_uom_name);
        btn_add = findViewById(R.id.btn_save);
        ll_back = findViewById(R.id.ll_back);

        uomItemList = new ArrayList<>();

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        //on save click
        btn_add.setOnClickListener(view -> onClickSave());

        //back press
        ll_back.setOnClickListener(view -> onBackPressed());

        //get saved list
        getUomListFromDb();

    }

    @Override
    public void initObservers() {

    }

    private void onClickSave(){
        try {

            String uomName = et_uomName.getText().toString();
            if(uomName.isEmpty()){
                et_uomName.setError(getString(R.string.enter_name));
                et_uomName.requestFocus();
                return;
            }

            String nameExists = checkNameAlreadyExists(uomName);
            if(nameExists!=null){

                String title = getString(R.string.alert);
                String msg = "'"+nameExists+"'"+" \n"+getString(R.string.name_exists);

                AppDialogs appDialogs = new AppDialogs(AddUomActivity.this);
                appDialogs.showCommonSingleAlertDialog(title, msg, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        et_uomName.setText("");
                    }
                });
                return;
            }

            String lastId;
            if(uomItemList.size()>0) {
                lastId = ""+uomItemList.get(uomItemList.size() - 1).getUomId();
            }else {
                lastId = "1";
            }
            int newId = Integer.parseInt(lastId);
            newId++;

            UomItem newUom = new UomItem();
            newUom.setUomName(uomName);
            newUom.setUomId(newId);

            appExecutors.diskIO().execute(() -> {

                localDb.uomDao().insertSingleUom(newUom);

                runOnUiThread(() -> {
                    AppDialogs appDialogs = new AppDialogs(AddUomActivity.this);
                    appDialogs.showCommonSuccessDialog(getString(R.string.uom_add_success), view -> finish());
                });
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To check the name already exists
     * @param newName Name to add
     * */
    private String checkNameAlreadyExists(String newName){
        try {

            String uomName;
            for (int i = 0; i < uomItemList.size(); i++) {
                uomName = uomItemList.get(i).getUomName();
                if(uomName.toLowerCase().equals(newName.toLowerCase())){
                    return uomName;
                }
            }

            return null;
        }catch (Exception e){
            return "Error";
        }
    }

    private void getUomListFromDb(){
        try {

            AppExecutors uomExecutors = new AppExecutors();

            uomExecutors.diskIO().execute(() -> {
                try {

                    List<UomItem> savedUomsList = localDb.uomDao().getAllUnitsOfMessurements();
                    if(savedUomsList!=null && savedUomsList.size()>0) {
                        uomItemList.clear();
                        uomItemList.addAll(savedUomsList);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}