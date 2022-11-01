package com.rpos.pos.presentation.ui.item.list;

import androidx.fragment.app.Fragment;
import android.content.Intent;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.remote.dto.uom.list.UomItem;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.item.add.AddItemActivity;
import com.rpos.pos.presentation.ui.item.list.fragment.ItemListFragment;
import com.rpos.pos.presentation.ui.item.view.ItemViewActivity;

import java.util.List;


public class ItemActivity extends SharedActivity {

    private AppExecutors appExecutors;
    private AppDatabase localDb;

    @Override
    public int setUpLayout() {
        return R.layout.activity_item;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        //load list fragment
        loadListFragment();

        //pre-load uom list to speed up things
        preLoadUomListFromLocalDb();
    }

    @Override
    public void initObservers() {

    }


    public String getDefaultCurrency(){
        return getCoreApp().getDefaultCurrency();
    }

    public void gotoAddItemActivity(){
        Intent addItemIntent = new Intent(this, AddItemActivity.class);
        startActivity(addItemIntent);
        finish();
    }

    private void loadListFragment(){
        loadFragment(new ItemListFragment(), Constants.FRAGMENT_ITEM_LIST);
    }


    /**
     *  pre-loading uom items from local db to speed item list,view screens
     *  use the preloaded uom list to populate data in item view
     * */
    private void preLoadUomListFromLocalDb(){
        try {

            appExecutors.diskIO().execute(() -> {
                try {

                    List<UomItem> savedUomsList = localDb.uomDao().getAllUnitsOfMessurements();
                    if(savedUomsList!=null && savedUomsList.size()>0) {
                        getCoreApp().setUomList(savedUomsList);
                    }else {
                        getCoreApp().setUomList(null);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadFragment(Fragment fragment, String tag) {
        try {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment, tag)
                    .commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}