package com.alexvasilkov.android.commons.nav.builders;

import android.content.Intent;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alexvasilkov.android.commons.nav.IntentBuilder;
import com.alexvasilkov.android.commons.nav.IntentHolder;
import com.alexvasilkov.android.commons.nav.Navigate;

import java.util.TimeZone;

@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class AddToCalendarIntentBuilder extends IntentBuilder {

    private long beginTime;
    private long endTime;
    private TimeZone tz;
    private boolean isAllDay;
    private String title;
    private String description;
    private String location;

    public AddToCalendarIntentBuilder(@NonNull Navigate navigator) {
        super(navigator);
    }

    public AddToCalendarIntentBuilder begin(long beginTime) {
        this.beginTime = beginTime;
        return this;
    }

    public AddToCalendarIntentBuilder end(long endTime) {
        this.endTime = endTime;
        return this;
    }

    public AddToCalendarIntentBuilder timezone(@Nullable TimeZone tz) {
        this.tz = tz;
        return this;
    }

    public AddToCalendarIntentBuilder allDay(boolean isAllDay) {
        this.isAllDay = isAllDay;
        return this;
    }

    public AddToCalendarIntentBuilder allDay() {
        return allDay(true);
    }

    public AddToCalendarIntentBuilder title(@NonNull String title) {
        this.title = title;
        return this;
    }

    public AddToCalendarIntentBuilder description(String description) {
        this.description = description;
        return this;
    }

    public AddToCalendarIntentBuilder location(String location) {
        this.location = location;
        return this;
    }

    @Override
    protected IntentHolder build(Navigate navigator) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);

        if (tz != null) {
            // Setting timezone is not directly supported, so we will add timezone offset manually
            if (beginTime != 0) {
                final long offsetDiff = tz.getOffset(beginTime)
                        - TimeZone.getDefault().getOffset(beginTime);
                beginTime += offsetDiff;
            }
            if (endTime != 0) {
                final long offsetDiff = tz.getOffset(endTime)
                        - TimeZone.getDefault().getOffset(endTime);
                endTime += offsetDiff;
            }
        }

        if (beginTime == 0) {
            // Begin time seems to be mandatory for some apps
            beginTime = System.currentTimeMillis();
        }

        if (endTime == 0) {
            // End time seems to be mandatory for some apps
            endTime = beginTime + 60 * 60 * 1000;
        }

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);

        if (isAllDay) {
            intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
        }
        if (title != null) {
            intent.putExtra(CalendarContract.Events.TITLE, title);
        }
        if (description != null) {
            intent.putExtra(CalendarContract.Events.DESCRIPTION, description);
        }
        if (location != null) {
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, location);
        }

        return new IntentHolder(navigator, intent);
    }

}
