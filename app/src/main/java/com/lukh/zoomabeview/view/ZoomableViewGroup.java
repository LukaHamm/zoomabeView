package com.lukh.zoomabeview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.*;

import com.lukh.zoomabeview.Listeners.OnCircuitComponentDragListener;
import com.lukh.zoomabeview.Listeners.OnCircuitComponentDragListenerTest;
import com.lukh.zoomabeview.Listeners.OnCircuitComponentTouchedListener;

import java.util.ArrayList;
import java.util.List;

public class ZoomableViewGroup extends ViewGroup {

    private static final int INVALID_POINTER_ID = 1;
    private int mActivePointerId = INVALID_POINTER_ID;
    private final String circuitComponentTag = "CircuitComponent";
    private final String connectionPointTag = "ConnectionPoint";

    private float mScaleFactor = 1;
    private ScaleGestureDetector mScaleDetector;
    private Matrix mScaleMatrix = new Matrix();
    private Matrix mScaleMatrixInverse = new Matrix();

    private float mPosX;
    private float mPosY;
    private Matrix mTranslateMatrix = new Matrix();
    private Matrix mTranslateMatrixInverse = new Matrix();

    private float mLastTouchX;
    private float mLastTouchY;
    private float savedMposX;
    private float savedMposY;

    private float mFocusY;

    private float mFocusX;

    private float[] mInvalidateWorkingArray = new float[6];
    private float[] mDispatchTouchEventWorkingArray = new float[2];
    private float[] mOnTouchEventWorkingArray = new float[2];
    private boolean drawMode;
    private boolean deleteMode;
    private boolean normalMode = true;
    private int touchCount = 0;
    private PointF drawBegin;
    private PointF drawEnd;
    private List<PointF> lineCoordinates = new ArrayList<PointF>();
    private CircuitComponent currentDrawPointBeginComponent;
    private CircuitComponent currentDrawpointEndComponent;
    private ConnectionPoint startConnectionPoint;
    private ConnectionPoint endConnectionPoint;
    private boolean preventOnDrawBegin = false;
    private boolean preventOnDrawEnd = false;

    private OnCircuitComponentDragListener onCircuitComponentDragListener;
    private OnCircuitComponentDragListenerTest onCircuitComponentDragListenerTest;

    public ZoomableViewGroup(Context context) {
        super(context);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mTranslateMatrix.setTranslate(0, 0);
        mScaleMatrix.setScale(1, 1);
    }

    public ZoomableViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mTranslateMatrix.setTranslate(0, 0);
        mScaleMatrix.setScale(1, 1);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.layout(l, t, l + child.getMeasuredWidth(), t + child.getMeasuredHeight());
            }
        }
    }


    public void initDrawMode(boolean drawMode) {
        if(drawMode) {
            initDeleteMode(false);
            initNormalMode(false);
        }
        this.drawMode = drawMode;
        this.preventOnDrawBegin = false;
        this.preventOnDrawEnd = false;


    }

    public void initDeleteMode(boolean deleteMode){
            if(deleteMode) {
                initDrawMode(false);
                initNormalMode(false);
                this.drawBegin = null;
                this.drawEnd = null;
                this.touchCount = 0;
            }
            this.deleteMode = deleteMode;

    }

    public void  initNormalMode(boolean normalMode){
        if(normalMode) {
            initDrawMode(false);
            initDeleteMode(false);
            this.drawBegin = null;
            this.drawEnd = null;
            this.touchCount = 0;
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (getChildAt(i).getTag().equals(circuitComponentTag)) {
                CircuitComponent circuitComponent = (CircuitComponent) getChildAt(i);
                circuitComponent.setNormalMode(normalMode);
            } else if (getChildAt(i).getTag().equals(connectionPointTag)) {
                ConnectionPoint connectionPoint = (ConnectionPoint) getChildAt(i);
                connectionPoint.setNormalMode(normalMode);
            }
        }
        this.normalMode = normalMode;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        if (drawMode) {
            if (drawBegin != null && drawEnd != null) {
                if (currentDrawPointBeginComponent != null) {
                    currentDrawPointBeginComponent.setPointLocationOnComponent(drawBegin);
                } else if (startConnectionPoint != null) {
                    startConnectionPoint.setDrawPointLocation(drawBegin);
                } else if (currentDrawPointBeginComponent == null && startConnectionPoint == null && !preventOnDrawBegin) {
                    ConnectionPoint connectionPoint = new ConnectionPoint(getContext());
                    connectionPoint.setXYCoordinatesBasedOnPoint(drawBegin);
                    connectionPoint.setOnCircuitComponentTouchedListener(new OnCircuitComponentTouchedListener());
                    connectionPoint.setNormalMode(this.normalMode);
                    this.addView(connectionPoint);
                    preventOnDrawBegin = true;
                }
                if (currentDrawpointEndComponent != null) {
                    currentDrawpointEndComponent.setPointLocationOnComponent(drawEnd);
                } else if (endConnectionPoint != null) {
                    endConnectionPoint.setDrawPointLocation(drawEnd);
                } else if (currentDrawpointEndComponent == null && endConnectionPoint == null && !preventOnDrawEnd) {
                    ConnectionPoint connectionPoint = new ConnectionPoint(getContext());
                    connectionPoint.setXYCoordinatesBasedOnPoint(drawEnd);
                    connectionPoint.setOnCircuitComponentTouchedListener(new OnCircuitComponentTouchedListener());
                    connectionPoint.setNormalMode(this.normalMode);
                    this.addView(connectionPoint);
                    preventOnDrawEnd = true;
                }

                lineCoordinates.add(drawBegin);
                lineCoordinates.add(drawEnd);
                drawEnd = null;
                drawBegin = null;
                currentDrawPointBeginComponent = null;
                currentDrawpointEndComponent = null;
                endConnectionPoint = null;
                startConnectionPoint = null;
            }
        }

        canvas.translate(mPosX, mPosY);
        canvas.scale(mScaleFactor, mScaleFactor, mFocusX, mFocusY);
        float lineCoordinatesArray[] = pointListToFloatArray(lineCoordinates);
        canvas.drawLines(lineCoordinatesArray, paint);
        canvas.save();
        super.dispatchDraw(canvas);
        canvas.restore();

    }


    private float[] pointListToFloatArray(List<PointF> points) {
        int size = 2 * points.size();
        int index = 0;
        float floatArray[] = new float[size];
        for (PointF point : points) {
            floatArray[index] = point.x;
            ++index;
            floatArray[index] = point.y;
            ++index;
        }


        return floatArray;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mDispatchTouchEventWorkingArray[0] = ev.getX();
        mDispatchTouchEventWorkingArray[1] = ev.getY();
        mDispatchTouchEventWorkingArray = screenPointsToScaledPoints(mDispatchTouchEventWorkingArray);
        ev.setLocation(mDispatchTouchEventWorkingArray[0],
                mDispatchTouchEventWorkingArray[1]);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * Although the docs say that you shouldn't override this, I decided to do
     * so because it offers me an easy way to change the invalidated area to my
     * likening.
     */
    /*
    @Override
    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {

        mInvalidateWorkingArray[0] = dirty.left;
        mInvalidateWorkingArray[1] = dirty.top;
        mInvalidateWorkingArray[2] = dirty.right;
        mInvalidateWorkingArray[3] = dirty.bottom;


        mInvalidateWorkingArray = scaledPointsToScreenPoints(mInvalidateWorkingArray);
        dirty.set(Math.round(mInvalidateWorkingArray[0]), Math.round(mInvalidateWorkingArray[1]),
                Math.round(mInvalidateWorkingArray[2]), Math.round(mInvalidateWorkingArray[3]));

        location[0] *= mScaleFactor;
        location[1] *= mScaleFactor;
        return super.invalidateChildInParent(location, dirty);
    }

     */
    private float[] scaledPointsToScreenPoints(float[] a) {
        mScaleMatrix.mapPoints(a);
        mTranslateMatrix.mapPoints(a);
        return a;
    }

    private float[] screenPointsToScaledPoints(float[] a) {
        mTranslateMatrixInverse.mapPoints(a);
        mScaleMatrixInverse.mapPoints(a);
        return a;
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        onCircuitComponentDragListenerTest.setmTranslateMatrixInverse(mTranslateMatrixInverse);
        onCircuitComponentDragListenerTest.setmScaleMatrixInverse(mScaleMatrixInverse);
        return onCircuitComponentDragListenerTest.onDrag(this, event);
    }


    private boolean movingComponents(MotionEvent ev) {
        mOnTouchEventWorkingArray[0] = ev.getX();
        mOnTouchEventWorkingArray[1] = ev.getY();
        mOnTouchEventWorkingArray = scaledPointsToScreenPoints(mOnTouchEventWorkingArray);
        ev.setLocation(mOnTouchEventWorkingArray[0], mOnTouchEventWorkingArray[1]);
        mScaleDetector.onTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();

                mLastTouchX = x;
                mLastTouchY = y;

                // Save the ID of this pointer
                mActivePointerId = ev.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                mPosX += dx;
                mPosY += dy;
                mTranslateMatrix.preTranslate(dx, dy);
                mTranslateMatrix.invert(mTranslateMatrixInverse);

                mLastTouchX = x;
                mLastTouchY = y;

                invalidate();
                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                // Extract the index of the pointer that left the touch sensor
                final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }
        return true;

    }

    private boolean drawConnections(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (touchCount == 0) {
                drawBegin = new PointF(ev.getX(), ev.getY());
                drawEnd = null;
                touchCount++;
                CircuitComponent component = isPointOnCircuitComponent(drawBegin);
                startConnectionPoint = isPointOnConnectionPoint(drawBegin);
                currentDrawPointBeginComponent = component;
                return true;
            } else if (touchCount == 1) {
                drawEnd = new PointF(ev.getX(), ev.getY());
                touchCount = 0;
                CircuitComponent component = isPointOnCircuitComponent(drawEnd);
                endConnectionPoint = isPointOnConnectionPoint(drawEnd);
                currentDrawpointEndComponent = component;
                preventOnDrawBegin = false;
                preventOnDrawEnd = false;
                invalidate();
                return true;
            }
        }

        return false;
    }

    private boolean delete(MotionEvent event){
        PointF point = new PointF(event.getX(),event.getY());
        CircuitComponent component = isPointOnCircuitComponent(point);
        ConnectionPoint connectionPoint = isPointOnConnectionPoint(point);
        if (component != null && connectionPoint == null){
            component.deleteAllPoints(lineCoordinates);
            this.removeView(component);
            invalidate();
            return true;
        }
        if (connectionPoint != null && component == null){
            connectionPoint.deleteAllPoints(lineCoordinates);
            this.removeView(connectionPoint);
            invalidate();
            return true;
        }
        if(component == null && connectionPoint == null){
            invalidate();
            deletePoints(point);
            return true;
        }
        return false;
    }

    private boolean deletePoints(PointF point){
        PointF lastPoint = lineCoordinates.get(0);
        for (int i = 1 ; i< lineCoordinates.size();i++){
            float lambdaX = (point.x-lastPoint.x)/lineCoordinates.get(i).x;
            float lambdaY = (point.y-lastPoint.y)/lineCoordinates.get(i).y;
            if(lambdaX == lambdaY){
                ConnectionPoint connectionPoint = isPointOnConnectionPoint(point);
                if (connectionPoint != null){
                    connectionPoint.setPointLeft(null);
                }
                lineCoordinates.remove(lineCoordinates.get(i));
                lineCoordinates.remove(lastPoint);
                return true;
            }
            i++;
            if (i== lineCoordinates.size()){
                break;
            }
            lastPoint = lineCoordinates.get(i);
        }
        return  false;
    }





    public ConnectionPoint isPointOnConnectionPoint(PointF point) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).getTag().equals(connectionPointTag)) {
                ConnectionPoint child = (ConnectionPoint) getChildAt(i);
                float left = child.getX();
                float top = child.getY();
                float bottom = top + child.getHeight();
                float right = left + child.getWidth();
                RectF rectF = new RectF(left, top, right, bottom);
                if (rectF.contains(point.x, point.y)) {
                    return child;
                }

            }
        }
        return null;
    }

    public CircuitComponent isPointOnCircuitComponent(PointF point) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).getTag().equals(circuitComponentTag)) {
                CircuitComponent child = (CircuitComponent) getChildAt(i);
                float rightChild = child.getX() + child.getWidth();
                float bottomChild = child.getY() + child.getHeight();
                float x = child.getX();
                float y = child.getY();
                float rotation = child.getRotation();
                Matrix m = new Matrix();
                m.setRotate(-rotation, x + 25, y + 50);
                RectF rectF = new RectF(x, y, rightChild, bottomChild);
                m.mapRect(rectF);

                if (rectF.contains(point.x, point.y)) {
                    return child;
                }
            }
        }

        return null;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (drawMode) {
            drawConnections(ev);
        } else if(normalMode) {
            movingComponents(ev);
        }else if(deleteMode){
            delete(ev);
        }
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (!drawMode) {
                mScaleFactor *= detector.getScaleFactor();
                if (detector.isInProgress()) {
                    mFocusX = detector.getFocusX();
                    mFocusY = detector.getFocusY();
                }
                mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
                mScaleMatrix.setScale(mScaleFactor, mScaleFactor,
                        mFocusX, mFocusY);
                mScaleMatrix.invert(mScaleMatrixInverse);
                invalidate();
                requestLayout();
            }


            return true;
        }
    }

    public OnCircuitComponentDragListener getOnCircuitComponentDragListener() {
        return onCircuitComponentDragListener;
    }

    public void setOnCircuitComponentDragListener(OnCircuitComponentDragListener
                                                          onCircuitComponentDragListener) {
        this.onCircuitComponentDragListener = onCircuitComponentDragListener;
    }

    public OnCircuitComponentDragListenerTest getOnCircuitComponentDragListenerTest() {
        return onCircuitComponentDragListenerTest;
    }

    public void setOnCircuitComponentDragListenerTest(OnCircuitComponentDragListenerTest
                                                              onCircuitComponentDragListenerTest) {
        this.onCircuitComponentDragListenerTest = onCircuitComponentDragListenerTest;
    }
}


