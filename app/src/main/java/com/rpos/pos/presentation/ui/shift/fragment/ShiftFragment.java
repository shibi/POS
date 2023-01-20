package com.rpos.pos.presentation.ui.shift.fragment;

import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.ShiftRegEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.domain.requestmodel.shift.ShiftOpenRequestJson;
import com.rpos.pos.domain.responsemodels.shift.ShiftOpenData;
import com.rpos.pos.domain.responsemodels.shift.ShiftOpenMessage;
import com.rpos.pos.domain.responsemodels.shift.ShiftOpenResponse;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.DateTimeUtils;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.presentation.ui.common.SharedFragment;
import com.rpos.pos.presentation.ui.shift.ShiftActivity;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShiftFragment extends SharedFragment {

    private LinearLayout ll_back;
    private AppCompatButton btn_openShift,btn_closeShift;
    private LinearLayout ll_openShiftView,ll_closeShiftView;
    private AppCompatTextView tv_startTime,tv_duration;
    private AppCompatEditText et_openingCash, et_closingCash;
    private LinearLayout ll_shiftReport;

    private AppExecutors appExecutors;
    private AppDatabase localDb;

    private ShiftRegEntity runningShift;
    private Handler timerTickHandler = new Handler();
    private AppDialogs progressDialog;
    private Runnable timerRunnable;
    private boolean allowTimerRun;
    private String currentShiftId;

    private AppDialogs appDialogs;

    @Override
    protected int setContentLayout() {
        return R.layout.fragment_shift;
    }

    @Override
    protected void onCreateView(View getView) {

        //initialize
        ll_back = getView.findViewById(R.id.ll_back);
        ll_openShiftView = getView.findViewById(R.id.ll_openShift);
        ll_closeShiftView = getView.findViewById(R.id.ll_closeShift);
        tv_duration = getView.findViewById(R.id.tv_duration);
        tv_startTime = getView.findViewById(R.id.tv_startTime);
        btn_openShift = getView.findViewById(R.id.btn_openshift);
        btn_closeShift = getView.findViewById(R.id.btn_closeshift);
        et_openingCash = getView.findViewById(R.id.et_opening_cash);
        et_closingCash = getView.findViewById(R.id.et_closing_cash);
        ll_shiftReport = getView.findViewById(R.id.ll_rightMenu);

        //progress bar
        progressDialog = new AppDialogs(getContext());

        //for dialog boxes
        appDialogs = new AppDialogs(getContext());

        //thread
        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        //default initialize
        runningShift = null;
        currentShiftId = Constants.EMPTY;

        //get the last shift from table
        getCurrentShiftDetails();

        //add click listener for open/ close buttons
        //only one button shows at a time
        btn_openShift.setOnClickListener(this::openShift);
        btn_closeShift.setOnClickListener(this::closeShiftClick);

        //To show timer running
        //describe the runnable method for handler
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    //check whether currenct shift is not null
                    if(runningShift==null){
                        return;
                    }

                    //check is allowed to run loop
                    if(!allowTimerRun){
                        return;
                    }

                    //calculate the time difference
                    String timer = DateTimeUtils.getDateDiffInFullPassedTime(runningShift.getTimestamp(),new Date().getTime());

                    //display it in field
                    tv_duration.setText(timer);

                    //restart loop
                    timerTickHandler.postDelayed(this, 1000);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        //on click , redirect to shift report fragment
        ll_shiftReport.setOnClickListener(this::gotoShiftReport);

        //back press listener
        ll_back.setOnClickListener(view -> getActivity().onBackPressed());

    }

    @Override
    protected void initViewModels() {

    }

    @Override
    protected void initObservers() {

    }

    /**
     * Api call to request open shift
     * @param openingCash cash at the beginning of the shift
     * */
    private void openShiftApiCall(int openingCash){
        try {

            showProgress();

            String userId = SharedPrefHelper.getInstance(getContext()).getUserId();
            if(userId.isEmpty()){
                showToast(getString(R.string.invalid_userid));
                return;
            }

            ApiService api = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY, Constants.API_SECRET);
            ShiftOpenRequestJson requestParams = new ShiftOpenRequestJson();
            requestParams.setUserId(userId);
            requestParams.setOpeningCash(openingCash);

            api.openShift(requestParams).enqueue(new Callback<ShiftOpenResponse>() {
                @Override
                public void onResponse(Call<ShiftOpenResponse> call, Response<ShiftOpenResponse> response) {
                    try {

                        hideProgress();

                        if(response.isSuccessful()){
                            ShiftOpenResponse shiftOpenResponse = response.body();
                            if(shiftOpenResponse!=null){
                                ShiftOpenMessage messageData = shiftOpenResponse.getMessage();
                                if(messageData.getSuccess()){
                                    //get the shift data
                                    ShiftOpenData shiftOpenData = messageData.getData();
                                    //save current shift id
                                    getCoreApp().setShiftOpened(shiftOpenData.getOpeningEntryId());
                                    //show the shift open screen
                                    shiftStatusOpen(true);
                                    //start timer to show the elapsed time
                                    startTimer();
                                    return;
                                }
                            }
                        }

                        showToast(getString(R.string.please_check_internet));

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ShiftOpenResponse> call, Throwable t) {
                    showToast(getString(R.string.please_check_internet));
                    hideProgress();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to change the shift view b/w open view  / close view
     *
     * */
    private void shiftStatusOpen(boolean isOpen){

        //check activity is present
        if(getActivity() == null){
            return;
        }
        //UI thread to update the ui components
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //check flag
                    if(isOpen){
                        //shift is opened , reset the timer to default
                        tv_duration.setText("00:00:00");
                        //clear closing cash fields
                        et_closingCash.setText("");
                        //set views
                        setCloseShiftViewVisibility(true);
                        setOpenShiftViewVisibility(false);
                    }else {
                        //shift is closed
                        //show the open shift view
                        //clear cash fields
                        setCloseShiftViewVisibility(false);
                        setOpenShiftViewVisibility(true);
                        et_openingCash.setText("");
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * to get the current shift details
     * */
    private void getCurrentShiftDetails(){
        try {

            String shift = SharedPrefHelper.getInstance(getContext()).getOpenShift();
            if(shift.equals(Constants.EMPTY)){
                //change shift view
                shiftStatusOpen(false);
                currentShiftId = Constants.EMPTY;

            }else {
                //change shift view
                shiftStatusOpen(true);
                currentShiftId = shift;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void openShift(View view){
        try {

            //validate user entered cash fields
            String cash = et_openingCash.getText().toString();
            if(cash.isEmpty()){
                showToast(getString(R.string.enter_amount));
                return;
            }


            Float cash_float = Float.parseFloat(cash);
            int cash_in_integer = Math.round(cash_float);

            //call api to
            openShiftApiCall(cash_in_integer);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To close the shift
     * step 1 : validate cash fields
     * step 2 : shows confirmation dialog for closing
     *          if user confirms close, then closes the shift
     *          if user denies it , do nothing
     * */
    private void closeShiftClick(View view){
        try {

            String cash = et_closingCash.getText().toString();
            if(cash.isEmpty()){
                showToast(getString(R.string.enter_amount));
                return;
            }

            //shows confirmation for closing
            showShiftCloseConfirmationWindow();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to show confirmation for shift close
     * if user allows , then close the shift
     * if denies , do nothing.
     * */
    private void showShiftCloseConfirmationWindow(){
        try {

            String message = getString(R.string.close_shift_confirmation);
            appDialogs.showCommonDualActionAlertDialog(getString(R.string.close_shift), message, new AppDialogs.OnDualActionButtonClickListener() {
                @Override
                public void onClickPositive(String id) {
                    closeShift();
                }

                @Override
                public void onClickNegetive(String id) {
                    //do nothing
                }
            });

        }catch (Exception e){
            throw e;
        }
    }

    /**
     * to close shift
     *  step 1 : update running shift object with closing cash, closing date and time, status to CLOSED,
     *  step 2 : stop timer timer (since shift is closed, no need to show elapsed time)
     *  step 3 : update the row in db
     *  step 4 : show shift closed dialog box
     * */
    private void closeShift(){
        try {

            float fl_endCash = Float.parseFloat(et_closingCash.getText().toString());
            runningShift.setEndingCash(fl_endCash);
            runningShift.setEndDate(DateTimeUtils.getCurrentDate());
            runningShift.setEndTime(DateTimeUtils.getCurrentTime());
            runningShift.setStatus(Constants.SHIFT_CLOSED);
            //stop timer, since shift closed , no need to show timer.
            stopTimer();

            //thread to update shift
            appExecutors.diskIO().execute(() -> {
                try {
                    //update shift
                    localDb.shiftDao().insertShift(runningShift);

                    //set the running shift
                    getCoreApp().setCurrentShift(null);

                    //ui thread to update ui components
                    getActivity().runOnUiThread(() -> {
                        AppDialogs appDialogs = new AppDialogs(getContext());
                        appDialogs.showCommonSuccessDialog(getString(R.string.shift_closed), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                shiftStatusOpen(false);
                            }
                        });

                    });

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To start timer handler loop
     * loop will run in every 1 seconds
     * */
    private void startTimer(){
        try {
            timerTickHandler.postDelayed(timerRunnable, 1000);
            allowTimerRun = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To stop timer
     * remove callback to avoid loop running
     * */
    private void stopTimer(){
        try {
            allowTimerRun = false;
            timerTickHandler.removeCallbacks(timerRunnable);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To goto shift report fragment
     * */
    private void gotoShiftReport(View view){
        ((ShiftActivity)getContext()).gotoShiftReportFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {

            //stop timer on destroy
            //N.B , required. If not stopped , timer will run even after activity closed(memory leak)
            //only applicable in case of shift is opened and timer running
            stopTimer();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setOpenShiftViewVisibility(boolean isShow){
        ll_openShiftView.setVisibility((isShow)?View.VISIBLE:View.GONE);
    }

    private void setCloseShiftViewVisibility(boolean isShow){
        ll_closeShiftView.setVisibility((isShow)?View.VISIBLE:View.GONE);
    }

    private void showProgress(){
        progressDialog.showProgressBar();
    }
    private void hideProgress(){
        progressDialog.hideProgressbar();
    }

}
