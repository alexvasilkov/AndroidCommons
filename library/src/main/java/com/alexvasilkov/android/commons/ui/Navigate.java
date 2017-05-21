package com.alexvasilkov.android.commons.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alexvasilkov.android.commons.R;

/**
 * Helper class to navigate between activities with animation.
 * <p/>
 * Usage example: <br/>
 * Navigate.from(activity).forResult(requestCode).animate(Navigate.FADE).start(MyActivity.class)
 */
@SuppressWarnings({ "WeakerAccess", "unused" }) // Public API
public class Navigate {

    public static final NavTransition DEFAULT = null;

    public static final NavTransition NONE = new NavTransition(0, 0, 0, 0);

    public static final NavTransition FADE = new NavTransition(
            R.anim.commons_fade_in, R.anim.commons_hold,
            R.anim.commons_hold, R.anim.commons_fade_out);

    public static final NavTransition SLIDE_BOTTOM = new NavTransition(
            R.anim.commons_slide_bottom_to_up, R.anim.commons_hold,
            R.anim.commons_hold, R.anim.commons_slide_up_to_bottom);

    public static final NavTransition SLIDE_LEFT = new NavTransition(
            R.anim.commons_slide_from_right, R.anim.commons_slide_to_left,
            R.anim.commons_slide_from_left, R.anim.commons_slide_to_right);

    public static final NavTransition ZOOM = new NavTransition(
            R.anim.commons_zoom_enter, R.anim.commons_zoom_exit,
            R.anim.commons_zoom_enter, R.anim.commons_zoom_exit);

    private static final int NO_RESULT_CODE = Integer.MIN_VALUE;


    private final Activity activity;
    private final Fragment fragment;
    private final android.support.v4.app.Fragment fragmentSupport;

    private int requestCode = NO_RESULT_CODE;
    private NavTransition transition;

    private Navigate(Activity activity, Fragment fragment,
            android.support.v4.app.Fragment fragmentSupport) {
        this.activity = activity;
        this.fragment = fragment;
        this.fragmentSupport = fragmentSupport;
    }

    /**
     * Initiates navigation starting from given activity.
     */
    public static Navigate from(@NonNull Activity activity) {
        return new Navigate(activity, null, null);
    }

    /**
     * Initiates navigation starting from given fragment.
     */
    public static Navigate from(@NonNull Fragment fragment) {
        return new Navigate(null, fragment, null);
    }

    /**
     * Initiates navigation starting from given fragment.
     */
    public static Navigate from(@NonNull android.support.v4.app.Fragment fragment) {
        return new Navigate(null, null, fragment);
    }

    /**
     * Sets activity request code.<br/>
     * If request code is provided method
     * {@link android.app.Activity#startActivityForResult(android.content.Intent, int)} of activity
     * (or similar method of fragment) will be used to start next activity.
     */
    public Navigate forResult(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    /**
     * Sets animation to be played when activity is entered / finished.
     */
    public Navigate animate(@Nullable NavTransition transition) {
        this.transition = transition;
        return this;
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
     * Animations resources holder
     */
    public static class NavTransition {

        final int startEnterAnim;
        final int startExitAnim;
        final int finishEnterAnim;
        final int finishExitAnim;

        public NavTransition(int startEnterAnim, int startExitAnim,
                int finishEnterAnim, int finishExitAnim) {
            this.startEnterAnim = startEnterAnim;
            this.startExitAnim = startExitAnim;
            this.finishEnterAnim = finishEnterAnim;
            this.finishExitAnim = finishExitAnim;
        }

    }

}
