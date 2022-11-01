package com.rpos.pos.presentation.ui.sales.order.details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.TaxSlabEntity;
import java.util.List;


public class TaxSpinnerAdapter extends BaseAdapter {

    private List<TaxSlabEntity> taxSlabList;
    private LayoutInflater inflater;

    public TaxSpinnerAdapter(Context mContext, List<TaxSlabEntity> taxSlabList){

        this.taxSlabList = taxSlabList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return taxSlabList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.view_common_spinner_item, null);
        TextView names = (TextView) view.findViewById(R.id.textView);
        names.setText(taxSlabList.get(i).getTaxSlabName());
        return view;
    }
}
