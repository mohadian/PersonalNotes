package com.zagros.personalnotes.ui;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.zagros.personalnotes.BuildConfig;
import com.zagros.personalnotes.R;
import com.zagros.personalnotes.ui.activity.NoteDetailActivity;
import com.zagros.personalnotes.ui.widget.NavDrawerItemView;
import com.zagros.personalnotes.utils.AppConstant;
import com.zagros.personalnotes.utils.ImageLoader;
import com.zagros.personalnotes.widgets.NavigationDrawerFragment;

import java.util.ArrayList;

public abstract class BaseActivity extends AppCompatActivity {

    // Operation type (what is being executed)
    public static final int NAVDRAWER_ITEM_NOTES = 1;
    public static final int NAVDRAWER_ITEM_REMINDERS = 2;
    public static final int NAVDRAWER_ITEM_ARCHIVES = 3;
    public static final int NAVDRAWER_ITEM_TRASH = 4;
    public static final int NAVDRAWER_ITEM_SETTINGS = 5;
    public static final int NAVDRAWER_ITEM_ABOUT = 6;
    public static final int NAVDRAWER_ITEM_DEBUG = 7;
    public static final int NAVDRAWER_ITEM_INVALID = -1;
    private static final String TAG = BaseActivity.class.getSimpleName();

    // Default toolbar title (can change)
    public static int mTitle = R.string.notes;

    // Default type of operation
    public static int mType = NAVDRAWER_ITEM_NOTES;

    // Navigation drawer:
    private DrawerLayout mDrawerLayout;

    private ObjectAnimator mStatusBarColorAnimator;

    private ViewGroup mDrawerItemsListContainer;

    private Handler mHandler;

    // When set, these components will be shown/hidden in sync with the action bar
    // to implement the "quick recall" effect (the Action Bar and the header views disappear
    // when you scroll down a list, and reappear quickly when you scroll up).
    private ArrayList<View> mHideableHeaderViews = new ArrayList<View>();

    // Durations for certain animations we use:
    private static final int HEADER_HIDE_ANIM_DURATION = 300;

    // titles for navdrawer items (indices must correspond to the above)
    private static final int[] NAVDRAWER_TITLE_RES_ID = new int[]{
            R.string.drawer_notes,
            R.string.drawer_reminders,
            R.string.drawer_archives,
            R.string.drawer_trash,
            R.string.drawer_trash,
            R.string.drawer_help_and_feedback,
            R.string.about
    };

    // icons for navdrawer items (indices must correspond to above array)
    private static final int[] NAVDRAWER_ICON_RES_ID = new int[]{
            R.drawable.ic_playlist_check_white_36dp,
            R.drawable.ic_alarm_multiple_white_36dp,
            R.drawable.ic_archive_white_36dp,
            R.drawable.ic_delete_forever_white_36dp,
            R.drawable.ic_settings_white_36dp,
            R.drawable.ic_help_circle_outline_white_36dp
    };


    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    // fade in and fade out durations for the main content when switching between
    // different Activities of the app through the Nav Drawer
    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;

    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;

    // list of navdrawer items that were actually added to the navdrawer, in order
    private ArrayList<Integer> mNavDrawerItems = new ArrayList<Integer>();

    // views that correspond to each navdrawer item, null if not yet created
    private View[] mNavDrawerItemViews = null;

    // SwipeRefreshLayout allows the user to swipe the screen down to trigger a manual refresh
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // Primary toolbar and drawer toggle
    private Toolbar mActionBarToolbar;

    // variables that control the Action Bar auto hide behavior (aka "quick recall")
    private boolean mActionBarAutoHideEnabled = false;

    private int mActionBarAutoHideSensivity = 0;

    private int mActionBarAutoHideMinY = 0;

    private int mActionBarAutoHideSignal = 0;

    private boolean mActionBarShown = true;

    private boolean mManualSyncRequest;

    private int mThemedStatusBarColor;

    private int mNormalStatusBarColor;

    private int mProgressBarTopWhenActionBarShown;

    private static final TypeEvaluator ARGB_EVALUATOR = new ArgbEvaluator();

    private ImageLoader mImageLoader;

    private NavigationDrawerFragment mDrawerFragment;

    // These will be the 3 icons on the screen to add a new note, list notes, or photo note
    public static ImageView makeNote, makeListNote, makePhotoNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = new ImageLoader(this);
        mHandler = new Handler();

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mThemedStatusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        mNormalStatusBarColor = mThemedStatusBarColor;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
        updateSwipeRefreshProgressBarTop();

        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        } else {
            Log.w(TAG, "No view with ID main_content to fade in.");
        }
    }

    protected void setProgressBarTopWhenActionBarShown(int progressBarTopWhenActionBarShown) {
        mProgressBarTopWhenActionBarShown = progressBarTopWhenActionBarShown;
        updateSwipeRefreshProgressBarTop();
    }

    private void updateSwipeRefreshProgressBarTop() {
        if (mSwipeRefreshLayout == null) {
            return;
        }

        int progressBarStartMargin = getResources().getDimensionPixelSize(
                R.dimen.swipe_refresh_progress_bar_start_margin);
        int progressBarEndMargin = getResources().getDimensionPixelSize(
                R.dimen.swipe_refresh_progress_bar_end_margin);
        int top = mActionBarShown ? mProgressBarTopWhenActionBarShown : 0;
        mSwipeRefreshLayout.setProgressViewOffset(false,
                top + progressBarStartMargin, top + progressBarEndMargin);
    }

    /**
     * Returns the navigation drawer item that corresponds to this Activity. Subclasses
     * of BaseActivity override this to indicate what nav drawer item corresponds to them
     * Return NAVDRAWER_ITEM_INVALID to mean that this Activity should not have a Nav Drawer.
     */
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }

    /**
     * Sets up the navigation drawer as appropriate. Note that the nav drawer will be
     * different depending on whether the attendee indicated that they are attending the
     * event on-site vs. attending remotely.
     */
    private void setupNavDrawer() {
        // What nav drawer item should be selected?
        int selfItem = getSelfNavDrawerItem();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }
        mDrawerLayout.setStatusBarBackgroundColor(
                getResources().getColor(R.color.colorPrimaryDark));

        if (mActionBarToolbar != null) {
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                // run deferred action, if we have one
                onNavDrawerStateChanged(false, false);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                onNavDrawerStateChanged(true, false);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                onNavDrawerStateChanged(isNavDrawerOpen(), newState != DrawerLayout.STATE_IDLE);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                onNavDrawerSlide(slideOffset);
            }
        });

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // populate the nav drawer with the correct items
        populateNavDrawer();

        // When the user runs the app for the first time, we want to land them with the
        // navigation drawer open. But just the first time.

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }

    // Subclasses can override this for custom behavior
    protected void onNavDrawerStateChanged(boolean isOpen, boolean isAnimating) {
        if (mActionBarAutoHideEnabled && isOpen) {
            autoShowOrHideActionBar(true);
        }
    }

    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (mActionBarToolbar != null) {
                // Depending on which version of Android you are on the Toolbar or the ActionBar may be
                // active so the a11y description is set here.
                mActionBarToolbar.setNavigationContentDescription(getResources().getString(R.string
                        .navdrawer_description_a11y));
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }


    protected void autoShowOrHideActionBar(boolean show) {
        if (show == mActionBarShown) {
            return;
        }

        mActionBarShown = show;
        onActionBarAutoShowOrHide(show);
    }


    protected void onActionBarAutoShowOrHide(boolean shown) {
        if (mStatusBarColorAnimator != null) {
            mStatusBarColorAnimator.cancel();
        }
        mStatusBarColorAnimator = ObjectAnimator.ofInt(
                mDrawerLayout,
                (mDrawerLayout != null) ? "statusBarBackgroundColor" : "statusBarColor",
                shown ? Color.BLACK : mNormalStatusBarColor,
                shown ? mNormalStatusBarColor : Color.BLACK)
                .setDuration(250);
        if (mDrawerLayout != null) {
            mStatusBarColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ViewCompat.postInvalidateOnAnimation(mDrawerLayout);
                }
            });
        }
        mStatusBarColorAnimator.setEvaluator(ARGB_EVALUATOR);
        mStatusBarColorAnimator.start();

        updateSwipeRefreshProgressBarTop();

        for (final View view : mHideableHeaderViews) {
            if (shown) {
                ViewCompat.animate(view)
                        .translationY(0)
                        .alpha(1)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator())
                        // Setting Alpha animations should be done using the
                        // layer_type set to layer_type_hardware for the duration of the animation.
                        .withLayer();
            } else {
                ViewCompat.animate(view)
                        .translationY(-view.getBottom())
                        .alpha(0)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator())
                        // Setting Alpha animations should be done using the
                        // layer_type set to layer_type_hardware for the duration of the animation.
                        .withLayer();
            }
        }
    }


    protected void onNavDrawerSlide(float offset) {
    }

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * Defines the Navigation Drawer items to display by updating {@code mNavDrawerItems} then
     * forces the Navigation Drawer to redraw itself.
     */
    private void populateNavDrawer() {
        mNavDrawerItems.clear();

        mNavDrawerItems.add(NAVDRAWER_ITEM_NOTES);
        mNavDrawerItems.add(NAVDRAWER_ITEM_REMINDERS);
        mNavDrawerItems.add(NAVDRAWER_ITEM_ARCHIVES);
        mNavDrawerItems.add(NAVDRAWER_ITEM_TRASH);
        mNavDrawerItems.add(NAVDRAWER_ITEM_SETTINGS);
        mNavDrawerItems.add(NAVDRAWER_ITEM_ABOUT);
        // Debug menu only on debug builds.
        if (BuildConfig.DEBUG) {
            mNavDrawerItems.add(NAVDRAWER_ITEM_DEBUG);
        }

        createNavDrawerItems();
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private void createNavDrawerItems() {
        mDrawerItemsListContainer = (ViewGroup) findViewById(R.id.navdrawer_items_list);
        if (mDrawerItemsListContainer == null) {
            return;
        }

        mNavDrawerItemViews = new View[mNavDrawerItems.size()];
        mDrawerItemsListContainer.removeAllViews();
        int i = 0;
        for (int itemId : mNavDrawerItems) {
            mNavDrawerItemViews[i] = makeNavDrawerItem(itemId, mDrawerItemsListContainer);
            mDrawerItemsListContainer.addView(mNavDrawerItemViews[i]);
            ++i;
        }
    }

    private View makeNavDrawerItem(final int itemId, ViewGroup container) {
        NavDrawerItemView item = (NavDrawerItemView) getLayoutInflater().inflate(
                R.layout.navdrawer_item, container, false);
        item.setContent(NAVDRAWER_ICON_RES_ID[itemId], NAVDRAWER_TITLE_RES_ID[itemId]);
        item.setActivated(getSelfNavDrawerItem() == itemId);
        if (item.isActivated()) {
            item.setContentDescription(getString(R.string.navdrawer_selected_menu_item_a11y_wrapper,
                    getString(NAVDRAWER_TITLE_RES_ID[itemId])));
        } else {
            item.setContentDescription(getString(R.string.navdrawer_menu_item_a11y_wrapper,
                    getString(NAVDRAWER_TITLE_RES_ID[itemId])));
        }

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavDrawerItemClicked(itemId);
            }
        });
        return item;
    }

    private void onNavDrawerItemClicked(final int itemId) {
        if (itemId == getSelfNavDrawerItem()) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if (isSpecialItem(itemId)) {
            goToNavDrawerItem(itemId);
        } else {
            // launch the target Activity after a short delay, to allow the close animation to play
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToNavDrawerItem(itemId);
                }
            }, NAVDRAWER_LAUNCH_DELAY);

            // change the active item on the list so the user can see the item changed
            setSelectedNavDrawerItem(itemId);
            // fade out the main content
            View mainContent = findViewById(R.id.main_content);
            if (mainContent != null) {
                mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
            }
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void goToNavDrawerItem(int itemId) {

    }


    private boolean isSpecialItem(int itemId) {
        return itemId == NAVDRAWER_ITEM_SETTINGS;
    }



    public void actAsReminder() {
        mType = NAVDRAWER_ITEM_REMINDERS;
        mTitle = R.string.reminders;
    }

    public static void actAsNote() {
        mType = NAVDRAWER_ITEM_NOTES;
        mTitle = R.string.notes;
    }

    protected void setUpActions() {
        makeNote = (ImageView) findViewById(R.id.new_note);
        makeListNote = (ImageView) findViewById(R.id.new_list);
        makePhotoNote = (ImageView) findViewById(R.id.new_image);

        makeNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NoteDetailActivity.class);
                intent.putExtra(AppConstant.NOTE_OR_REMINDER, mTitle);
                startActivity(intent);
            }
        });

        makeListNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NoteDetailActivity.class);
                intent.putExtra(AppConstant.LIST_NOTES, AppConstant.TRUE);
                intent.putExtra(AppConstant.NOTE_OR_REMINDER, mTitle);
                startActivity(intent);
            }
        });
        makePhotoNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NoteDetailActivity.class);
                intent.putExtra(AppConstant.GO_TO_CAMERA, AppConstant.TRUE);
                intent.putExtra(AppConstant.NOTE_OR_REMINDER, mTitle);
                startActivity(intent);
            }
        });
    }

    protected void showToast(int message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    protected void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * Sets up the given navdrawer item's appearance to the selected state. Note: this could
     * also be accomplished (perhaps more cleanly) with state-based layouts.
     */
    private void setSelectedNavDrawerItem(int itemId) {
        if (mNavDrawerItemViews != null) {
            for (int i = 0; i < mNavDrawerItemViews.length; i++) {
                if (i < mNavDrawerItems.size()) {
                    int thisItemId = mNavDrawerItems.get(i);
                    mNavDrawerItemViews[i].setActivated(itemId == thisItemId);
                }
            }
        }
    }
}