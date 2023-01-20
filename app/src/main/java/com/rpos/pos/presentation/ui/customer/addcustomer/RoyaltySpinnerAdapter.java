package com.rpos.pos.presentation.ui.customer.addcustomer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rpos.pos.R;
import com.rpos.pos.data.remote.dto.royalty.RoyaltyProgram;
import com.rpos.pos.data.remote.dto.uom.list.UomItem;

import java.util.List;

public class RoyaltySpinnerAdapter extends BaseAdapter {

    List<RoyaltyProgram> royaltyProgramsList;
    LayoutInflater inflater;

    public RoyaltySpinnerAdapter(Context context, List<RoyaltyProgram> _royaltyProgramsList) {
        this.royaltyProgramsList = _royaltyProgramsList;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return royaltyProgramsList.size();
    }

    @Override
    public Object getItem(int i) {
        return royaltyProgramsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.view_customer_spinner_item, null);
        TextView names = (TextView) view.findViewById(R.id.textView);
        names.setText(royaltyProgramsList.get(i).getLoyaltyProgramName());
        return view;
    }

}
