package com.rpos.pos.presentation.ui.report;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;

import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.presentation.ui.common.SharedActivity;

public class ReportActivity extends SharedActivity {

    private CardView cv_sales,cv_purchase;
    private LinearLayout ll_back;

    @Override
    public int setUpLayout() {
        return R.layout.activity_report;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        ll_back = findViewById(R.id.ll_back);
        cv_sales = findViewById(R.id.cv_sales);
        cv_purchase = findViewById(R.id.cv_purchase);


        cv_sales.setOnClickListener(this::gotoSalesReportFragment);
        cv_purchase.setOnClickListener(this::gotoPurchaseReportFragment);
        ll_back.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void initObservers() {

    }

    private void gotoSalesReportFragment(View view){
        Intent salesReport = new Intent(ReportActivity.this, ReportViewerActivity.class);
        salesReport.putExtra(Constants.REPORT_TYPE, Constants.FRAGMENT_SALES_REPORT);
        startActivity(salesReport);
    }

    private void gotoPurchaseReportFragment(View view){
        Intent purchaserReport = new Intent(ReportActivity.this, ReportViewerActivity.class);
        purchaserReport.putExtra(Constants.REPORT_TYPE, Constants.FRAGMENT_PURCHASE_REPORT);
        startActivity(purchaserReport);
    }


}