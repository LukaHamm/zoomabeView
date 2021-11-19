package com.lukh.zoomabeview.Listeners;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lukh.zoomabeview.view.CircuitComponent;
import com.lukh.zoomabeview.view.ZoomableViewGroup;

public class OnRotateButtonClickListener implements View.OnClickListener {

    private float previousRotation = 0.0f;

    private Matrix matrix;
    private ZoomableViewGroup circuitDiagram;

    public OnRotateButtonClickListener (){
        matrix = new Matrix();
        matrix.setRotate(90f);
    }

    @Override
    public void onClick(View v) {
        //circuitDiagram = (ZoomableViewGroup) v.getParent().getParent();
        RelativeLayout relativeLayout = (RelativeLayout) v.getParent();
        circuitDiagram = (ZoomableViewGroup) relativeLayout.getParent().getParent();
        CircuitComponent container = (CircuitComponent) relativeLayout.getParent();
        if((previousRotation + 90) <360){
            previousRotation += 90;
        }else{
            previousRotation = 0;
        }
        container.setRotation(previousRotation);
        if(container.getTopPoint() != null) {
            PointF rotatedPoint = rotatePoint90Degrees(container.getTopPoint(),container);
            container.getTopPoint().x = rotatedPoint.x;
            container.getTopPoint().y = rotatedPoint.y;
        }
        if(container.getBottomPoint() != null) {
            PointF rotatedPoint = rotatePoint90Degrees(container.getBottomPoint(),container);
            container.getBottomPoint().x = rotatedPoint.x;
            container.getBottomPoint().y = rotatedPoint.y;
        }
    }

    private PointF rotatePoint90Degrees(PointF pointToRotate, CircuitComponent component){
        /*float drawPointCoords [] = {pointToRotate.x, pointToRotate.y};
        float componentCorrds [] = {component.getX(), component.getY()};
        float drawPointVec [] = {drawPointCoords[0]-componentCorrds[0], drawPointCoords[1]-componentCorrds[1]};
        float vecZeroToComponent[] = {componentCorrds[0]-0f,componentCorrds[1]-0f};
        float lengthDrawPointVec = (float) Math.sqrt(Math.pow(drawPointVec[0],2.0) + Math.pow(drawPointVec[1],2.0));
        float lengthVecZeroToComponent = (float) Math.sqrt(Math.pow(vecZeroToComponent[0],2.0) + Math.pow(vecZeroToComponent[1],2.0));
        float multiplicator = lengthVecZeroToComponent/lengthDrawPointVec;
        float constructionVectorX = vecZeroToComponent[1]/multiplicator;
        float constructionVectorY = vecZeroToComponent[0]/multiplicator;
        float constructionVector[] = {constructionVectorX,constructionVectorY};
        matrix.mapPoints(constructionVector);
        float rotatedPointCoords [] = {constructionVector[0] + vecZeroToComponent[0],constructionVector[1] + vecZeroToComponent[1]};
        //rotatedPointCoords[1] +=30;
        PointF rotatedPoint = new PointF(rotatedPointCoords[0],rotatedPointCoords[1]);
        matrix.setRotate(previousRotation);
        circuitDiagram.invalidate();*/
        PointF rotationCenter = new PointF(component.getX() +25,component.getY() +50);
        float rotatedX = rotationCenter.x + (pointToRotate.x-rotationCenter.x)* (float) Math.sin(-90) - (pointToRotate.y -rotationCenter.y) * (float) Math.cos(-90);
        float rotatedy = rotationCenter.y + (pointToRotate.x-rotationCenter.x)* (float) Math.cos(-90) + (pointToRotate.y -rotationCenter.y) * (float) Math.sin(-90);
        PointF rotatedPoint = new PointF(rotatedX,rotatedy);
        circuitDiagram.invalidate();

        return rotatedPoint;
    }
}
