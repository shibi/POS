package com.rpos.pos.presentation.ui.shift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.ShiftRegEntity;

import java.util.List;

public class ShiftSpinnerAdapter extends BaseAdapter {

    private List<ShiftRegEntity> shiftList;
    private LayoutInflater inflater;
    private Context mContext;

    public ShiftSpinnerAdapter(List<ShiftRegEntity> shiftList, Context mContext) {
        this.shiftList = shiftList;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return shiftList.size();
    }

    @Override
    public ShiftRegEntity getItem(int i) {
        return shiftList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ShiftRegEntity shift = shiftList.get(i);

        view = inflater.inflate(R.layout.view_shift_spinner_item, null);
        TextView tvnames = (TextView) view.findViewById(R.id.tv_name);
        TextView tvOpenDate = (TextView) view.findViewById(R.id.tv_opening_date);
        TextView tvOpenTime = (TextView) view.findViewById(R.id.tv_opening_time);

        TextView tvCloseDate = (TextView) view.findViewById(R.id.tv_closing_date);
        TextView tvCloseTime = (TextView) view.findViewById(R.id.tv_closing_time);

        TextView tvStatus = (TextView) view.findViewById(R.id.tv_status);

        String name = mContext.getResources().getString(R.string.shift_label)+shift.getId();
        tvnames.setText(name);

        String open_label = mContext.getResources().getString(R.string.open);
        String close_label = mContext.getResources().getString(R.string.close);

        tvOpenDate.setText(shift.getStartDate());
        tvOpenTime.setText(shift.getStartTime());

        tvCloseDate.setText((shift.getEndDate()!=null)?shift.getEndDate():"none");
        tvCloseTime.setText((shift.getEndTime()!=null)?shift.getEndTime():"none");

        tvStatus.setText((shift.getStatus().equals(Constants.SHIFT_CLOSED))?close_label:open_label);

        return view;
    }
}
