package com.rpos.pos.presentation.ui.shift;

import androidx.fragment.app.Fragment;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.shift.fragment.ShiftFragment;
import com.rpos.pos.presentation.ui.shift.fragment.ShiftReportFragment;

public class ShiftActivity extends SharedActivity {

    private String currentFragmentTag = "";

    @Override
    public int setUpLayout() {
        return R.layout.activity_shift;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        //load shift fragment
        loadShiftFragment();

    }

    @Override
    public void initObservers() {

    }

    private void loadShiftFragment(){
        loadFragment(new ShiftFragment(), Constants.FRAGMENT_SHIFT);
    }

    public void gotoShiftReportFragment(){
        loadFragment(new ShiftReportFragment(), Constants.FRAGMENT_SHIFT_REPORT);
    }

    public void loadFragment(Fragment fragment, String tag) {
        try {

            currentFragmentTag = tag;

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment, tag)
                    .commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}