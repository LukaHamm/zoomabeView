package com.lukh.zoomabeview.Listeners;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.dynamicanimation.animation.SpringAnimation;

import com.google.android.material.card.MaterialCardView;

import java.util.Arrays;

public class OnCircuitComponentDragListener implements View.OnDragListener {

    private final String componentStackContainer = "componentstackContainer";
    private final String circuitdiagram = "circuit_diagram";
    private Context context;
    private boolean isSourceCircuitDiagram;
    private OnCircuitComponentTouchedListener onCircuitComponentTouchedListener;
    private Matrix mTranslateMatrix;
    private Matrix mTranslateMatrixInverse;
    private Matrix mScaleMatrix = new Matrix();
    private Matrix mScaleMatrixInverse = new Matrix();
    private  float [] coords = new float[2];
    private float [] oldCoords = new float[2];
    private float [] translation = new float[2];
    private float scaleFactor;
    private int idcount;


    private float[] screenPointsToScaledPoints(float[] a){
        mTranslateMatrixInverse.mapPoints(a);
        mScaleMatrixInverse.mapPoints(a);
        return a;
    }

    public OnCircuitComponentDragListener (Context context, OnCircuitComponentTouchedListener onCircuitComponentTouchedListener){
        this.context = context;
        this.onCircuitComponentTouchedListener = onCircuitComponentTouchedListener;
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
                    if (mTranslateMatrixInverse != null && mScaleMatrixInverse != null){
                        coords[0]= event.getX();
                        coords[1] = event.getY();
                        coords = screenPointsToScaledPoints(coords);
                        draggedComponent.setX(coords[0]);
                        draggedComponent.setY(coords[1]);
                    }else{
                        draggedComponent.setX(event.getX());
                        draggedComponent.setY(event.getY());
                    }
                    draggedComponent.setVisibility(View.VISIBLE);

                }
                break;
            case DragEvent.ACTION_DRAG_ENDED:

            default:
                break;
        }
        return true;
    }



    public Matrix getmTranslateMatrixInverse() {
        return mTranslateMatrixInverse;
    }

    public void setmTranslateMatrixInverse(Matrix mTranslateMatrixInverse) {
        this.mTranslateMatrixInverse = mTranslateMatrixInverse;
    }

    public Matrix getmScaleMatrixInverse() {
        return mScaleMatrixInverse;
    }

    public void setmScaleMatrixInverse(Matrix mScaleMatrixInverse) {
        this.mScaleMatrixInverse = mScaleMatrixInverse;
    }
}
