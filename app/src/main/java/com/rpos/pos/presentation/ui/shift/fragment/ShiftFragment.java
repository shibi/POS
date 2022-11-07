package com.rpos.pos.presentation.ui.shift.fragment;

import android.os.Handler;
import android.view.View;
import android.widget.EditText;
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

    @Override
    protected int setContentLayout() {
        return R.layout.fragment_shift;
    }

    @Override
    protected void onCreateView(View getView) {

        ll_back = getView.findViewById(R.id.ll_back);
        ll_openShiftView = getView.findViewById(R.id.ll_openShift);
        ll_closeShiftView = getView.findViewById(R.id.ll_closeShift);
        tv_duration = getView.findViewById(R.id.tv_duration);
        tv_startTime = getView.findViewById(R.id.tv_startTime);

        progressDialog = new AppDialogs(getContext());

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        btn_openShift = getView.findViewById(R.id.btn_openshift);
        btn_closeShift = getView.findViewById(R.id.btn_closeshift);
        et_openingCash = getView.findViewById(R.id.et_opening_cash);
        et_closingCash = getView.findViewById(R.id.et_closing_cash);

        runningShift = null;

        getLastShift();


        btn_openShift.setOnClickListener(this::openShift);
        btn_closeShift.setOnClickListener(this::closeShift);


        timerRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if(runningShift==null){
                        return;
                    }

                    if(!allowTimerRun){
                        return;
                    }

                    String timer = DateTimeUtils.getDateDiffInFullPassedTime(runningShift.getTimestamp(),new Date().getTime());
                    tv_duration.setText(timer);


                    timerTickHandler.postDelayed(this::run, 1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };


        ll_back.setOnClickListener(view -> getActivity().onBackPressed());

    }

    @Override
    protected void initViewModels() {

    }

    @Override
    protected void initObservers() {

    }

    private void getLastShift(){
        try {

            progressDialog.showProgressBar();

            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                         ShiftRegEntity lastShift = localDb.shiftDao().getLastEntryInShift();
                         if(lastShift!=null){
                             if(lastShift.getStatus().equals(Constants.SHIFT_OPEN)){
                                 runningShift = lastShift;
                                 tv_startTime.setText(runningShift.getStartDate() +" \n "+ runningShift.getStartTime());
                                 shiftStatusOpen(true);

                                 startTimer();

                             }else {
                                 shiftStatusOpen(false);
                             }
                         }else {
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

    private void shiftStatusOpen(boolean isOpen){

        if(getActivity() == null){
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {

                    if(isOpen){
                        tv_duration.setText("00:00:00");
                        et_closingCash.setText("");
                        setCloseShiftViewVisibility(true);
                        setOpenShiftViewVisibility(false);
                    }else {
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


    private void openShift(View view){
        try {

            String cash = et_openingCash.getText().toString();
            if(cash.isEmpty()){
                showToast(getString(R.string.enter_amount));
                return;
            }

            progressDialog.showProgressBar();

            float flCash = Float.parseFloat(cash);

            final ShiftRegEntity shift = new ShiftRegEntity();
            shift.setBeginningCash(flCash);
            shift.setShiftType(1);
            shift.setStartDate(DateTimeUtils.getCurrentDate());
            shift.setStartTime(DateTimeUtils.getCurrentTime());
            shift.setStatus(Constants.SHIFT_OPEN);
            shift.setTimestamp(new Date().getTime());

            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        localDb.shiftDao().insertShift(shift);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    progressDialog.hideProgressbar();
                                    shiftStatusOpen(true);

                                    runningShift = shift;

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

    private void closeShift(View view){
        try {

            String cash = et_closingCash.getText().toString();
            if(cash.isEmpty()){
                showToast(getString(R.string.enter_amount));
                return;
            }

            float fl_endCash = Float.parseFloat(cash);
            runningShift.setEndingCash(fl_endCash);
            runningShift.setEndDate(DateTimeUtils.getCurrentDate());
            runningShift.setEndTime(DateTimeUtils.getCurrentTime());
            runningShift.setStatus(Constants.SHIFT_CLOSED);

            stopTimer();

            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        localDb.shiftDao().insertShift(runningShift);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AppDialogs appDialogs = new AppDialogs(getContext());
                                appDialogs.showCommonSuccessDialog(getString(R.string.shift_closed), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        shiftStatusOpen(false);
                                    }
                                });
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

    private void startTimer(){
        try {
            timerTickHandler.postDelayed(timerRunnable, 1000);
            allowTimerRun = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
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
