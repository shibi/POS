package com.rpos.pos.presentation.ui.taxes.list;

import android.content.Intent;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.TaxSlabEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.taxes.TaxesAdapter;
import com.rpos.pos.presentation.ui.taxes.add.AddTaxSlabActivity;
import com.rpos.pos.presentation.ui.taxes.view.TaxViewActivity;

import java.util.ArrayList;
import java.util.List;

public class TaxesActivity extends SharedActivity implements TaxesAdapter.TaxClickListener {

    private RecyclerView rv_taxes;
    private LinearLayout ll_back,ll_add_taxes;
    private TaxesAdapter taxesAdapter;
    private List<TaxSlabEntity> taxSlabEntityList;

    private AppDialogs progressDialog;
    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private AppDialogs appDialogs;
    private SharedPrefHelper sharedPref;

    private int defaultTaxid;

    @Override
    public int setUpLayout() {
        return R.layout.activity_taxes;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        ll_back = findViewById(R.id.ll_back);
        ll_add_taxes = findViewById(R.id.ll_rightMenu);
        rv_taxes = findViewById(R.id.rv_taxes);

        taxSlabEntityList = new ArrayList<>();
        taxesAdapter = new TaxesAdapter(TaxesActivity.this, taxSlabEntityList, this);
        rv_taxes.setAdapter(taxesAdapter);

        progressDialog = new AppDialogs(this);
        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();
        appDialogs = new AppDialogs(this);

        sharedPref = SharedPrefHelper.getInstance(TaxesActivity.this);


        defaultTaxid = sharedPref.getDefaultTaxId();


        //BACK PRESS
        ll_back.setOnClickListener(view -> onBackPressed());

        //
        ll_add_taxes.setOnClickListener(view -> {
            try {
                gotoAddTaxesActivity();
            }catch (Exception e){
                e.printStackTrace();
            }
        });


    }

    @Override
    public void initObservers() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        getTaxesSlabListFromDb();
    }

    private void getTaxesSlabListFromDb(){
        try {

            progressDialog.showProgressBar();

            appExecutors.diskIO().execute(() -> {
                try {
                    //get list from db
                    List<TaxSlabEntity> taxesList = localDb.taxesDao().getAllSlabs();
                    //check whether list is empty
                    if (taxesList != null && !taxesList.isEmpty()) {
                        //clear previous data and add new list
                        taxSlabEntityList.clear();
                        taxSlabEntityList.addAll(taxesList);

                        //check whether default tax is selected ( -1 equals no tax is selected )
                        if(defaultTaxid > -1) {
                            //Default selected tax is available.
                            //To highlight default selected tax item when added to recyclerview,
                            //loop through the main list and set the isSelected flag true for the item
                            for (int i = 0; i < taxSlabEntityList.size(); i++) {
                                if (taxSlabEntityList.get(i).getId() == defaultTaxid) {
                                    taxSlabEntityList.get(i).setSelected(true);
                                    break;
                                }
                            }
                        }

                        //use ui thread to update the adapter
                        //and progressbar remove
                        runOnUiThread(() -> {
                            taxesAdapter.notifyDataSetChanged();
                            progressDialog.hideProgressbar();
                        });

                    } else {
                        //show message
                        runOnUiThread(() -> {
                            showToast(getString(R.string.empty_data), TaxesActivity.this);
                            progressDialog.hideProgressbar();
                        });
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
    public void onClickRemove(TaxSlabEntity tax_item) {

        String msg = getString(R.string.delete_confirmation);
        appDialogs.showCommonDualActionAlertDialog(getString(R.string.delete_label), msg, new AppDialogs.OnDualActionButtonClickListener() {
            @Override
            public void onClickPositive(String id) {

                //remove item from db and array
                deleteTaxItem(tax_item);
            }

            @Override
            public void onClickNegetive(String id) {
            }
        });

    }

    @Override
    public void onSelectTaxDefault(int taxid, String slabName, float taxRate) {
        try {

            SharedPrefHelper sharedPrefHelper = SharedPrefHelper.getInstance(TaxesActivity.this);
            sharedPrefHelper.saveDefaultTax(taxid,slabName,taxRate);
            defaultTaxid = taxid;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClickTaxView(int taxid) {
        try {

            //on click goto view screen
            gotoTaxViewScreen(taxid);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to remove item from db and array then update list adapter
     * */
    private void deleteTaxItem(TaxSlabEntity tax_item){
        try {

            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    //removing item from db
                    localDb.taxesDao().delete(tax_item);

                    //using ui thread to update list changes
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //to remove item from array and update adapter
                                removeItemFromArray(tax_item.getId());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to remove item from array and update adapter
     * */
    private void removeItemFromArray(int taxItemId){
        try {

            //default initialization to check value updated.
            int removedPosition = -1;

            //find the item to remove
            for (int i=0; i < taxSlabEntityList.size(); i++){
                if(taxSlabEntityList.get(i).getId().equals(taxItemId)){
                    taxSlabEntityList.remove(i);
                    removedPosition = i;
                    break;
                }
            }

            //update updater only if remove position is not -1
            if(removedPosition!=-1){
                taxesAdapter.notifyItemRemoved(removedPosition);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void gotoAddTaxesActivity(){
        Intent intent = new Intent(this, AddTaxSlabActivity.class);
        startActivity(intent);
    }

    private void gotoTaxViewScreen(int taxid){
        Intent intent = new Intent(this, TaxViewActivity.class);
        intent.putExtra(Constants.TAX_ID, taxid);
        startActivity(intent);
    }


}