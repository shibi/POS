package com.rpos.pos.presentation.viewmodel.login;

import android.app.Application;

import androidx.annotation.NonNull;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.CoreApp;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.domain.repository.login.LoginRepository;
import com.rpos.pos.presentation.viewmodel.common.AppViewModel;

public class LoginViewModel extends AppViewModel {

    private LoginRepository loginRepository;

    public LoginViewModel(@NonNull Application application) {

       super(application);

       onInitDependencies(ApiGenerator.createNoTokenApiService(ApiService.class),((CoreApp)application).getLocalDb(),((CoreApp)application).getAppExecutors());

       loginRepository = new LoginRepository(webService,localDb,appExecutors);

    }




}
