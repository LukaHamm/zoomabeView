package com.lukh.zoomabeview.Listeners;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Arrays;

public class OnCircuitComponentLongClickListener implements View.OnLongClickListener {

   private Button rotateButton;
   private ImageView component;
   private int parentMeasurements [] = new int[2];
   private int componentMeasurements [] = new int[2];

   public OnCircuitComponentLongClickListener(Button rotateButton,ImageView component,int parentMeasurements []){
        this.component = component;
        this.rotateButton = rotateButton;
       Arrays.copyOf(parentMeasurements,2);
   }


    @Override
    public boolean onLongClick(View v) {
        if (this.rotateButton.getVisibility() == View.INVISIBLE) {
            this.rotateButton.setVisibility(View.VISIBLE);
            this.rotateButton.setEnabled(true);
            this.component.setMinimumWidth(componentMeasurements[0]);
            this.component.setMinimumHeight(componentMeasurements[1]);
        } else {
            this.rotateButton.setVisibility(View.INVISIBLE);
            this.rotateButton.setEnabled(false);
            this.component.setMinimumWidth(parentMeasurements[0]);
            this.component.setMinimumHeight(parentMeasurements[1]);
        }
        return true;
    }

    public Button getRotateButton() {
        return rotateButton;
    }

    public void setRotateButton(Button rotateButton) {
        this.rotateButton = rotateButton;
    }

    public ImageView getComponent() {
        return component;
    }

    public void setComponent(ImageView component) {
        this.component = component;
    }
}
