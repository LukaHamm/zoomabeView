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
import com.lukh.zoomabeview.Listeners.OnDirectionImageClickedListener;
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
    private ImageView currentDirectionImage;
    private ImageView voltageDirectionImage;
    private final String rotateButtonId = "rotateButton_";
    private final String componentId = "circuitComponent_";
    private final String resistanceId = "resistance";
    private final String voltageSourceId = "voltage_source";
    private final String currentSourceId = "current_source";
    private final String currentDirectionId = "current_direction";
    private final String voltageDirectionId = "voltage_direction";
    private int height;
    private int width;
    private boolean drawMode;
    private boolean deleteMode;
    private boolean normalMode = true;
    private PointF topPoint;
    private PointF bottomPoint;
    private PointF offSetTopPoint= new PointF(45,0);
    private PointF offSetBottomPoint = new PointF(45,90);
    private String connection;
    private enum Description  {VOLTAGESOURCE,CURRENTSOURCE,RESISTANCE};
    private Description description = null;
    private Integer value = 0;
    private Integer uniqueComponentId = 0;
    private View nextComponent;


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
        PointF pointsToDelete [] = new PointF[4];
        int currentIndex = 0;
        for(int i = 0 ; i< points.length;i++){
            int indexPoint = lineCoordinates.indexOf(points[i]);
            if(indexPoint == -1){
                continue;
            }
            int indexPointConnection = indexPoint%2==0? indexPoint+1:indexPoint-1;
            pointsToDelete[currentIndex] = lineCoordinates.get(indexPoint);
            ++currentIndex;
            pointsToDelete[currentIndex] = lineCoordinates.get(indexPointConnection);
            ++currentIndex;
        }
        for(int i = 0; i<pointsToDelete.length;i++){
            if(pointsToDelete[i] != null) {
                lineCoordinates.remove(pointsToDelete[i]);
            }
        }


    }

    public void initAllChild() {
        relativeLayout = (RelativeLayout) getChildAt(0);
        int childCountRelLayout = relativeLayout.getChildCount();
        for (int i = 0; i < childCountRelLayout; i++) {
            View v = relativeLayout.getChildAt(i);
            if ((v.getResources().getResourceEntryName(v.getId()).equals(componentId))) {
                this.componentSymbol = (ImageView) v;
            } else if(v.getResources().getResourceEntryName(v.getId()).equals(rotateButtonId)) {
                this.rotateButton = (Button) v;
            }else if(v.getResources().getResourceEntryName(v.getId()).equals(currentDirectionId)) {
                this.currentDirectionImage = (ImageView) v;
            }else if(v.getResources().getResourceEntryName(v.getId()).equals(voltageDirectionId)){
                this.voltageDirectionImage = (ImageView)v;
            }
        }
        rotateButton.setEnabled(false);
        rotateButton.setVisibility(INVISIBLE);
        currentDirectionImage.setVisibility(INVISIBLE);
        currentDirectionImage.setTag(currentDirectionId);
        voltageDirectionImage.setVisibility(INVISIBLE);
        voltageDirectionImage.setTag(voltageDirectionId);
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
            this.description = Description.CURRENTSOURCE;

        }
        if (id.equals(resistanceId)) {
            componentSymbol.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.resistance));
            this.description = Description.RESISTANCE;
        }
        if (id.equals(voltageSourceId)) {
            componentSymbol.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.voltagesource));
            this.description = Description.VOLTAGESOURCE;
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
        if (this.getOffSetTopPoint().y == 60 || this.getOffSetTopPoint().y == 40) {
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

    public ImageView getCurrentDirectionImage() {
        return currentDirectionImage;
    }

    public ImageView getVoltageDirectionImage() {
        return voltageDirectionImage;
    }

    public void setCurrentDirectionImage(ImageView currentDirectionImage) {
        this.currentDirectionImage = currentDirectionImage;
    }

    public void setVoltageDirectionImage(ImageView voltageDirectionImage) {
        this.voltageDirectionImage = voltageDirectionImage;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getUniqueComponentId() {
        return uniqueComponentId;
    }

    public void setUniqueComponentId(Integer uniqueComponentId) {
        this.uniqueComponentId = uniqueComponentId;
    }

    public View getNextComponent() {
        return nextComponent;
    }

    public void setNextComponent(View nextComponent) {
        this.nextComponent = nextComponent;
    }
}


