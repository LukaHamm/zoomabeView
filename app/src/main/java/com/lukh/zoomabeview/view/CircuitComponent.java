package com.lukh.zoomabeview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.lukh.zoomabeview.Listeners.OnCircuitComponentLongClickListener;
import com.lukh.zoomabeview.Listeners.OnCircuitComponentTouchedListener;
import com.lukh.zoomabeview.Listeners.OnRotateButtonClickListener;
import com.lukh.zoomabeview.R;

public class CircuitComponent extends LinearLayout {

    private OnCircuitComponentTouchedListener onCircuitComponentTouchedListener;
    private ImageView component;
    private Button rotateButton;
    private final String rotateButtonId = "rotateButton_";
    private final String componentId = "circuitComponent_";
    private int height;
    private int width;

    public CircuitComponent(Context context) {
        super(context);
    }

    public CircuitComponent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeView(context);
        initAllChild();
        initListeners();
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return onCircuitComponentTouchedListener.onTouch(this,event) ;
    }

    private void initializeView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sidespinner_view, this);
    }

    private void initAllChild () {
        int childCount = getChildCount();
        RelativeLayout relLayout = (RelativeLayout) getChildAt(childCount);
        int childCountRelLayout = relLayout.getChildCount();
        for (int i = 0;i < childCountRelLayout;i++){
            View v = relLayout.getChildAt(i);
            if ((v.getResources().getResourceEntryName(v.getId()).equals(componentId))){
                this.component = (ImageView) v;
            }else{
                this.rotateButton = (Button) v;
            }
        }
        rotateButton.setEnabled(false);
        rotateButton.setVisibility(INVISIBLE);
        component.setMinimumHeight(this.height);
        component.setMinimumWidth(this.width);
    }
    private void initListeners(){
        int parentMeasurements [] = new int[2];
        parentMeasurements[0] = this.width;
        parentMeasurements[1] = this.height;
        OnCircuitComponentLongClickListener onCircuitComponentLongClickListener = new OnCircuitComponentLongClickListener(rotateButton,component,parentMeasurements);
        setOnLongClickListener(onCircuitComponentLongClickListener);
        rotateButton.setOnClickListener(new OnRotateButtonClickListener());
    }



    public OnCircuitComponentTouchedListener getOnCircuitComponentTouchedListener() {
        return onCircuitComponentTouchedListener;
    }

    public void setOnCircuitComponentTouchedListener(OnCircuitComponentTouchedListener onCircuitComponentTouchedListener) {
        this.onCircuitComponentTouchedListener = onCircuitComponentTouchedListener;
    }



}
