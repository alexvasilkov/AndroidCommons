package com.azcltd.fluffycommons.converter;

import java.text.ParseException;

public interface Convertable<T> {

    T convert() throws ParseException;

}
