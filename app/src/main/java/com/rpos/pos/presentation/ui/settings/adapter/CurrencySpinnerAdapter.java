package com.rpos.pos.presentation.ui.settings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.CurrencyItem;

import java.util.List;

public class CurrencySpinnerAdapter extends BaseAdapter {

    private List<CurrencyItem> currencyItemList;
    private LayoutInflater inflater;


    public CurrencySpinnerAdapter(Context mContext,List<CurrencyItem> currencyItemList) {
        this.currencyItemList = currencyItemList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return currencyItemList.size();
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
        names.setText(currencyItemList.get(i).getSymbol());
        return view;
    }
}
