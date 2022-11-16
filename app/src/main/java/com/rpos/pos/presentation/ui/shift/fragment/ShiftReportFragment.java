package com.rpos.pos.presentation.ui.shift.fragment;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.ShiftRegEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedFragment;
import com.rpos.pos.presentation.ui.shift.ShiftWiseFilterable;
import com.rpos.pos.presentation.ui.shift.adapter.ShiftSpinnerAdapter;
import com.rpos.pos.presentation.ui.shift.adapter.ViewPagerAdapter;
import com.rpos.pos.presentation.ui.shift.fragment.salesreport.ShiftwiseSalesReport;

import java.util.ArrayList;
import java.util.List;

public class ShiftReportFragment extends SharedFragment {

    private LinearLayout ll_back;
    private RecyclerView rv_shiftList;
    private AppCompatSpinner spinnerShifts;

    private AppExecutors appExecutors;
    private AppDatabase localDb;

    private AppDialogs progressDialog;

    private ShiftSpinnerAdapter shiftSpinnerAdapter;
    private List<ShiftRegEntity> shiftRegList;

    private boolean isSpinnerFirstLoad;

    @Override
    protected int setContentLayout() {
        return R.layout.fragment_shift_report;
    }

    @Override
    protected void onCreateView(View getView) {

        ll_back = getView.findViewById(R.id.ll_back);
        //rv_shiftList = getView.findViewById(R.id.rv_shifts);
        spinnerShifts = getView.findViewById(R.id.sp_shift);


        //progress bar
        progressDialog = new AppDialogs(getContext());

        //thread
        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        TabLayout tabLayout = getView.findViewById(R.id.tab_layout);
        ViewPager2 viewPager = getView.findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), getLifecycle());

        viewPager.setAdapter(adapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position)
                {
                    case 0:
                        tab.setText(getString(R.string.sales_title));
                        break;
                    case 1:
                        tab.setText(getString(R.string.purchase_label));
                        break;
                    default:
                        tab.setText(getString(R.string.default_label_small));
                        break;
                }
            }
        });
        tabLayoutMediator.attach();




        shiftRegList = new ArrayList<>();
        shiftSpinnerAdapter = new ShiftSpinnerAdapter(shiftRegList, getContext());
        spinnerShifts.setAdapter(shiftSpinnerAdapter);

        isSpinnerFirstLoad = true;
        spinnerShifts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                try{
                    if(isSpinnerFirstLoad){
                        isSpinnerFirstLoad = false;
                        return;
                    }

                    Fragment currentFragment = getChildFragmentManager().getFragments().get(viewPager.getCurrentItem());
                    if(currentFragment!=null){
                        ((ShiftWiseFilterable)currentFragment).onFilterWithShift(shiftRegList.get(position).getId());
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });



        //back press listener
        ll_back.setOnClickListener(view -> getActivity().onBackPressed());


        getAllShifts();

    }

    @Override
    protected void initViewModels() {

    }

    @Override
    protected void initObservers() {

    }

    private void getAllShifts(){
        try {

            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        List<ShiftRegEntity> shiftTempList = localDb.shiftDao().getAllShifts();
                        if(shiftTempList!=null && !shiftTempList.isEmpty()){

                            shiftRegList.clear();
                            shiftRegList.addAll(shiftTempList);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    shiftSpinnerAdapter.notifyDataSetChanged();
                                }
                            });

                        }else {

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
}
