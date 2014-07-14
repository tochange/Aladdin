package com.tochange.yang.aladdinanimation;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.tochange.yang.lib.BadgeView;

public class MainActivity extends Activity
{
    protected static boolean DEBUG_MODE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // DisplayMetrics dm = new DisplayMetrics();
        // dm = getResources().getDisplayMetrics();
        // log.e(dm.widthPixels + "...." + dm.heightPixels);

        final MainView mainView = (MainView) findViewById(R.id.view);
        mainView.setIsDebug(DEBUG_MODE);

        setCloseBadgeView(mainView);
        setDebugBadgeView(mainView);
    }

    private void setCloseBadgeView(final MainView mainView)
    {
        final BadgeView badge = new BadgeView(this, mainView);
        badge.setBadgePosition(267 - 17, -3, 0, 0);
        badge.setBadgeBackgroundShape(BadgeView.BACKGROUND_SHAPE_OVAL);
        badge.setText(" × ");// delete button
        badge.setTextSize(25);
        badge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if (mainView.startAnimation(false))
                    badge.hide();
            }
        });
        badge.show();
        mainView.setDeleteButton(badge);
    }

    private void setDebugBadgeView(final MainView mainView)
    {
        final BadgeView badge1 = new BadgeView(this, mainView);
        badge1.setBadgePosition(BadgeView.POSITION_BOTTOM_LEFT);
        badge1.setBadgeBackgroundShape(BadgeView.BACKGROUND_SHAPE_ROUNDRECTANGLL);
        badge1.setBadgeBackgroundColor(Color.GREEN);
        badge1.setText("debug");// delete button
        badge1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                DEBUG_MODE = !DEBUG_MODE;
                mainView.setIsDebug(DEBUG_MODE);
            }
        });
        badge1.show();
    }
}
