package com.alexvasilkov.android.commons.converters;

import java.text.ParseException;

public interface Convertable<T> {

    T convert() throws ParseException;

}
