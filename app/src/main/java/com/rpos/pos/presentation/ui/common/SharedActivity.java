package com.rpos.pos.presentation.ui.common;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.domain.utils.LocalHelper;
import com.rpos.pos.domain.utils.SharedPrefHelper;

public abstract class SharedActivity extends AppCompatActivity implements ISharedActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            int layout = setUpLayout();

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(layout);

            //TODO :- status bar color change not working
            changeStatusBarColor();

            initViewModels();

            initObservers();

            initViews();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void changeStatusBarColor(){
        try {

            if (Build.VERSION.SDK_INT >= 21) {

                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(this.getResources().getColor(R.color.color_primary));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) { hideSystemUi(); }
    }

    private void hideSystemUi()
    {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }


    private void showSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    protected CoreApp getCoreApp(){
        return ((CoreApp) getApplication());
    }

    /**
     * to switch language
     * */
    private void toggleLang(){
        try {

            SharedPrefHelper prefHelper = SharedPrefHelper.getInstance(this);

            String currentLang = prefHelper.getSelectedLang();
            String toggledLang = (currentLang.equals(Constants.LANG_EN))?Constants.LANG_AR:Constants.LANG_EN;
            CoreApp.DEFAULT_LANG = toggledLang;

            prefHelper.setSelectedLang(toggledLang);

            recreate();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void showToast(String msg, Activity activity){
        try{

            // Get your custom_toast.xml layout
            LayoutInflater inflater = activity.getLayoutInflater();

            View layout = inflater.inflate(R.layout.view_app_toast, (ViewGroup) activity.findViewById(R.id.ll_toast_layout_root));

            // set message
            AppCompatTextView text = (AppCompatTextView) layout.findViewById(R.id.tv_message);
            text.setText(msg);


            Toast toast = new Toast(getApplicationContext());
            //toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocalHelper.updateBaseContextLocale(base));
    }


}
