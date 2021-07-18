package com.lukh.zoomabeview.Listeners;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class OnCircuitComponentTouchedListener implements View.OnTouchListener {

    private final String circuitDiagram = "circuit_diagram";
    private boolean isSourceCircuitDiagram;
    private final  String circuitComponentIds [] =  {"resistance","voltagesource","currentsource"};
    private boolean componentTouched;


    @Override
    public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                componentTouched = true;
                ViewGroup parentView = (ViewGroup) v.getParent();
                String id = parentView.getResources().getResourceEntryName(parentView.getId());
                isSourceCircuitDiagram = id.equals(circuitDiagram);
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        v);
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                return true;
            } else {
                return false;
            }
        }


    public boolean isSourceCircuitDiagram() {
        return isSourceCircuitDiagram;
    }

    public boolean isComponentTouched() {
        return componentTouched;
    }

    public void setComponentTouched(boolean componentTouched) {
        this.componentTouched = componentTouched;
    }


}
