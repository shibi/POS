package com.rpos.pos.domain.repository.login;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.remote.api.ApiService;

public class LoginRepository {

    private ApiService webService;
    private AppDatabase localDb;
    private AppExecutors appExecutors;


    public LoginRepository(ApiService webService, AppDatabase localDb, AppExecutors appExecutors) {
        this.webService = webService;
        this.localDb = localDb;
        this.appExecutors = appExecutors;
    }




}
