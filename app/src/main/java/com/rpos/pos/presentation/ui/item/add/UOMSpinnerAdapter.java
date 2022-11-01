package com.rpos.pos.presentation.ui.item.add;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rpos.pos.R;
import com.rpos.pos.data.remote.dto.uom.list.UomItem;
import java.util.List;

public class UOMSpinnerAdapter extends BaseAdapter {

    List<UomItem> uomItemList;
    LayoutInflater inflater;

    public UOMSpinnerAdapter(Context context, List<UomItem> uomItemList) {
        this.uomItemList = uomItemList;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return uomItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return uomItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.view_customer_spinner_item, null);
        TextView names = (TextView) view.findViewById(R.id.textView);
        names.setText(uomItemList.get(i).getUomName());
        return view;
    }

}
