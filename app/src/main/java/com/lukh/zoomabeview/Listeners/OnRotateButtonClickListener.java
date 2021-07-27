package com.lukh.zoomabeview.Listeners;

import android.view.View;
import android.widget.RelativeLayout;

public class OnRotateButtonClickListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        RelativeLayout relativeLayout = (RelativeLayout) v.getParent();
        relativeLayout.setRotation(90.0f);
    }
}
