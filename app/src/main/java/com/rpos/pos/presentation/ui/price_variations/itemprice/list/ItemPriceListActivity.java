package com.rpos.pos.presentation.ui.price_variations.itemprice.list;

import android.content.Intent;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.ItemPriceEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.price_variations.itemprice.add.AddItemPriceActivity;
import com.rpos.pos.presentation.ui.price_variations.itemprice.edit.ItemPriceEditActivity;

import java.util.ArrayList;
import java.util.List;

public class ItemPriceListActivity extends SharedActivity {

    private LinearLayout ll_back, ll_add;
    private RecyclerView rv_itemPriceList;
    private List<ItemPriceEntity> itemPriceList;

    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private AppDialogs progressDialog;

    private ItemPriceListAdapter itemPriceListAdapter;

    @Override
    public int setUpLayout() {
        return R.layout.activity_item_price_list;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        ll_back = findViewById(R.id.ll_back);
        ll_add = findViewById(R.id.ll_rightMenu);
        rv_itemPriceList = findViewById(R.id.rv_itempriceList);

        //progress dialog
        progressDialog = new AppDialogs(this);

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        itemPriceList = new ArrayList<>();
        itemPriceListAdapter = new ItemPriceListAdapter(ItemPriceListActivity.this, itemPriceList, itemPriceEntity -> {
            //on click item
            gotoEditItemPriceListActivity(itemPriceEntity.getId(), itemPriceEntity.getItemId(), itemPriceEntity.getPriceListType());
        });
        rv_itemPriceList.setAdapter(itemPriceListAdapter);


        //add item price screen
        ll_add.setOnClickListener(view -> gotoAddItemPriceActivity());

        //back
        ll_back.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void initObservers() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        getItemPriceList();

    }

    private void getItemPriceList(){
        try {

            appExecutors.diskIO().execute(() -> {
                try {

                  List<ItemPriceEntity> tempList = localDb.itemPriceListDao().getAllItems();
                  if(tempList!=null){
                      itemPriceList.clear();
                      itemPriceList.addAll(tempList);
                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              itemPriceListAdapter.notifyDataSetChanged();
                          }
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

    private void gotoAddItemPriceActivity(){
        Intent addItemPrice = new Intent(this, AddItemPriceActivity.class);
        startActivity(addItemPrice);
    }

    private void gotoEditItemPriceListActivity(int item_price_id, int _itemId, int type){
        Intent editItemPrice = new Intent(this, ItemPriceEditActivity.class);
        editItemPrice.putExtra(Constants.ITEM_PRICE_LIST_ID, item_price_id);
        startActivity(editItemPrice);
    }
}