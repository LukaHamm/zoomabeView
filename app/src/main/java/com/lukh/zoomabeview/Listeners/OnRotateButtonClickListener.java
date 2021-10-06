package com.lukh.zoomabeview.Listeners;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lukh.zoomabeview.view.CircuitComponent;

public class OnRotateButtonClickListener implements View.OnClickListener {

    private float previousRotation = 0.0f;


    @Override
    public void onClick(View v) {
        RelativeLayout relativeLayout = (RelativeLayout) v.getParent();
        CircuitComponent container = (CircuitComponent) relativeLayout.getParent();
        if(previousRotation <360){
            previousRotation += 90;
        }else{
            previousRotation = 0;
        }
        container.setRotation(previousRotation);
    }
}
