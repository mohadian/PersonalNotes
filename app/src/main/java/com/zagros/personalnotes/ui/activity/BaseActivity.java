package com.zagros.personalnotes.ui.activity;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.zagros.personalnotes.R;
import com.zagros.personalnotes.ui.adapter.NavigationDrawerAdapter;
import com.zagros.personalnotes.data.model.NavigationDrawerItem;
import com.zagros.personalnotes.utils.AppConstant;
import com.zagros.personalnotes.utils.AppSharedPreferences;
import com.zagros.personalnotes.widgets.NavigationDrawerFragment;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {

    // Operation type (what is being executed)
    public static final int NOTES = 1;
    public static final int REMINDERS = 2;
    public static final int ARCHIVES = 3;
    public static final int TRASH = 4;
    public static final int SETTINGS = 5;

    // Default toolbar title (can change)
    public static int mTitle = R.string.notes;

    // Default type of operation
    public static int mType = NOTES;

    // Misc
    protected Toolbar mToolBar;

    private NavigationDrawerFragment mDrawerFragment;

    // These will be the 3 icons on the screen to add a new note, list notes, or photo note
    public static ImageView makeNote, makeListNote, makePhotoNote;

    public void actAsReminder() {
        mType = REMINDERS;
        mTitle = R.string.reminders;
    }

    public static void actAsNote() {
        mType = NOTES;
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

    protected Toolbar activateToolbar() {
        if (mToolBar == null) {
            mToolBar = (Toolbar) findViewById(R.id.app_bar);
            if (mToolBar != null) {
                setSupportActionBar(mToolBar);
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    switch (mType) {
                        case REMINDERS:
                            actionBar.setTitle(R.string.reminders);
                            break;
                        case NOTES:
                            actionBar.setTitle(R.string.notes);
                            break;
                        case ARCHIVES:
                            actionBar.setTitle(R.string.archives);
                            break;
                        case TRASH:
                            actionBar.setTitle(R.string.trash);
                            break;
                    }
                }
            }
        }
        return mToolBar;
    }

    protected Toolbar activateToolbarWithHomeEnabled() {
        activateToolbar();
        if (mToolBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (mType == REMINDERS)
                getSupportActionBar().setTitle(R.string.activity_make_reminder);
            else if (mType == NOTES)
                getSupportActionBar().setTitle(R.string.activity_make_notes);
            else if (mType == SETTINGS)
                getSupportActionBar().setTitle(R.string.settings);
        }
        return mToolBar;
    }

    protected void removeActions() {
        CardView cardView;
        cardView = (CardView) findViewById(R.id.card_view);
        cardView.setVisibility(View.GONE);
    }

    protected void setUpNavigationDrawer() {

        ListView navigationListView;
        //Few items, so added manually

        List<NavigationDrawerItem> items = new ArrayList<>();
        items.add(new NavigationDrawerItem(R.drawable.ic_playlist_check_white_36dp, R.string.drawer_notes));
        items.add(new NavigationDrawerItem(R.drawable.ic_alarm_multiple_white_36dp, R.string.drawer_reminders));
        items.add(new NavigationDrawerItem(R.drawable.ic_archive_white_36dp, R.string.drawer_archives));
        items.add(new NavigationDrawerItem(R.drawable.ic_delete_forever_white_36dp, R.string.drawer_trash));
        items.add(new NavigationDrawerItem(R.drawable.ic_settings_white_36dp, R.string.drawer_trash));
        items.add(new NavigationDrawerItem(R.drawable.ic_help_circle_outline_white_36dp, R.string.drawer_help_and_feedback));

        //initialize the drawer fragment
        mDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        mDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolBar);

        NavigationDrawerAdapter navigationDrawerAdapter = new NavigationDrawerAdapter(getApplicationContext(), items);
        //initialize the list view for navigation drawer
        navigationListView = (ListView) findViewById(R.id.navigation_list);
        navigationListView.setAdapter(navigationDrawerAdapter);

        navigationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Class nextActivity;

                switch (position) {
                    case 0:
                        nextActivity = NotesActivity.class;
                        actAsNote();
                        break;
                    case 1:
                        nextActivity = NotesActivity.class;
                        actAsReminder();
                        break;
                    case 2:
                        nextActivity = ArchivesActivity.class;
                        mType = ARCHIVES;
                        break;
                    case 3:
                        nextActivity = TrashActivity.class;
                        mType = TRASH;
                        break;
                    case 4:
                        nextActivity = AppAuthenticationActivity.class;
                        mType = SETTINGS;
                        break;
                    case 5:
                        nextActivity = HelpFeedActivity.class;
                        break;
                    default:
                        nextActivity = HelpFeedActivity.class;
                }

                AppSharedPreferences.setUserLearned(getApplicationContext(), AppConstant.KEY_USER_LEARNED_DRAWER, AppConstant.TRUE);
                Intent intent = new Intent(BaseActivity.this, nextActivity);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent, 0);
                overridePendingTransition(0, 0);
                mDrawerFragment.closeDrawer();
            }
        });
    }

    protected void showToast(int message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    protected void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

}
