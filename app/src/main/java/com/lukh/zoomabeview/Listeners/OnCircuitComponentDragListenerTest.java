package com.lukh.zoomabeview.Listeners;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lukh.zoomabeview.view.CircuitComponent;
import com.lukh.zoomabeview.view.ConnectionPoint;

public class OnCircuitComponentDragListenerTest implements View.OnDragListener {

    private final String componentStackContainer = "componentstackContainer";
    private final String circuitdiagram = "circuit_diagram";
    private final String circuitComponentTag = "CircuitComponent";
    private final String connectionPointTag = "ConnectionPoint";
    private Context context;
    private boolean isSourceCircuitDiagram;
    private OnCircuitComponentTouchedListener onCircuitComponentTouchedListener;
    private Matrix mTranslateMatrix;
    private Matrix mTranslateMatrixInverse;
    private Matrix mScaleMatrix = new Matrix();
    private Matrix mScaleMatrixInverse = new Matrix();
    private float[] coords = new float[2];
    private final String currentDirectionTag = "current_direction";
    private final String voltageDirectionTag = "voltage_direction";
    private Integer idCounter = 0;



    private float[] screenPointsToScaledPoints(float[] a) {
        mTranslateMatrixInverse.mapPoints(a);
        mScaleMatrixInverse.mapPoints(a);
        return a;
    }

    public OnCircuitComponentDragListenerTest(Context context, OnCircuitComponentTouchedListener onCircuitComponentTouchedListener) {
        this.context = context;
        this.onCircuitComponentTouchedListener = onCircuitComponentTouchedListener;
    }


    private CircuitComponent prepareDraggedComponent(View view, CircuitComponent draggedComponent) {
        if (!isSourceCircuitDiagram) {
            CircuitComponent copiedDraggedComponent = new CircuitComponent(context, draggedComponent.getId());
            copiedDraggedComponent.setUniqueComponentId(this.idCounter++);
            ImageView componentSymbol = copyComponentSymbol(draggedComponent.getComponentSymbol());
            Button rotateButton = copyRotateButton(draggedComponent.getRotateButton());
            RelativeLayout relativeLayout = copyRelLayout();
            ImageView currentDirection = copyDirectionArrow(draggedComponent.getCurrentDirectionImage());
            ImageView voltageDirection = copyDirectionArrow(draggedComponent.getVoltageDirectionImage());
            relativeLayout.addView(componentSymbol);
            relativeLayout.addView(rotateButton);
            relativeLayout.addView(currentDirection);
            relativeLayout.addView(voltageDirection);
            copiedDraggedComponent.addView(relativeLayout);
            //copiedDraggedComponent.addView(currentDirection);
            copiedDraggedComponent.setRelativeLayout(relativeLayout);
            copiedDraggedComponent.setComponentSymbol(componentSymbol);
            copiedDraggedComponent.setRotateButton(rotateButton);
            copiedDraggedComponent.setCurrentDirectionImage(currentDirection);
            copiedDraggedComponent.setVoltageDirectionImage(voltageDirection);
            copiedDraggedComponent.initListeners();
            copiedDraggedComponent.setBackgroundColor(Color.TRANSPARENT);
            copiedDraggedComponent.setLayoutParams(new ViewGroup.LayoutParams(70, 100));
            copiedDraggedComponent.setTag(circuitComponentTag);
            copiedDraggedComponent.setOnCircuitComponentTouchedListener(onCircuitComponentTouchedListener);
            return copiedDraggedComponent;
        }

        ViewGroup parent = (ViewGroup) draggedComponent.getParent();
        parent.removeView(draggedComponent);

        return draggedComponent;
    }


    private ImageView copyComponentSymbol(ImageView componentSymbol) {
        ImageView newComponentSymbol = new ImageView(context);
        newComponentSymbol.setImageDrawable(componentSymbol.getDrawable());
        newComponentSymbol.setBackgroundColor(Color.TRANSPARENT);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 90);
        params.alignWithParent = true;
        params.bottomMargin = 10;
        params.leftMargin = 20;
        newComponentSymbol.setLayoutParams(params);
        return newComponentSymbol;
    }

    private Button copyRotateButton(Button rotateButton) {
        Button newRotateButton = new Button(context);
        newRotateButton.setBackground(rotateButton.getBackground());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(20, 20);
        params.setMarginStart(50);
        params.setMarginEnd(1);
        params.setMargins(0, 0, 0, 89);
        params.alignWithParent = true;
        newRotateButton.setLayoutParams(params);

        return newRotateButton;
    }

    private RelativeLayout copyRelLayout() {
        RelativeLayout newRelativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newRelativeLayout.setLayoutParams(params);
        return newRelativeLayout;
    }

    private ImageView copyDirectionArrow(ImageView directionArrow){
        ImageView newDirectionArrow = new ImageView(context);
        newDirectionArrow.setImageDrawable(directionArrow.getDrawable());
        newDirectionArrow.setOnClickListener(new OnDirectionImageClickedListener());
        if(directionArrow.getTag().equals(currentDirectionTag)){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(10,10);
            params.leftMargin = 40;
            //params.topMargin = 10;
            newDirectionArrow.setLayoutParams(params);
        }else{
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(20,60);
            params.topMargin = 15;
            newDirectionArrow.setLayoutParams(params);
        }
        return newDirectionArrow;
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
                if (v.getResources().getResourceEntryName(v.getId()).equals(circuitdiagram)) {
                    ViewGroup circuitdigramCard = (ViewGroup) v;
                    View view = (View) event.getLocalState();
                    String tag = (String) view.getTag();
                    if (tag.equals(circuitComponentTag)) {
                        CircuitComponent draggedComponent = (CircuitComponent) event.getLocalState();
                        draggedComponent = prepareDraggedComponent(v, draggedComponent);
                        dropCircuitComponent(draggedComponent, circuitdigramCard, event);
                    }else if (tag.equals(connectionPointTag)){
                        ConnectionPoint connectionPoint = (ConnectionPoint) event.getLocalState();
                        circuitdigramCard.removeView(connectionPoint);
                        dropConnectionPoint(connectionPoint,circuitdigramCard,event);
                    }
                }
                break;
            case DragEvent.ACTION_DRAG_ENDED:

            default:
                break;
        }
        return true;
    }


    private void dropCircuitComponent(CircuitComponent component, ViewGroup parent, DragEvent event) {
        if (!isSourceCircuitDiagram) {
            float coords[] = {event.getX(), event.getY()};
            coords = screenPointsToScaledPoints(coords);
            component.setTopPoint(new PointF(coords[0] + component.getOffSetTopPoint().x, coords[1] + component.getOffSetTopPoint().y));
            component.setBottomPoint(new PointF(coords[0] + component.getOffSetBottomPoint().x, coords[1] + component.getOffSetBottomPoint().y));

        }
        parent.addView(component);
        if (mTranslateMatrixInverse != null && mScaleMatrixInverse != null) {
            coords[0] = event.getX();
            coords[1] = event.getY();
            coords = screenPointsToScaledPoints(coords);
            float[] drawPointCoordsTop = {event.getX(), event.getY()};
            drawPointCoordsTop = screenPointsToScaledPoints(drawPointCoordsTop);
            drawPointCoordsTop[0] += component.getOffSetTopPoint().x;
            drawPointCoordsTop[1] += component.getOffSetTopPoint().y;
            component.getTopPoint().x = drawPointCoordsTop[0];
            component.getTopPoint().y = drawPointCoordsTop[1];
            float[] drawPointCoordsBottom = {event.getX(), event.getY()};
            drawPointCoordsBottom = screenPointsToScaledPoints(drawPointCoordsBottom);
            drawPointCoordsBottom[0] += component.getOffSetBottomPoint().x;
            drawPointCoordsBottom[1] += component.getOffSetBottomPoint().y;
            component.getBottomPoint().x = drawPointCoordsBottom[0];
            component.getBottomPoint().y = drawPointCoordsBottom[1];
            component.setX(coords[0]);
            component.setY(coords[1]);
        } else {
            component.setX(event.getX());
            component.setY(event.getY());
            float[] drawPointCoordsTop = {event.getX(), event.getY()};
            drawPointCoordsTop[0] += component.getOffSetTopPoint().x;
            drawPointCoordsTop[1] += component.getOffSetTopPoint().y;
            component.getTopPoint().x = drawPointCoordsTop[0];
            component.getTopPoint().y = drawPointCoordsTop[1];
            float[] drawPointCoordsBottom = {event.getX(), event.getY()};
            drawPointCoordsBottom[0] += component.getOffSetBottomPoint().x;
            drawPointCoordsBottom[1] += component.getOffSetBottomPoint().y;
            component.getBottomPoint().x = drawPointCoordsBottom[0];
            component.getBottomPoint().y = drawPointCoordsBottom[1];
        }
        component.setVisibility(View.VISIBLE);


    }

    private void dropConnectionPoint(ConnectionPoint point, ViewGroup parent, DragEvent event) {
        if (mTranslateMatrixInverse != null && mScaleMatrixInverse != null) {
            coords[0] = event.getX();
            coords[1] = event.getY();
            coords = screenPointsToScaledPoints(coords);
            point.setX(coords[0]);
            point.setY(coords[1]);
            point.refreshPointLocation();

        }else {
            point.setX(event.getX());
            point.setY(event.getY());
            point.refreshPointLocation();
        }
        parent.addView(point);
        point.setVisibility(View.VISIBLE);
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
