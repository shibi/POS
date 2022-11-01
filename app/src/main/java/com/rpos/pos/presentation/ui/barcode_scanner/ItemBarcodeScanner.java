package com.rpos.pos.presentation.ui.barcode_scanner;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.presentation.ui.common.SharedActivity;

public class ItemBarcodeScanner extends SharedActivity {

    private CodeScanner mCodeScanner;

    @Override
    public int setUpLayout() {
        return R.layout.activity_item_barcode_scanner;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ItemBarcodeScanner.this, result.getText(), Toast.LENGTH_SHORT).show();
                        String scannedBarcode = result.getText();
                        if(!scannedBarcode.isEmpty()){

                            //pass barcode to item select screen with activity result
                            redirectToItemSelectionWithCode(scannedBarcode);

                        }else {
                            showToast(getString(R.string.barcode_empty));
                        }
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    public void initObservers() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private void showToast(String msg){
        showToast(msg, ItemBarcodeScanner.this);
    }

    /**
     *  set the result barcode back to item select screen
     * */
    private void redirectToItemSelectionWithCode(String barcode){
        Intent intent = new Intent();
        intent.putExtra(Constants.BARCODE_NUMBER,barcode);
        setResult(RESULT_OK, intent);
        finish();
    }
}