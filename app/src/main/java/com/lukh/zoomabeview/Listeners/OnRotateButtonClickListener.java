package com.lukh.zoomabeview.Listeners;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lukh.zoomabeview.view.CircuitComponent;
import com.lukh.zoomabeview.view.ZoomableViewGroup;

public class OnRotateButtonClickListener implements View.OnClickListener {

    private int previousRotation = 0;

    private Matrix matrix;
    private ZoomableViewGroup circuitDiagram;
    private PointF rotationCenter;
    private final String currentDirectionTag = "current_direction";
    private final String voltageDirectionTag = "voltage_direction";
    private ImageView currentDirectionImage;
    private ImageView voltageDirectionImage;


    public OnRotateButtonClickListener() {
        matrix = new Matrix();
        matrix.setRotate(90f);
    }

    @Override
    public void onClick(View v) {
        RelativeLayout relativeLayout = (RelativeLayout) v.getParent();
        /*
        int childCount = relativeLayout.getChildCount();
        for(int i = 0; i <childCount;i++){
            View view = relativeLayout.getChildAt(i);
            if(view.getTag().equals(currentDirectionTag)){
                currentDirectionImage = (ImageView) view;
            }else if(view.getTag().equals(voltageDirectionTag)){
                voltageDirectionImage = (ImageView) view;
            }
        }

         */
        circuitDiagram = (ZoomableViewGroup) relativeLayout.getParent().getParent();
        CircuitComponent container = (CircuitComponent) relativeLayout.getParent();
        if ((previousRotation + 90) < 360) {
            previousRotation += 90;
        } else {
            previousRotation = 0;
        }
        PointF rotatedPointTop = rotate90Degrees(container.getTopPoint(), 50, container);
        container.getTopPoint().x = rotatedPointTop.x;
        container.getTopPoint().y = rotatedPointTop.y;
        container.getOffSetTopPoint().x = rotatedPointTop.x - container.getX();
        container.getOffSetTopPoint().y = rotatedPointTop.y - container.getY();
        PointF rotatedPointBottom = rotate90Degrees(container.getBottomPoint(), -40, container);
        container.getBottomPoint().x = rotatedPointBottom.x;
        container.getBottomPoint().y = rotatedPointBottom.y;
        container.getOffSetBottomPoint().x = rotatedPointBottom.x - container.getX();
        container.getOffSetBottomPoint().y = rotatedPointBottom.y - container.getY();
        container.setRotation(previousRotation);
        circuitDiagram.invalidate();
    }


    private PointF rotate90Degrees(PointF pointToRotate, float offsetToMid, CircuitComponent component) {
        PointF rotateCenter = new PointF(component.getX() + 35, component.getY() + 50);
        PointF rotatedPoint = null;
        switch (previousRotation) {
            case 0:
                rotatedPoint = new PointF(rotateCenter.x +10, rotateCenter.y - offsetToMid);
                break;
            case 90:
                rotatedPoint = new PointF(rotateCenter.x + offsetToMid, rotateCenter.y +10 );
                break;
            case 180:
                rotatedPoint = new PointF(rotateCenter.x -10, rotateCenter.y + offsetToMid);
                break;
            case 270:
                rotatedPoint = new PointF(rotateCenter.x - offsetToMid, rotateCenter.y -10);
                break;
            default:
                break;
        }
        return rotatedPoint;
    }
}
