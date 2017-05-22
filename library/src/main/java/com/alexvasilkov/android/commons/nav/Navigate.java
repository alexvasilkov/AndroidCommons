package com.alexvasilkov.android.commons.nav;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alexvasilkov.android.commons.R;
import com.alexvasilkov.android.commons.ui.ContextHelper;

/**
 * Helper class to navigate between activities with animation.
 * <p/>
 * Usage example: <br/>
 * Navigate.from(activity).forResult(requestCode).animate(Navigate.FADE).start(MyActivity.class)
 */
@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class Navigate {

    public static final Transitions DEFAULT = null;

    public static final Transitions NONE = new Transitions(0, 0, 0, 0);

    public static final Transitions FADE = new Transitions(
            R.anim.commons_fade_in, R.anim.commons_hold,
            R.anim.commons_hold, R.anim.commons_fade_out);

    public static final Transitions SLIDE_BOTTOM = new Transitions(
            R.anim.commons_slide_bottom_to_up, R.anim.commons_hold,
            R.anim.commons_hold, R.anim.commons_slide_up_to_bottom);

    public static final Transitions SLIDE_LEFT = new Transitions(
            R.anim.commons_slide_from_right, R.anim.commons_slide_to_left,
            R.anim.commons_slide_from_left, R.anim.commons_slide_to_right);

    public static final Transitions ZOOM = new Transitions(
            R.anim.commons_zoom_enter, R.anim.commons_zoom_exit,
            R.anim.commons_zoom_enter, R.anim.commons_zoom_exit);

    private static final int NO_RESULT_CODE = Integer.MIN_VALUE;


    private final Application application;
    private final Activity activity;
    private final Fragment fragment;
    private final android.support.v4.app.Fragment fragmentSupport;

    private int requestCode = NO_RESULT_CODE;
    private Transitions transition;
    private boolean isNewDocument;

    private Navigate(Application application, Activity activity, Fragment fragment,
            android.support.v4.app.Fragment fragmentSupport) {
        this.application = application;
        this.activity = activity;
        this.fragment = fragment;
        this.fragmentSupport = fragmentSupport;
    }

    /**
     * Initiates navigation starting from given context.<br/>
     * Note, that if this is not an activity context then you can't use {@link #forResult(int)},
     * {@link #animate(Transitions)} and all the {@link #finish() finish(...)}
     * and {@link #navigateUp(Intent) navigateUp(...)} methods.
     */
    public static Navigate from(@NonNull Context context) {
        final Activity activity = ContextHelper.asActivity(context);
        if (activity != null) {
            return from(activity);
        } else {
            return new Navigate(ContextHelper.asApplication(context), null, null, null);
        }
    }

    /**
     * Initiates navigation starting from given activity.
     */
    public static Navigate from(@NonNull Activity activity) {
        return new Navigate(null, activity, null, null);
    }

    /**
     * Initiates navigation starting from given fragment.
     */
    public static Navigate from(@NonNull Fragment fragment) {
        return new Navigate(null, null, fragment, null);
    }

    /**
     * Initiates navigation starting from given fragment.
     */
    public static Navigate from(@NonNull android.support.v4.app.Fragment fragment) {
        return new Navigate(null, null, null, fragment);
    }

    /**
     * Sets activity request code.<br/>
     * If request code is provided method
     * {@link android.app.Activity#startActivityForResult(android.content.Intent, int)} of activity
     * (or similar method of fragment) will be used to start next activity.
     */
    public Navigate forResult(int requestCode) {
        if (application != null) {
            throw new IllegalArgumentException(
                    "You can't start activity for result from application context");
        }
        this.requestCode = requestCode;
        return this;
    }

    /**
     * Sets animation to be played when activity is entered / finished.
     */
    public Navigate animate(@Nullable Transitions transition) {
        if (application != null) {
            throw new IllegalArgumentException(
                    "You can't animate activity transition when using application context");
        }
        this.transition = transition;
        return this;
    }

    /**
     * Sets intent flags so that it is opened outside of app's task.<br/>
     * Useful when redirecting user to external apps.
     */
    public Navigate newDocument() {
        isNewDocument = true;
        return this;
    }

    public ExternalIntents external() {
        return new ExternalIntents(this);
    }

    /**
     * Starts activity by activity class.
     */
    public void start(Class<? extends Activity> activityClass) {
        start(new Intent(getActivity(), activityClass));
    }

    /**
     * Starts activity by intent.
     */
    public void start(Intent intent) {
        intent = setupIntent(intent);

        if (application != null) {
            application.startActivity(intent);
            return; // No transitions, so just return
        }

        if (activity != null) {
            if (requestCode == NO_RESULT_CODE) {
                activity.startActivity(intent);
            } else {
                activity.startActivityForResult(intent, requestCode);
            }
        } else if (fragment != null) {
            if (requestCode == NO_RESULT_CODE) {
                fragment.startActivity(intent);
            } else {
                fragment.startActivityForResult(intent, requestCode);
            }
        } else if (fragmentSupport != null) {
            if (requestCode == NO_RESULT_CODE) {
                fragmentSupport.startActivity(intent);
            } else {
                fragmentSupport.startActivityForResult(intent, requestCode);
            }
        }

        setTransition(false);
    }

    void startExternal(IntentHolder holder) {
        try {
            // Trying to start the intent
            start(holder.getIntent());
        } catch (ActivityNotFoundException e1) {
            if (holder.getFallback() != null) {
                try {
                    // Trying to start fallback intent
                    start(holder.getFallback());
                } catch (ActivityNotFoundException e2) {
                    try {
                        // Displaying empty chooser
                        start(Intent.createChooser(holder.getIntent(), null));
                    } catch (ActivityNotFoundException e3) {
                        e3.printStackTrace();
                    }
                }
            } else {
                try {
                    // Displaying empty chooser
                    start(Intent.createChooser(holder.getIntent(), null));
                } catch (ActivityNotFoundException e4) {
                    e4.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private Intent setupIntent(Intent intent) {
        Intent target = intent;
        // Getting target intent from chooser
        if (Intent.ACTION_CHOOSER.equals(intent.getAction())) {
            target = intent.getParcelableExtra(Intent.EXTRA_INTENT);
        }

        if (isNewDocument) {
            if (Build.VERSION.SDK_INT < 21) {
                target.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            } else {
                target.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            }
        }

        return intent;
    }

    /**
     * Finishes current activity.
     */
    public void finish() {
        getActivity().finish();
        setTransition(true);
    }

    /**
     * Finishes current activity with provided result code.
     */
    public void finish(int resultCode) {
        finish(resultCode, null);
    }

    /**
     * Finishes current activity with provided result code and data.
     */
    public void finish(int resultCode, Intent data) {
        Activity activity = getActivity();
        activity.setResult(resultCode, data);
        activity.finish();
        setTransition(true);
    }

    /**
     * Navigates up to specified activity in the back stack skipping intermediate activities
     */
    public void navigateUp(Class<? extends Activity> activityClass) {
        navigateUp(new Intent(getActivity(), activityClass));
    }

    /**
     * Navigates up to specified activity in the back stack skipping intermediate activities
     */
    public void navigateUp(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        start(intent);
        finish();
    }


    /* Helper methods */

    @NonNull
    private Activity getActivity() {
        Activity result = null;
        if (activity != null) {
            result = activity;
        } else if (fragment != null) {
            result = fragment.getActivity();
        } else if (fragmentSupport != null) {
            result = fragmentSupport.getActivity();
        }
        if (result == null) {
            throw new NullPointerException("No activity or fragment instance");
        }
        return result;
    }

    @NonNull
    Context getContext() {
        return application == null ? getActivity() : application;
    }

    private void setTransition(boolean isFinishing) {
        if (transition != null) {
            if (isFinishing) {
                getActivity().overridePendingTransition(transition.finishEnterAnim,
                        transition.finishExitAnim);
            } else {
                getActivity().overridePendingTransition(transition.startEnterAnim,
                        transition.startExitAnim);
            }
        }
    }


    /**
     * Animations resources holder.
     */
    public static class Transitions {

        final int startEnterAnim;
        final int startExitAnim;
        final int finishEnterAnim;
        final int finishExitAnim;

        public Transitions(int startEnterAnim, int startExitAnim,
                int finishEnterAnim, int finishExitAnim) {
            this.startEnterAnim = startEnterAnim;
            this.startExitAnim = startExitAnim;
            this.finishEnterAnim = finishEnterAnim;
            this.finishExitAnim = finishExitAnim;
        }

    }

}
