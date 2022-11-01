package com.rpos.pos.domain.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.rpos.pos.R;

public class AppHeader extends LinearLayout {

    public AppHeader(Context context) {
        super(context);
    }

    public AppHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AppHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public AppHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs){

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CommonHeaderAttr, 0, 0);

        String title = a.getString(R.styleable.CommonHeaderAttr_title);
        int leftIconId = a.getResourceId(R.styleable.CommonHeaderAttr_leftIcon,-1);
        int rightIconId = a.getResourceId(R.styleable.CommonHeaderAttr_rightIcon,-1);


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_header_common, this, true);

        if(title!=null && !title.isEmpty()) {
            AppCompatTextView tv_title = view.findViewById(R.id.tv_title);//(AppCompatTextView) getChildAt(0);
            tv_title.setText(title);
        }

        if(leftIconId!=-1) {
            ImageView iv_left = view.findViewById(R.id.iv_leftIcon);

            Drawable left_drawable = context.getResources().getDrawable(leftIconId);
            iv_left.setImageDrawable(left_drawable);
        }

        if(rightIconId!= -1) {
            ImageView iv_right = view.findViewById(R.id.iv_rightIcon);

            Drawable right_drawable = context.getResources().getDrawable(rightIconId);
            iv_right.setImageDrawable(right_drawable);
        }

    }
}
