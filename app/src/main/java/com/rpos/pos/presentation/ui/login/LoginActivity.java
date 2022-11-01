package com.rpos.pos.presentation.ui.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;

import com.rpos.pos.Config;
import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.api.LicenceKeyApiService;
import com.rpos.pos.data.remote.dto.ZatcaResponse;
import com.rpos.pos.data.remote.dto.licenceserver.LicenceServerLoginResponse;
import com.rpos.pos.data.remote.dto.login.ApiToken;
import com.rpos.pos.data.remote.dto.login.LoginMessage;
import com.rpos.pos.data.remote.dto.login.LoginResponse;
import com.rpos.pos.domain.requestmodel.login.LoginRequest;
import com.rpos.pos.domain.requestmodel.login.LoginRequestJson;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.domain.utils.Utility;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.dashboard.DashboardActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends SharedActivity {

    private RadioGroup rg_logintype;
    private AppCompatEditText et_serverurl;
    private LinearLayout ll_showPass;
    private LinearLayout ll_lang_arab,ll_lang_eng;
    private AppCompatEditText et_username,et_password;
    private AppCompatButton btn_login;
    private ImageView iv_pass_show;

    private AppDialogs progressDialog;

    private int loginType;
    private static final int LOGIN_ERPNEXT = 1;
    private static final int LOGIN_LICENCE_SERVER = 2;

    @Override
    public int setUpLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        rg_logintype = findViewById(R.id.rg_logintype);
        et_serverurl = findViewById(R.id.et_server_url);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_continue);
        ll_showPass = findViewById(R.id.ll_showPass);
        iv_pass_show= findViewById(R.id.iv_show);
        ll_lang_arab = findViewById(R.id.ll_lang_arab);
        ll_lang_eng = findViewById(R.id.ll_lang_eng);

        progressDialog = new AppDialogs(this);


        //default selection
        loginType = LOGIN_ERPNEXT;
        rg_logintype.check(R.id.rb_erpnext);
        //listener for radio button check change tracking
        rg_logintype.setOnCheckedChangeListener(radionButtonListener);

        //password toggle
        ll_showPass.setOnClickListener(this::togglePasswordVisibility);

        //login
        btn_login.setOnClickListener(view -> gotoDashboardActivity());


        ll_lang_arab.setOnClickListener(view -> { toggleLanguage(Constants.LANG_AR); });
        ll_lang_eng.setOnClickListener(view -> { toggleLanguage(Constants.LANG_EN); });

        //check custom logo available, show if any.
        getAvailableCustomLogo();
    }

    @Override
    public void initObservers() {

    }

    private void gotoDashboardActivity(){
        Intent dashboardIntent = new Intent(this, DashboardActivity.class);
        startActivity(dashboardIntent);
    }

    private void onClickLogin(){
        try {

            if(loginType == LOGIN_ERPNEXT){
                loginERPNEXT();
            }else {
                loginLicenceServer();
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loginERPNEXT(){
        try {

            if(isValid()){
                String username = et_username.getText().toString();
                String password = et_password.getText().toString();
                requestLogin(username,password.trim());
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loginLicenceServer(){
        try {

            progressDialog.showProgressBar();

            String licenceKey = et_serverurl.getText().toString();
            if(licenceKey.isEmpty()){
                showToast(getString(R.string.enter_licencekey),LoginActivity.this);
                return;
            }

            LicenceKeyApiService api = ApiGenerator.createLicenceKeyApiService(LicenceKeyApiService.class);
            Call<LicenceServerLoginResponse> call = api.licenceKeyValidate(licenceKey);
            call.enqueue(new Callback<LicenceServerLoginResponse>() {
                @Override
                public void onResponse(Call<LicenceServerLoginResponse> call, Response<LicenceServerLoginResponse> response) {
                    Log.e("-----------","res"+response.isSuccessful());
                    progressDialog.hideProgressbar();

                    if(response.isSuccessful()){

                        gotoDashboardActivity();

                    }else {

                    }
                }

                @Override
                public void onFailure(Call<LicenceServerLoginResponse> call, Throwable t) {
                    Log.e("-----------","failed");
                    progressDialog.hideProgressbar();
                    showToast("Invalid credentials");
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isValid(){
        try {
            String username = et_username.getText().toString();
            String password = et_password.getText().toString();

            if(username.isEmpty()){
                et_username.setError("Enter username");
                return false;
            }

            if(password.isEmpty() || password.trim().isEmpty()){
                et_password.setError("Enter password");
                return false;
            }

            return true;

        }catch (Exception e){
            return false;
        }
    }

    private void requestLogin(String email,String password){
        try {

            progressDialog.showProgressBar();

            ApiService apiService = getCoreApp().getNoTokenWebService();

            LoginRequestJson params = new LoginRequestJson();
            params.setUsr(email);
            params.setPwd(password);

            Call<LoginResponse> call = apiService.posLogin(params);
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    try{

                        progressDialog.hideProgressbar();

                        Log.e("----------","onResponse");
                        if(response.isSuccessful()){

                            LoginResponse loginResponse = response.body();

                            if(loginResponse!=null){

                                if(loginResponse.getMessage()!=null){

                                    LoginMessage loginMessage = loginResponse.getMessage();

                                    if(loginMessage.getSuccess()){

                                        ApiToken apiToken = loginMessage.getData();
                                        ((CoreApp)getApplicationContext()).setApiTokens(apiToken.getApiKey(),apiToken.getApiSecret());
                                        SharedPrefHelper.getInstance(getApplicationContext()).saveUserId(email);

                                        gotoDashboardActivity();

                                    }else {
                                        showToast(loginMessage.getMessage(),LoginActivity.this);
                                    }
                                }else {
                                    showToast("Login failed",LoginActivity.this);
                                }
                            }else {
                                showToast("Login failed",LoginActivity.this);
                            }

                        }else {

                            showToast("Login response failed",LoginActivity.this);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    try{
                        progressDialog.hideProgressbar();
                        showToast("Login failed. Please check your internet",LoginActivity.this);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void togglePasswordVisibility(View view){
        try {

            if(et_password.getTag()==null || et_password.getTag().equals("show")){
                et_password.setTag("hide");
                et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                iv_pass_show.setImageResource(R.drawable.ic_eye_hide);
            }else {
                et_password.setTag("show");
                et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                iv_pass_show.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private RadioGroup.OnCheckedChangeListener radionButtonListener = new RadioGroup.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int radiobutton_id) {
            try {

                switch (radiobutton_id)
                {
                    case R.id.rb_erpnext:

                        loginType = LOGIN_ERPNEXT;
                        et_serverurl.setVisibility(View.GONE);

                        et_username.setVisibility(View.VISIBLE);
                        et_password.setVisibility(View.VISIBLE);

                        break;
                    case R.id.rb_licenceserver:

                        loginType = LOGIN_LICENCE_SERVER;
                        et_serverurl.setVisibility(View.VISIBLE);

                        et_username.setVisibility(View.GONE);
                        et_password.setVisibility(View.GONE);

                        break;
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private void showToast(String msg){
        showToast(msg, LoginActivity.this);
    }

    private void toggleLanguage(final String language){
        try {

            Log.e("------------","Lang > "+language);

            switch (language)
            {
                case Constants.LANG_EN:
                    getCoreApp().setDefaultLanguage(this, Constants.LANG_EN);
                    break;
                case Constants.LANG_AR:
                    getCoreApp().setDefaultLanguage(this, Constants.LANG_AR);
                    break;
            }

            recreate();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * check custom logo available and show
     * */
    private void getAvailableCustomLogo(){
        try {

            //check saved logo path available
            String appIconPath = SharedPrefHelper.getInstance(this).getAppLogoPath();
            //check whether icon path is valid
            if(appIconPath != null && !appIconPath.isEmpty()){
                //load saved image from external storage
                Bitmap logoBitmap = getCoreApp().loadImageFromStorage(appIconPath);
                //check image is valid
                if(logoBitmap!= null){
                    //get the logo view
                    ImageView iv_logo = findViewById(R.id.iv_appLogo);
                    //display image in logo view
                    iv_logo.setImageBitmap(logoBitmap);
                }
            }

        }catch (Exception e){
            throw e;
        }
    }

}