package com.alexvasilkov.android.commons.nav.builders;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.alexvasilkov.android.commons.nav.IntentBuilder;
import com.alexvasilkov.android.commons.nav.IntentHolder;
import com.alexvasilkov.android.commons.nav.Navigate;

@SuppressWarnings("unused") // Public API
public class YouTubeIntentBuilder extends IntentBuilder {

    private String videoId;

    public YouTubeIntentBuilder(@NonNull Navigate navigator) {
        super(navigator);
    }

    public YouTubeIntentBuilder video(@NonNull String videoId) {
        this.videoId = videoId;
        return this;
    }

    @Override
    protected IntentHolder build(Navigate navigator) {
        if (videoId == null) {
            throw new NullPointerException("Missing video id when launching YouTube");
        }

        final Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("vnd.youtube:" + videoId));

        final Intent fallback = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://m.youtube.com/watch?v=" + videoId));

        return new IntentHolder(navigator, intent, fallback);
    }

}
