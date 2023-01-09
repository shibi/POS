package com.rpos.pos.presentation.ui.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.rpos.pos.Config;
import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.api.LicenceKeyApiService;
import com.rpos.pos.data.remote.dto.licenceserver.LicenceServerLoginResponse;
import com.rpos.pos.data.remote.dto.login.ApiToken;
import com.rpos.pos.data.remote.dto.login.LoginMessage;
import com.rpos.pos.data.remote.dto.login.LoginResponse;
import com.rpos.pos.domain.requestmodel.login.LoginRequestJson;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.domain.utils.Utility;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.dashboard.DashboardActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends SharedActivity {

    //linear views
    private LinearLayout ll_showPass;
    private LinearLayout ll_lang_arab,ll_lang_eng;

    //user interaction views
    private AppCompatEditText et_username,et_password;
    private AppCompatButton btn_login;

    //password eye icon
    private ImageView iv_eye_icon;

    //show progress
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

        //initializing views
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_continue);

        ll_lang_arab = findViewById(R.id.ll_lang_arab);
        ll_lang_eng = findViewById(R.id.ll_lang_eng);
        ll_showPass = findViewById(R.id.ll_showPass);

        //eye icon to toggle password visibility
        iv_eye_icon = findViewById(R.id.iv_show);

        //progress dialog initialize
        progressDialog = new AppDialogs(this);

        //default selection
        loginType = LOGIN_ERPNEXT;

        //login click
        btn_login.setOnClickListener(view -> onClickLogin());

        //language toggle English and arabic on click
        ll_lang_arab.setOnClickListener(view -> { toggleLanguage(Constants.LANG_AR); });
        ll_lang_eng.setOnClickListener(view -> { toggleLanguage(Constants.LANG_EN); });

        //N.B Need to set a tag first.
        //else, tag will be null and throws error while password toggle
        et_password.setTag("show");
        //to show / hide password
        ll_showPass.setOnClickListener(this::togglePasswordVisibility);

        //check custom logo available, show if any.
        getAvailableCustomLogo();
    }

    @Override
    public void initObservers() {

    }

    /**
     * on click login
     * check login type selected
     * */
    private void onClickLogin(){
        try {
            //check login type selected
            if(loginType == LOGIN_ERPNEXT){
                //if login type equals erp next
                //login with erp next
                loginERPNEXT();
            }else {
                //login using licence server
                loginLicenceServer();
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * login using erp next
     * */
    private void loginERPNEXT(){
        try {

            //check entered values are valid
            if(validateUserEntryFields()){

                //if valid, get the username and password for login request
                String username = et_username.getText().toString();
                String password = et_password.getText().toString();

                //request login through api
                requestLogin(username.trim(),password.trim());
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * licence key validation API
     * */
    private void loginLicenceServer(){
        try {
            //show progress bar
            progressDialog.showProgressBar();
            //get the licence key entered
            String licenceKey = "dummy";//et_serverurl.getText().toString();
            //check key is valid
            if(licenceKey.isEmpty()){
                //if key is not valid, then show message and stop processing
                showToast(getString(R.string.enter_licencekey),LoginActivity.this);
                return;
            }
            //initialize api
            LicenceKeyApiService api = ApiGenerator.createLicenceKeyApiService(LicenceKeyApiService.class);
            Call<LicenceServerLoginResponse> call = api.licenceKeyValidate(licenceKey);
            call.enqueue(new Callback<LicenceServerLoginResponse>() { //
                @Override
                public void onResponse(Call<LicenceServerLoginResponse> call, Response<LicenceServerLoginResponse> response) {
                    //hide progress
                    progressDialog.hideProgressbar();
                    //check response success
                    if(response.isSuccessful()){
                        //if true, redirect to dashboard
                        gotoDashboardActivity();
                    }else {

                    }
                }

                @Override
                public void onFailure(Call<LicenceServerLoginResponse> call, Throwable t) {
                    //on failure
                    //hide progress bar
                    progressDialog.hideProgressbar();
                    //show credential invalid message
                    showToast("Invalid credentials");
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * validate entry fields
     * */
    private boolean validateUserEntryFields(){
        try {

            //get user inputs
            String username = et_username.getText().toString();
            String password = et_password.getText().toString();

            //check username is valid
            if(username.isEmpty()){
                //if not, show error on username field
                et_username.setError("Enter username");
                return false;
            }

            //check password is valid
            if(password.isEmpty() || password.trim().isEmpty()){
                //if not, show error on password field
                et_password.setError("Enter password");
                return false;
            }

            //if valid, return true
            return true;

        }catch (Exception e){
            return false;
        }
    }

    /**
     * ERP next login
     * */
    private void requestLogin(String email,String password){
        try {
            //show progress
            progressDialog.showProgressBar();

            //api service with no token
            ApiService apiService = getCoreApp().getNoTokenWebService();

            //prepare login params to json format for passing in api request
            LoginRequestJson params = new LoginRequestJson();
            params.setUsr(email);
            params.setPwd(password);
            params.setSite(Config.BASE_URL_PREFIX);

            //call the login api with username and password params
            Call<LoginResponse> call = apiService.posLogin(params);
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    try{
                        //hide progress
                        progressDialog.hideProgressbar();
                        //check response success
                        if(response.isSuccessful()){
                            //if true, get the message in body
                            LoginResponse loginResponse = response.body();
                            //check response valid
                            if(loginResponse!=null){
                                //check message is valid
                                if(loginResponse.getMessage()!=null){
                                    //get the login message
                                    LoginMessage loginMessage = loginResponse.getMessage();
                                    //check the status of the message ( TRUE or FALSE )
                                    if(loginMessage.getSuccess()){
                                        //if true, get the api token from response
                                        ApiToken apiToken = loginMessage.getData();
                                        //save api token to shared preference to use in other apis
                                        ((CoreApp)getApplicationContext()).setApiTokens(apiToken.getApiKey(),apiToken.getApiSecret());
                                        //save username
                                        SharedPrefHelper.getInstance(getApplicationContext()).saveUserId(email);

                                        //redirect to dashboard
                                        gotoDashboardActivity();

                                    }else {
                                        showToast(loginMessage.getMessage(),LoginActivity.this);
                                    }
                                }else {
                                    showToast(getString(R.string.login_failed),LoginActivity.this);
                                }
                            }else {
                                showToast(getString(R.string.login_failed),LoginActivity.this);
                            }

                        }else {

                            showToast(getString(R.string.login_failed),LoginActivity.this);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    try{
                        progressDialog.hideProgressbar();
                        String er_message = getString(R.string.login_failed) +". "+ getString(R.string.please_check_internet);
                        showToast(er_message,LoginActivity.this);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to show toast message
     * */
    private void showToast(String msg){
        showToast(msg, LoginActivity.this);
    }

    /**
     *  toggle language
     *  @param language language symbol. Eg : "en"  or "ar"
     * */
    private void toggleLanguage(String language){
        try {
            //to hold the language for to set the app lang
            String setLanguage = "";
            //check whether language is english or arabic
            //if not matching both languages then, set the English language as default in else part
            if(language.equals(Constants.LANG_EN) || language.equals(Constants.LANG_AR)){
                setLanguage = language;
            }else {
                //for no value , set english as default
                setLanguage = Constants.LANG_EN;
            }

            //change the language
            getCoreApp().setDefaultLanguage(this, setLanguage);

            //call activity recreate to reflect the changes
            recreate();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * toggle password visibility
     * */
    private void togglePasswordVisibility(View view){
        try{

            if (et_password.getTag().equals("show")) {
                et_password.setTag("hide");
                et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                iv_eye_icon.setImageResource(R.drawable.ic_eye_hide);
            } else {
                et_password.setTag("show");
                et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                iv_eye_icon.setImageResource(R.drawable.ic_baseline_remove_red_eye_24);
            }

            et_password.setSelection(et_password.getText().length());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * redirect to  dashboard
     * */
    private void gotoDashboardActivity(){
        Intent dashboardIntent = new Intent(this, DashboardActivity.class);
        startActivity(dashboardIntent);
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