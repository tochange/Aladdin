package com.tochange.yang.floatladdin.aladdin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.tochange.yang.floatladdin.LauncherActivity;
import com.tochange.yang.floatladdin.R;

public class SettingLayoutActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aladdin_view);

        final MainView mainView = (MainView) findViewById(R.id.view);
        Intent i = getIntent();

        setLeftMargin(i.getIntExtra(LauncherActivity.KEY_X, -1));
        setTopMargin(mainView, i.getIntExtra(LauncherActivity.KEY_Y, -1));
        mainView.setBitmap((Bitmap) (i
                .getParcelableExtra(LauncherActivity.KEY_IMAGE)));
        mainView.startAnimation(false, this);
    }

    private void setTopMargin(MainView mainView, int y)
    {
        LayoutParams lp = (LayoutParams) mainView.getLayoutParams();
        // lp.leftMargin = x;
        lp.topMargin = y - LauncherActivity.MAGIC_VERTICAL;
        mainView.setLayoutParams(lp);

    }

    private void setLeftMargin(int x)
    {
        TextView tv = (TextView) findViewById(R.id.space_h);
        LayoutParams lpp = (LayoutParams) tv.getLayoutParams();
        lpp.width = x;
        lpp.height = 1;
        tv.setLayoutParams(lpp);
    }
}
