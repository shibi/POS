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
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.DateTimeUtils;
import com.rpos.pos.presentation.ui.common.SharedFragment;
import java.util.Date;

public class ShiftFragment extends SharedFragment {

    private LinearLayout ll_back;
    private AppCompatButton btn_openShift,btn_closeShift;
    private LinearLayout ll_openShiftView,ll_closeShiftView;
    private AppCompatTextView tv_startTime,tv_duration;
    private AppCompatEditText et_openingCash, et_closingCash;

    private AppExecutors appExecutors;
    private AppDatabase localDb;

    private ShiftRegEntity runningShift;
    private Handler timerTickHandler = new Handler();
    private AppDialogs progressDialog;
    private Runnable timerRunnable;
    private boolean allowTimerRun;

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
        //progress bar
        progressDialog = new AppDialogs(getContext());

        //for dialog boxes
        appDialogs = new AppDialogs(getContext());

        //thread
        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        //default initialize
        runningShift = null;

        //get the last shift from table
        getLastShift();


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
     * to fetch the last opened shift
     * get the last entry in table, and check whether the status of the shift is open!
     * if open , then start timer and display time elapsed since beginning.
     * if closed, show the shift open view
     * */
    private void getLastShift(){
        try {
            //show progress
            progressDialog.showProgressBar();
            //new thread to db operation
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //get the last entry in shift table
                        ShiftRegEntity lastShift = localDb.shiftDao().getLastEntryInShift();
                        //check whether last item is not empty ( it can be null )
                        if(lastShift!=null){
                            //check the status is open
                            if(lastShift.getStatus().equals(Constants.SHIFT_OPEN)){
                                //shift is opened , so start the timer to show elapsed time since starting
                                runningShift = lastShift;
                                //display start time
                                tv_startTime.setText(runningShift.getStartDate() +" \n "+ runningShift.getStartTime());

                                //change shift view
                                shiftStatusOpen(true);

                                //start timer to show the elapsed time since starting
                                startTimer();

                             }else {
                                // last shift status is closed,
                                // so just show the shift open view
                                shiftStatusOpen(false);
                             }
                         }else {
                            //last shift entry is null, so show the shift open view
                            //usually runningShift is null when user opens shift screen for the first time, where there is no entry in table.
                            shiftStatusOpen(false);
                         }

                         //hide progress
                         getActivity().runOnUiThread(() -> progressDialog.hideProgressbar());

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
     * To open shift
     * step 1 : validate cash entry
     * step 2 : prepare shift object to save
     * step 3 : insert the shift object to local db with status OPEN
     * step 4 : change view to shift running view .
     * step 5 : start timer to show the time elapsed
     * */
    private void openShift(View view){
        try {

            //validate user entered cash fields
            String cash = et_openingCash.getText().toString();
            if(cash.isEmpty()){
                showToast(getString(R.string.enter_amount));
                return;
            }

            //progress show
            progressDialog.showProgressBar();

            //prepare shift object to insert to database
            //set opening cash,start time, startDate, shift status OPEN , timestamp before saving
            float flCash = Float.parseFloat(cash);
            final ShiftRegEntity shift = new ShiftRegEntity();
            shift.setBeginningCash(flCash);
            shift.setShiftType(1);
            shift.setStartDate(DateTimeUtils.getCurrentDate());
            shift.setStartTime(DateTimeUtils.getCurrentTime());
            shift.setStatus(Constants.SHIFT_OPEN);
            shift.setTimestamp(new Date().getTime());

            //use new thread for db operation
            //insert object to db
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        //insert
                        localDb.shiftDao().insertShift(shift);

                        //ui thread to update the ui
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //hide progress
                                    progressDialog.hideProgressbar();
                                    //show the shift running view
                                    shiftStatusOpen(true);
                                    //set the running shift object
                                    runningShift = shift;

                                    //set the running shift
                                    getCoreApp().setCurrentShift(shift);

                                    //start timer to show the elapsed time
                                    startTimer();

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });

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

}
