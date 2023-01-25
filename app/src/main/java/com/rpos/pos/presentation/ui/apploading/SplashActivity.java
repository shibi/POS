package com.rpos.pos.presentation.ui.apploading;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.rpos.pos.MainActivity;
import com.rpos.pos.R;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.login.LoginActivity;

public class SplashActivity extends SharedActivity {

    @Override
    public int setUpLayout() {
        return R.layout.activity_splash;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gotoLoginActivity();
            }
        }, 500);
    }

    @Override
    public void initObservers() {

    }

    /**
     * redirect to login activity
     * */
    private void gotoLoginActivity(){
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
        finish();
    }
}

/*
 * TODO EXTRA WORKS DONE
 *  1. price list
 *  2. item price
 *  3. changed price fetching from item price
 *  4. shiftwise report for sales and purchase
 *  5. Report sales and purchase
 *  6. report filtering
 *  7. report exporting to excel sheet
 * */


/*
 * TODO - completed apis
 *  1. Login
 *  2. get category list
 *  3. add category
 *  4. get customer list
 *  5. add customer
 *  6. get uom list
 *  7. get item list
 *  8. get supplier list
 *  9. add supplier
 *  10.List royalty program
 *  11.open shift
 *
 * */


/*
 * TODO API's needed
 *  1. customer edit api
 *  2. add uom api
 *
 * */