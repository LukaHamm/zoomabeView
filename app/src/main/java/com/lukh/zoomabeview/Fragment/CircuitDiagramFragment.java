package com.lukh.zoomabeview.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.android.material.appbar.AppBarLayout;
import com.lukh.zoomabeview.Listeners.OnCircuitComponentDragListener;
import com.lukh.zoomabeview.Listeners.OnCircuitComponentDragListenerTest;
import com.lukh.zoomabeview.Listeners.OnCircuitComponentLongClickListener;
import com.lukh.zoomabeview.Listeners.OnCircuitComponentTouchedListener;
import com.lukh.zoomabeview.R;
import com.lukh.zoomabeview.view.CircuitComponent;
import com.lukh.zoomabeview.view.ZoomableViewGroup;

public class CircuitDiagramFragment extends Fragment {

    private LinearLayout circuitComponentLinearLayout;
    private ZoomableViewGroup ciruitDiagramCardView;
    private CircuitComponent resistance;
    private CircuitComponent voltageSource;
    private CircuitComponent currentSource;
    private Context context;
    private OnCircuitComponentTouchedListener onCircuitComponentTouchedListener;
    public  static final String id = "CircuitDiagramFragment";
    private boolean drawMode;
    private boolean deleteMode;
    private boolean normalMode;



    public CircuitDiagramFragment(Context context){
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.circuit_diagram,container,false);
        ciruitDiagramCardView = view.findViewById(R.id.circuit_diagram);
        circuitComponentLinearLayout =  view.findViewById(R.id.componentstackContainer);
        onCircuitComponentTouchedListener = new OnCircuitComponentTouchedListener();
        ciruitDiagramCardView.setOnCircuitComponentDragListenerTest(new OnCircuitComponentDragListenerTest(context,onCircuitComponentTouchedListener));
        initCircuitComponents(view);
        setHasOptionsMenu(true);
        return view;
    }


    private void  initCircuitComponents(View view){
        resistance = view.findViewById(R.id.resistance);
        voltageSource = view.findViewById(R.id.voltage_source);
        currentSource = view.findViewById(R.id.current_source);
        resistance.setOnCircuitComponentTouchedListener(onCircuitComponentTouchedListener);
        voltageSource.setOnCircuitComponentTouchedListener(onCircuitComponentTouchedListener);
        currentSource.setOnCircuitComponentTouchedListener(onCircuitComponentTouchedListener);




    }

    private void initDrawMode(){
        ciruitDiagramCardView.initDrawMode(drawMode);

    }
    private void initDeleteMode(){
        ciruitDiagramCardView.initDeleteMode(deleteMode);

    }
    private void initNormalMode(){
        ciruitDiagramCardView.initNormalMode(normalMode);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.draw:
                drawMode = true;
                initDrawMode();
                break;
            case R.id.delete:
                deleteMode = true;
                initDeleteMode();
                break;
            case R.id.normal:
                normalMode = true;
                initNormalMode();
        }

        return true;
    }




}
