package com.zagros.personalnotes.ui.activity;

import android.os.Bundle;

import com.zagros.personalnotes.R;

public class HelpFeedActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_feedback_layout);
        mToolBar = activateToolbar();
        setUpNavigationDrawer();
    }
}
