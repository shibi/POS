package com.rpos.pos.presentation.ui.report;

import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.report.fragments.PurchaseReportFragment;
import com.rpos.pos.presentation.ui.report.fragments.SalesReportFragment;

public class ReportViewerActivity extends SharedActivity {

    @Override
    public int setUpLayout() {
        return R.layout.activity_report_viewer;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        Intent intent = getIntent();
        if(intent!=null){
            String fragmentToLoad = intent.getStringExtra(Constants.REPORT_TYPE);
            if(fragmentToLoad!=null && !fragmentToLoad.isEmpty()){

                if(fragmentToLoad.equals(Constants.FRAGMENT_PURCHASE_REPORT))
                    loadFragment(new PurchaseReportFragment(), Constants.FRAGMENT_PURCHASE_REPORT);
                else
                    loadFragment(new SalesReportFragment(), Constants.FRAGMENT_SALES_REPORT);

            }else {
                loadFragment(new SalesReportFragment(), Constants.FRAGMENT_SALES_REPORT);
            }
        }else {
            loadFragment(new SalesReportFragment(), Constants.FRAGMENT_SALES_REPORT);
        }


    }

    @Override
    public void initObservers() {

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