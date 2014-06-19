package com.alexvasilkov.android.commons.prefs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks fields that should be saved to {@link android.content.SharedPreferences}.<br/>
 * See also {@link PreferenceObject}
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreferenceField {

    /**
     * Preference key
     */
    public String value();

}