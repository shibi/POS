package com.rpos.pos.presentation.ui.shift.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.rpos.pos.presentation.ui.shift.fragment.purchasereport.ShiftwisePurchaseReport;
import com.rpos.pos.presentation.ui.shift.fragment.salesreport.ShiftwiseSalesReport;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0:
                return new ShiftwiseSalesReport();
            case 1:
                return new ShiftwisePurchaseReport();
            default:
                return new ShiftwiseSalesReport();
        }
    }

    public void getFragment(){
        //Fragment currentFragment =
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
