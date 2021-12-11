package com.lukh.zoomabeview.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import com.lukh.zoomabeview.Listeners.OnCircuitComponentTouchedListener;
import com.lukh.zoomabeview.R;

import java.util.List;

public class ConnectionPoint extends View {

    private static final float radius = 5.0f;

    public ConnectionPoint(Context context) {
        super(context);
        initConnectionPoint();
    }

    private void initConnectionPoint() {
        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.connectionpoint));
        setLayoutParams(new LinearLayout.LayoutParams(10,10));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.bottomMargin = 0;
        params.setMargins(0,0,0,0);
        setTag("ConnectionPoint");

    }

    private boolean drawMode = false;
    private boolean normalMode = true;
    private OnCircuitComponentTouchedListener onCircuitComponentTouchedListener;
    private PointF pointRight;
    private PointF pointLeft;
    private PointF pointTop;
    private PointF pointBottom;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (normalMode) {
            onCircuitComponentTouchedListener.onTouch(this, event);
        }

        return super.onTouchEvent(event);
    }

    public void setXYCoordinatesBasedOnPoint(PointF point) {
        this.setX(point.x);
        this.setY(point.y);
        point.y += radius;
        point.x += radius;
        setDrawPointLocation(point);
    }


    public void refreshPointLocation(){
        if(pointLeft != null){
            pointLeft.x = this.getX() +radius;
            pointLeft.y = this.getY() +radius;
        }
        if(pointTop != null){
            pointTop.x = this.getX() +radius;
            pointTop.y = this.getY() +radius;
        }
        if(pointBottom != null){
            pointBottom.x = this.getX() +radius;
            pointBottom.y = this.getY() +radius;
        }
        if(pointRight != null){
            pointRight.x = this.getX() +radius;
            pointRight.y = this.getY() +radius;
        }
    }

    public void setDrawPointLocation(PointF point){
        if(pointLeft == null){
            pointLeft = point;
            return;
        }else if(pointTop == null){
            pointTop = point;
            return;
        }else if(pointBottom == null){
            pointBottom = point;
            return;
        }else if(pointRight == null){
            pointRight = point;
            return;
        }
    }

    public void deleteAllPoints(List<PointF> lineCoordinates){
        //Abfragen ob Punkt gerader Index oder ungerader
        PointF points [] = {pointLeft,pointTop,pointRight,pointBottom};
        float pointIndexList[] = new float [8];
        int currentIndex = 0;
        for(int i = 0;i<points.length;i++){
            float indexPoint = lineCoordinates.indexOf(points[i]);
            float indexPointConnection = indexPoint%2== 0? indexPoint+1:indexPoint-1;
            pointIndexList[currentIndex] = indexPoint;
            ++currentIndex;
            pointIndexList[currentIndex] = indexPointConnection;
            ++currentIndex;
        }
        for(int i = 0; i<pointIndexList.length;i++){
            lineCoordinates.remove(pointIndexList[i]);
        }
        this.pointBottom= null;
        this.pointRight = null;
        this.pointLeft = null;
        this.pointTop = null;
    }



    public PointF getPointRight() {
        return pointRight;
    }

    public void setPointRight(PointF pointRight) {
        this.pointRight = pointRight;
    }

    public PointF getPointLeft() {
        return pointLeft;
    }

    public void setPointLeft(PointF pointLeft) {
        this.pointLeft = pointLeft;
    }

    public PointF getPointTop() {
        return pointTop;
    }

    public void setPointTop(PointF pointTop) {
        this.pointTop = pointTop;
    }

    public PointF getPointBottom() {
        return pointBottom;
    }

    public void setPointBottom(PointF pointBottom) {
        this.pointBottom = pointBottom;
    }

    public OnCircuitComponentTouchedListener getOnCircuitComponentTouchedListener() {
        return onCircuitComponentTouchedListener;
    }

    public void setOnCircuitComponentTouchedListener(OnCircuitComponentTouchedListener onCircuitComponentTouchedListener) {
        this.onCircuitComponentTouchedListener = onCircuitComponentTouchedListener;
    }

    public boolean isDrawMode() {
        return drawMode;
    }

    public void setDrawMode(boolean drawMode) {
        this.drawMode = drawMode;
    }

    public boolean isNormalMode() {
        return normalMode;
    }

    public void setNormalMode(boolean normalMode) {
        this.normalMode = normalMode;
    }
}
