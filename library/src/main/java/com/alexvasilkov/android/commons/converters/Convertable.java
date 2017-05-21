package com.alexvasilkov.android.commons.converters;

import java.text.ParseException;

@SuppressWarnings("WeakerAccess") // Public API
public interface Convertable<T> {

    T convert() throws ParseException;

}
