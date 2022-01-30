package com.lukh.zoomabeview.Listeners;

import android.view.View;

public class OnDirectionImageClickedListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        if(v.getRotation() == 180) {
            v.setRotation(0);
        }else {
            v.setRotation(180);
        }
    }
}
