package com.lukh.zoomabeview.Listeners;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lukh.zoomabeview.view.CircuitComponent;

public class OnCircuitComponentDragListenerTest implements View.OnDragListener {

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

    public OnCircuitComponentDragListenerTest(Context context, OnCircuitComponentTouchedListener onCircuitComponentTouchedListener){
        this.context = context;
        this.onCircuitComponentTouchedListener = onCircuitComponentTouchedListener;
        idcount = 1;
    }


    private CircuitComponent prepareDraggedComponent(View view, CircuitComponent draggedComponent){
        if (!isSourceCircuitDiagram){
            CircuitComponent copiedDraggedComponent = new CircuitComponent(context);
            Button rotateBtn = draggedComponent.getRotateButton();
            ImageView componentSymbol = copyComponent(draggedComponent.getComponentSymbol());
            RelativeLayout relativeLayout = copyRelLayout(draggedComponent.getRelativeLayout());
            copiedDraggedComponent.setBackgroundColor(Color.TRANSPARENT);
            copiedDraggedComponent.setLayoutParams(new ViewGroup.LayoutParams(50, 100));
            String tag = (String) copiedDraggedComponent.getTag();
            String newTag = tag + idcount;
            relativeLayout.addView(rotateBtn);
            relativeLayout.addView(componentSymbol);
            copiedDraggedComponent.addView(relativeLayout);
            copiedDraggedComponent.setTag(newTag);
            copiedDraggedComponent.setOnTouchListener(onCircuitComponentTouchedListener);
            copiedDraggedComponent.setComponentSymbol(componentSymbol);
            copiedDraggedComponent.setRotateButton(rotateBtn);
            copiedDraggedComponent.setRelativeLayout(relativeLayout);

            return copiedDraggedComponent;
        }

        ViewGroup parent = (ViewGroup) draggedComponent.getParent();
        parent.removeView(draggedComponent);

        return  draggedComponent;
    }


    private ImageView copyComponent(ImageView componentSymbol){
        ImageView newComponentSymbol = new ImageView(context);
        newComponentSymbol.setImageDrawable(componentSymbol.getDrawable());
        newComponentSymbol.setBackgroundColor(Color.TRANSPARENT);
        newComponentSymbol.setLayoutParams(componentSymbol.getLayoutParams());
        return newComponentSymbol;
    }

    private Button copyRotateButton (Button rotateButton){
        Button newRotateButton = new Button(context);
        newRotateButton.setBackground(rotateButton.getBackground());
        newRotateButton.setBackgroundColor(Color.BLACK);
        newRotateButton.setLayoutParams(rotateButton.getLayoutParams());
        newRotateButton.setEnabled(false);
        newRotateButton.setVisibility(View.INVISIBLE);
        return newRotateButton;
    }

    private RelativeLayout copyRelLayout(RelativeLayout relativeLayout){
        RelativeLayout newRelativeLayout = new RelativeLayout(context);
        newRelativeLayout.setLayoutParams(relativeLayout.getLayoutParams());
        return newRelativeLayout;
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
                CircuitComponent draggedComponent  = (CircuitComponent) event.getLocalState();
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
