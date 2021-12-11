package com.lukh.zoomabeview.Listeners;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.View;
import android.widget.RelativeLayout;

import com.lukh.zoomabeview.view.CircuitComponent;
import com.lukh.zoomabeview.view.ZoomableViewGroup;

public class OnRotateButtonClickListener implements View.OnClickListener {

    private int previousRotation = 0;

    private Matrix matrix;
    private ZoomableViewGroup circuitDiagram;
    private PointF rotationCenter;

    public OnRotateButtonClickListener() {
        matrix = new Matrix();
        matrix.setRotate(90f);
    }

    @Override
    public void onClick(View v) {
        //circuitDiagram = (ZoomableViewGroup) v.getParent().getParent();
        RelativeLayout relativeLayout = (RelativeLayout) v.getParent();
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
        PointF rotateCenter = new PointF(component.getX() + 25, component.getY() + 50);
        PointF rotatedPoint = null;
        switch (previousRotation) {
            case 0:
                rotatedPoint = new PointF(rotateCenter.x, rotateCenter.y - offsetToMid);
                break;
            case 90:
                rotatedPoint = new PointF(rotateCenter.x + offsetToMid, rotateCenter.y);
                break;
            case 180:
                rotatedPoint = new PointF(rotateCenter.x, rotateCenter.y + offsetToMid);
                break;
            case 270:
                rotatedPoint = new PointF(rotateCenter.x - offsetToMid, rotateCenter.y);
                break;
            default:
                break;
        }
        return rotatedPoint;
    }
}
