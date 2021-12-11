package com.lukh.zoomabeview.view;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.lukh.zoomabeview.Listeners.OnCircuitComponentLongClickListener;
import com.lukh.zoomabeview.Listeners.OnCircuitComponentTouchedListener;
import com.lukh.zoomabeview.Listeners.OnRotateButtonClickListener;
import com.lukh.zoomabeview.R;

import java.util.List;

public class CircuitComponent extends LinearLayout {

    private OnCircuitComponentTouchedListener onCircuitComponentTouchedListener;
    private OnCircuitComponentLongClickListener onCircuitComponentLongClickListener;
    private ImageView componentSymbol;
    private Button rotateButton;
    private GestureDetector gestureDetector;
    private RelativeLayout relativeLayout;
    private final String rotateButtonId = "rotateButton_";
    private final String componentId = "circuitComponent_";
    private final String resistanceId = "resistance";
    private final String voltageSourceId = "voltage_source";
    private final String currentSourceId = "current_source";
    private int height;
    private int width;
    private boolean drawMode;
    private boolean deleteMode;
    private boolean normalMode = true;
    private PointF topPoint;
    private PointF bottomPoint;
    private PointF offSetTopPoint= new PointF(25,0);
    private PointF offSetBottomPoint = new PointF(25,90);
    private String connection;

    public CircuitComponent(Context context, Integer id) {
        super(context);
        setId(id);


    }

    public CircuitComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setTag("CircuitComponent");
        initializeView(context);
    }


    public CircuitComponent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.height = heightMeasureSpec;
        this.width = widthMeasureSpec;

    }


    private void initializeView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sidespinner_view, this);
    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initAllChild();
        initListeners();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(normalMode) {
            onCircuitComponentTouchedListener.onTouch(this, event);
        }else{
            PointF point = new PointF(this.getX(),this.getY());
        }

        return super.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            onCircuitComponentLongClickListener.onLongClick(CircuitComponent.this);
        }
    }

    public void deleteAllPoints(List<PointF> lineCoordinates){
        //Abfragen ob Punkt gerade oder ungerader index
        PointF points [] = {this.topPoint,this.bottomPoint};
        float pointIndexList [] = new float[4];
        int currentIndex = 0;
        for(int i = 0 ; i< points.length;i++){
            float indexPoint = lineCoordinates.indexOf(points[i]);
            if(indexPoint == -1){
                continue;
            }
            float indexPointConnection = indexPoint%2==0? indexPoint+1:indexPoint-1;
            pointIndexList[currentIndex] = indexPoint;
            ++currentIndex;
            pointIndexList[currentIndex] = indexPointConnection;
            ++currentIndex;
        }
        for(int i = 0; i<pointIndexList.length;i++){
            lineCoordinates.remove(pointIndexList[i]);
        }


    }

    public void initAllChild() {
        int childCount = getChildCount();
        relativeLayout = (RelativeLayout) getChildAt(0);
        //relativeLayout = (RelativeLayout) findViewById(R.id.circuitComponentContainer);
        int childCountRelLayout = relativeLayout.getChildCount();
        for (int i = 0; i < childCountRelLayout; i++) {
            View v = relativeLayout.getChildAt(i);
            if ((v.getResources().getResourceEntryName(v.getId()).equals(componentId))) {
                this.componentSymbol = (ImageView) v;
            } else {
                this.rotateButton = (Button) v;
            }
        }
        //rotateButtonParams = rotateButton.getLayoutParams();
        rotateButton.setEnabled(false);
        rotateButton.setVisibility(INVISIBLE);
            //componentSymbolParams = componentSymbol.getLayoutParams();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) componentSymbol.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        params.setMarginEnd(0);
        initComponent();
    }


    private void initComponent() {
        String id = this.getResources().getResourceEntryName(this.getId());
        componentSymbol.setImageDrawable(null);
        if (id.equals(currentSourceId)) {
            componentSymbol.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.currentsource));
        }
        if (id.equals(resistanceId)) {
            componentSymbol.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.resistance));
        }
        if (id.equals(voltageSourceId)) {
            componentSymbol.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.voltagesource));

        }
    }

    public void initListeners() {
        int parentMeasurements[] = new int[2];
        parentMeasurements[0] = this.width;
        parentMeasurements[1] = this.height;
        rotateButton.setOnClickListener(new OnRotateButtonClickListener());
    }

    public void setPointLocationOnComponent(PointF drawPoint) {
        PointF currentDrawPointTop = this.getTopPoint();
        PointF currentDrawPointBottom = this.getBottomPoint();
        boolean isTopPoint = false;
        if (this.getOffSetTopPoint().y == 0 || this.getOffSetBottomPoint().y == 10) {
            isTopPoint = (Math.abs(drawPoint.y - currentDrawPointTop.y)) < (Math.abs(drawPoint.y - currentDrawPointBottom.y));
        }
        if (this.getOffSetTopPoint().y == 50) {
            isTopPoint = Math.abs(drawPoint.x - currentDrawPointTop.x) < Math.abs(drawPoint.x - currentDrawPointBottom.x);
        }
        if (isTopPoint) {
            drawPoint.y = this.getY() + this.getOffSetTopPoint().y;
            drawPoint.x = this.getX() + this.getOffSetTopPoint().x;
            this.topPoint = drawPoint;
        } else {
            drawPoint.y = this.getY() + this.getOffSetBottomPoint().y;
            drawPoint.x = this.getX() + this.getOffSetBottomPoint().x;
            this.bottomPoint = drawPoint;
        }

    }

    public Button getRotateButton() {
        return rotateButton;
    }

    public void setRotateButton(Button rotateButton) {
        this.rotateButton = rotateButton;
    }

    public RelativeLayout getRelativeLayout() {
        return relativeLayout;
    }

    public void setRelativeLayout(RelativeLayout relativeLayout) {
        this.relativeLayout = relativeLayout;
    }

    public ImageView getComponentSymbol() {
        return componentSymbol;
    }

    public void setComponentSymbol(ImageView componentSymbol) {
        this.componentSymbol = componentSymbol;
    }

    public void generateGestureDetector(){
        this.gestureDetector = new GestureDetector(getContext(),new GestureListener());
    }

    public OnCircuitComponentLongClickListener getOnCircuitComponentLongClickListener() {
        return onCircuitComponentLongClickListener;
    }

    public void setOnCircuitComponentLongClickListener(OnCircuitComponentLongClickListener onCircuitComponentLongClickListener) {
        this.onCircuitComponentLongClickListener = onCircuitComponentLongClickListener;
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

    public PointF getTopPoint() {
        return topPoint;
    }

    public void setTopPoint(PointF topPoint) {

        this.topPoint = topPoint;
    }

    public PointF getBottomPoint() {
        return bottomPoint;
    }

    public void setBottomPoint(PointF bottomPoint) {
        this.bottomPoint = bottomPoint;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public PointF getOffSetTopPoint() {
        return offSetTopPoint;
    }

    public void setOffSetTopPoint(PointF offSetTopPoint) {
        this.offSetTopPoint = offSetTopPoint;
    }

    public PointF getOffSetBottomPoint() {
        return offSetBottomPoint;
    }

    public void setOffSetBottomPoint(PointF offSetBottomPoint) {
        this.offSetBottomPoint = offSetBottomPoint;
    }

    public boolean isNormalMode() {
        return normalMode;
    }

    public void setNormalMode(boolean normalMode) {
        this.normalMode = normalMode;
    }
}


