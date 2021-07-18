package com.lukh.zoomabeview.Listeners;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.card.MaterialCardView;

public class OnCircuitComponentDragListener implements View.OnDragListener {

    private final String componentStackContainer = "componentstackContainer";
    private final String circuitdiagram = "circuit_diagram";
    private Context context;
    private boolean isSourceCircuitDiagram;
    private OnCircuitComponentTouchedListener onCircuitComponentTouchedListener;
    private int idcount;


    private ImageView tagForDraggedComponent;


    public OnCircuitComponentDragListener (Context context, OnCircuitComponentTouchedListener onCircuitComponentTouchedListener,ImageView tagForDraggedComponent){
        this.context = context;
        this.onCircuitComponentTouchedListener = onCircuitComponentTouchedListener;
        this.tagForDraggedComponent= tagForDraggedComponent;
        idcount = 1;
    }


    private ImageView prepareDraggedComponent(View view, ImageView draggedComponent){
        if (!isSourceCircuitDiagram){
            ImageView copiedDraggedComponent = new ImageView(context);
            ColorDrawable colorCopiedComponent =  (ColorDrawable) draggedComponent.getBackground();
            copiedDraggedComponent.setBackgroundColor(Color.rgb(255,255,255));
            copiedDraggedComponent.setAdjustViewBounds(true);
            copiedDraggedComponent.setMaxWidth(50);
            copiedDraggedComponent.setMaxHeight(100);
            copiedDraggedComponent.setLayoutParams(new ViewGroup.LayoutParams(50, 100));
            Drawable bitmap =  draggedComponent.getDrawable();
            copiedDraggedComponent.setImageDrawable(bitmap);
            String id= draggedComponent.getResources().getResourceEntryName(draggedComponent.getId());
            id = id.substring(0,id.length()-1);
            copiedDraggedComponent.setTag(id+idcount);
            copiedDraggedComponent.setOnTouchListener(onCircuitComponentTouchedListener);
            return copiedDraggedComponent;
        }

        ViewGroup parent = (ViewGroup) draggedComponent.getParent();
        parent.removeView(draggedComponent);

        return  draggedComponent;
    }


    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                isSourceCircuitDiagram = onCircuitComponentTouchedListener.isSourceCircuitDiagram();
                break;
            case DragEvent.ACTION_DRAG_ENTERED:

                break;
            case DragEvent.ACTION_DRAG_EXITED:

                break;
            case DragEvent.ACTION_DROP:
                // Dropped, reassign View to ViewGroup
                ImageView draggedComponent  = (ImageView) event.getLocalState();
                if(v.getResources().getResourceEntryName(v.getId()).equals(circuitdiagram)) {
                    ViewGroup circuitdigramCard = (ViewGroup) v;
                    draggedComponent = prepareDraggedComponent(v,draggedComponent);
                    circuitdigramCard.addView(draggedComponent);
                    draggedComponent.setX(event.getX());
                    draggedComponent.setY(event.getY());
                    draggedComponent.setVisibility(View.VISIBLE);
                }
                break;
            case DragEvent.ACTION_DRAG_ENDED:

            default:
                break;
        }
        return true;
    }


    public ImageView getTagForDraggedComponent() {
        return tagForDraggedComponent;
    }

    public void setTagForDraggedComponent(ImageView tagForDraggedComponent) {
        this.tagForDraggedComponent = tagForDraggedComponent;
    }


}
