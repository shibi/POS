package com.rpos.pos.domain.utils;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * text watcher extended to remove unnecessary methods
 * */
public abstract class TextWatcherExtended implements TextWatcher {

    public abstract void onAfterTextChange(String text);

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        onAfterTextChange(editable.toString());
    }


}
