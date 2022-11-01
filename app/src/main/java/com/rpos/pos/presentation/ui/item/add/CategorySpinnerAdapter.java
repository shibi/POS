package com.rpos.pos.presentation.ui.item.add;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rpos.pos.R;
import com.rpos.pos.data.remote.dto.category.list.CategoryItem;

import java.util.List;

public class CategorySpinnerAdapter extends BaseAdapter {

    private List<CategoryItem> categoryItemList;
    private LayoutInflater inflater;

    public CategorySpinnerAdapter(Context mContext, List<CategoryItem> categoryItemList){

        this.categoryItemList = categoryItemList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return categoryItemList.size();
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
        view = inflater.inflate(R.layout.view_customer_spinner_item, null);
        TextView names = (TextView) view.findViewById(R.id.textView);
        names.setText(categoryItemList.get(i).getCategory());
        return view;
    }
}
