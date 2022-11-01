package com.rpos.pos.presentation.viewmodel.common;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.CoreApp;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.remote.api.ApiService;


public class AppViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> loadingState = new MutableLiveData<>();
    private boolean isLoading;

    protected ApiService webService;
    protected AppDatabase localDb;
    protected AppExecutors appExecutors;

    public AppViewModel(@NonNull Application application) {
        super(application);
    }

    /*protected void onInitDependencies(Application application,ApiService _webService, AppDatabase _localDb,AppExecutors _appExecutors){
        this.localDb = ((CoreApp)application).getLocalDb();
        this.webService = ((CoreApp)application).getWebService();
        this.appExecutors = ((CoreApp)application).getAppExecutors();
    }*/

    protected void onInitDependencies(ApiService _webService, AppDatabase _localDb,AppExecutors _appExecutors){
        this.localDb = _localDb;
        this.webService = _webService;
        this.appExecutors = _appExecutors;
    }

    public MutableLiveData<Boolean> getLoadingState(){
        return loadingState;
    }

    public void setLoadingState(boolean isLoading){
        this.isLoading = isLoading;
        loadingState.setValue(isLoading);
    }
}
