package com.azcltd.fluffycommons.converters;

import java.text.ParseException;

public interface Convertable<T> {

    T convert() throws ParseException;

}
