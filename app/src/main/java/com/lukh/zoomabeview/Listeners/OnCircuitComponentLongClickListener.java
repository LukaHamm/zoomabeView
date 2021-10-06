package com.lukh.zoomabeview.Listeners;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Arrays;

public class OnCircuitComponentLongClickListener implements View.OnLongClickListener {

   private Button rotateButton;
   private ImageView component;
   private int parentMeasurements [] = new int[2];
   private int componentMeasurements [] = new int[2];
   private final int [] margins = new int[]{0,31,0,0};
   private final int marginEnd = 56;
   private final int marginBottom = 10;
   private final int width = 90;

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
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) component.getLayoutParams();
            params.setMargins(margins[0],margins[1],margins[2],margins[3]);
            params.setMarginEnd(marginEnd);
        } else {
            this.rotateButton.setVisibility(View.INVISIBLE);
            this.rotateButton.setEnabled(false);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) component.getLayoutParams();
            params.setMargins(0,0,0,0);
            params.setMarginEnd(0);

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
