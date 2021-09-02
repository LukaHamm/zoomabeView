package com.lukh.zoomabeview.Listeners;

import android.content.ClipData;
import android.view.View;
import android.view.ViewGroup;

public class OnCircuitComponentClickedListener implements View.OnClickListener {

    private final String circuitDiagram = "circuit_diagram";
    private boolean isSourceCircuitDiagram;
    private final  String circuitComponentIds [] =  {"resistance","voltagesource","currentsource"};
    private boolean componentTouched;


    @Override
    public void onClick(View view) {
        componentTouched = true;
        ViewGroup parentView = (ViewGroup) view.getParent();
        String id = parentView.getResources().getResourceEntryName(parentView.getId());
        isSourceCircuitDiagram = id.equals(circuitDiagram);
        ClipData data = ClipData.newPlainText("", "");
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                view);
        view.startDragAndDrop(data, shadowBuilder, view, 0);

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


