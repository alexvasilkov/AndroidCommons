package com.alexvasilkov.android.commons.texts;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * {@link TextWatcher} implementation that do nothing by default.
 */
@SuppressWarnings("unused") // Public API
public class SimpleTextWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {}

}
