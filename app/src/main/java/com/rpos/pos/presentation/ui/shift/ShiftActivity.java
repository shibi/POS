package com.rpos.pos.presentation.ui.shift;

import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.shift.fragment.ShiftFragment;

public class ShiftActivity extends SharedActivity {

    private String currentFragmentTag = "";
    private RadioGroup rb_switchTab;
    private ImageView iv_back;


    @Override
    public int setUpLayout() {
        return R.layout.activity_shift;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        rb_switchTab = findViewById(R.id.rb_switchTab);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setVisibility(View.VISIBLE);

        RadioButton rb_itemList = findViewById(R.id.rb_left);
        RadioButton rb_itemAdd = findViewById(R.id.rb_right);

        rb_itemList.setText("Shift view");
        rb_itemAdd.setText(R.string.shift_report);

        //check the first item
        rb_itemList.setChecked(true);

        //load shift fragment
        loadShiftFragment();

        //listener for radio button click
        rb_switchTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int radioButtonId) {
                try {

                    switch (radioButtonId)
                    {
                        case R.id.rb_left:

                            loadShiftFragment();

                            break;
                        case R.id.rb_right:

                            //gotoAddItemActivity();

                            break;
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });



        //back arrow press
        iv_back.setOnClickListener(view -> onBackPressed());

    }

    @Override
    public void initObservers() {

    }

    private void loadShiftFragment(){
        loadFragment(new ShiftFragment(), Constants.FRAGMENT_SHIFT);
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