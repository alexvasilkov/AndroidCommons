package com.alexvasilkov.android.commons.converters;

public interface Creator<IN, OUT> {

	OUT create(IN obj);

}
