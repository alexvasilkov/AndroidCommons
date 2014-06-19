package com.alexvasilkov.android.commons.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import com.alexvasilkov.android.commons.R;

/**
 * Helper class to navigate between activities with animation.
 * <p/>
 * Sample usage: <br/>
 * {@code NavHelper.from(activity).forResult(requestCode).animate(NavHelper.FADE).start(MyActivity.class)}
 */
public class NavHelper {

    public static final NavTransition DEFAULT = null;

    public static final NavTransition NONE = new NavTransition(0, 0, 0, 0);

    public static final NavTransition FADE = new NavTransition(
            R.anim.commons_fade_in, R.anim.commons_hold, R.anim.commons_hold, R.anim.commons_fade_out);

    public static final NavTransition SLIDE_BOTTOM = new NavTransition(
            R.anim.commons_slide_bottom_to_up, R.anim.commons_hold, R.anim.commons_hold, R.anim.commons_slide_up_to_bottom);

    public static final NavTransition SLIDE_LEFT = new NavTransition(
            R.anim.commons_slide_from_right, R.anim.commons_slide_to_left, R.anim.commons_slide_from_left, R.anim.commons_slide_to_right);

    public static final NavTransition ZOOM = new NavTransition(
            R.anim.commons_zoom_enter, R.anim.commons_zoom_exit, R.anim.commons_zoom_enter, R.anim.commons_zoom_exit);

    private static final int NO_RESULT_CODE = Integer.MIN_VALUE;


    private final Activity mActivity;
    private final Fragment mFragment;
    private final android.support.v4.app.Fragment mFragmentSupport;

    private int mRequestCode = NO_RESULT_CODE;
    private NavTransition mTransition;

    private NavHelper(Activity activity, Fragment fragment, android.support.v4.app.Fragment fragmentSupport) {
        mActivity = activity;
        mFragment = fragment;
        mFragmentSupport = fragmentSupport;
    }

    /**
     * Initiates navigation starting from given activity
     */
    public static NavHelper from(Activity activity) {
        return new NavHelper(activity, null, null);
    }

    /**
     * Initiates navigation starting from given fragment
     */
    public static NavHelper from(Fragment fragment) {
        return new NavHelper(null, fragment, null);
    }

    /**
     * Initiates navigation starting from given fragment
     */
    public static NavHelper from(android.support.v4.app.Fragment fragment) {
        return new NavHelper(null, null, fragment);
    }

    /**
     * Sets activity request code. If request code is provided method
     * {@link android.app.Activity#startActivityForResult(android.content.Intent, int)} will be used
     * to start next activity
     */
    public NavHelper forResult(int requestCode) {
        mRequestCode = requestCode;
        return this;
    }

    /**
     * Sets animation to be played when activity is entered / finished
     */
    public NavHelper animate(NavTransition transition) {
        mTransition = transition;
        return this;
    }

    /**
     * Actually starts activity by activity class
     */
    public void start(Class<? extends Activity> activityClass) {
        start(new Intent(getActivity(), activityClass));
    }

    /**
     * Actually starts activity by intent
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void start(Intent intent) {
        check();
        if (mActivity != null) {
            if (mRequestCode == NO_RESULT_CODE) {
                mActivity.startActivity(intent);
            } else {
                mActivity.startActivityForResult(intent, mRequestCode);
            }
        } else if (mFragment != null) {
            if (mRequestCode == NO_RESULT_CODE) {
                mFragment.startActivity(intent);
            } else {
                mFragment.startActivityForResult(intent, mRequestCode);
            }
        } else if (mFragmentSupport != null) {
            if (mRequestCode == NO_RESULT_CODE) {
                mFragmentSupport.startActivity(intent);
            } else {
                mFragmentSupport.startActivityForResult(intent, mRequestCode);
            }
        }
        setTransition(false);
    }

    /**
     * Finishes current activity
     */
    public void finish() {
        check();
        getActivity().finish();
        setTransition(true);
    }

    /**
     * Finishes current activity with provided result code
     */
    public void finish(int resultCode) {
        finish(resultCode, null);
    }

    /**
     * Finishes current activity with provided result code and data
     */
    public void finish(int resultCode, Intent data) {
        check();
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
    public void navigateUp(Intent upIntent) {
        upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        start(upIntent);
        finish();
    }


    /* Helper methods */

    private void check() {
        if (getActivity() == null) throw new NullPointerException("No activity or fragment instance");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private Activity getActivity() {
        if (mActivity != null) {
            return mActivity;
        } else if (mFragment != null) {
            return mFragment.getActivity();
        } else if (mFragmentSupport != null) {
            return mFragmentSupport.getActivity();
        }
        return null;
    }

    private void setTransition(boolean isFinishing) {
        if (mTransition != null) {
            if (isFinishing) {
                getActivity().overridePendingTransition(mTransition.finishEnterAnim, mTransition.finishExitAnim);
            } else {
                getActivity().overridePendingTransition(mTransition.startEnterAnim, mTransition.startExitAnim);
            }
        }
    }

    /**
     * Animations resources holder
     */
    public static class NavTransition {

        private final int startEnterAnim, startExitAnim;
        private final int finishEnterAnim, finishExitAnim;

        public NavTransition(int startEnterAnim, int startExitAnim, int finishEnterAnim, int finishExitAnim) {
            this.startEnterAnim = startEnterAnim;
            this.startExitAnim = startExitAnim;
            this.finishEnterAnim = finishEnterAnim;
            this.finishExitAnim = finishExitAnim;
        }

    }

}
