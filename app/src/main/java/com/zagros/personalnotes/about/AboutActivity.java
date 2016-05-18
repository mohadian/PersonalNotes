package com.zagros.personalnotes.about;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.zagros.personalnotes.R;
import com.zagros.personalnotes.ui.BaseActivity;
import com.zagros.personalnotes.ui.widget.DrawShadowFrameLayout;
import com.zagros.personalnotes.utils.UIUtils;

public class AboutActivity extends BaseActivity {

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        rootView = findViewById(R.id.about_container);

        overridePendingTransition(0, 0);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_ABOUT;
    }

    private void setContentTopClearance(int clearance) {
        if (rootView != null) {
            rootView.setPadding(rootView.getPaddingLeft(), clearance,
                    rootView.getPaddingRight(), rootView.getPaddingBottom());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int actionBarSize = UIUtils.calculateActionBarSize(this);
        DrawShadowFrameLayout drawShadowFrameLayout =
                (DrawShadowFrameLayout) findViewById(R.id.main_content);
        if (drawShadowFrameLayout != null) {
            drawShadowFrameLayout.setShadowTopOffset(actionBarSize);
        }
        setContentTopClearance(actionBarSize);
    }
}
