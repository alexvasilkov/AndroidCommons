package com.alexvasilkov.android.commons.texts;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * {@link TextWatcher} implementation that do nothing by default
 */
public class SimpleTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
