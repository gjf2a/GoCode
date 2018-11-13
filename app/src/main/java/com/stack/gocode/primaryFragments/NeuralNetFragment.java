package com.stack.gocode.primaryFragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.WindowDecorActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.stack.gocode.ModalDialogs;
import com.stack.gocode.R;
import com.stack.gocode.localData.DatabaseHelper;

public class NeuralNetFragment extends Fragment {
    public static final String TAG = NeuralNetFragment.class.getSimpleName();

    private View myView;
    private EditText learningRateBox, shrinkBox, hiddenBox, iterationsBox;
    private Spinner targetLabelSpinner;
    private Button train;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.neural_net, container, false);

        learningRateBox = myView.findViewById(R.id.rate);
        shrinkBox = myView.findViewById(R.id.shrink);
        hiddenBox = myView.findViewById(R.id.hidden);
        iterationsBox = myView.findViewById(R.id.iterations);

        DatabaseHelper db = new DatabaseHelper(myView.getContext());
        targetLabelSpinner = myView.findViewById(R.id.targetLabelSpinner);
        if (db.imagesReady()) {
            ArrayAdapter<String> labelAdapter = new ArrayAdapter<String>(myView.getContext(), R.layout.spinner_item, db.getAllLabels());
            labelAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            targetLabelSpinner.setAdapter(labelAdapter);
        }

        train = myView.findViewById(R.id.train);
        train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    double rate = Double.parseDouble(learningRateBox.getText().toString());
                    int shrink = (int)Double.parseDouble(shrinkBox.getText().toString());
                    int iterations = (int)Double.parseDouble(iterationsBox.getText().toString());
                    int hiddenNodes = (int)Double.parseDouble(hiddenBox.getText().toString());
                    String targetLabel = targetLabelSpinner.getSelectedItem().toString();
                    Log.i(TAG, String.format("Train: Rate: %3.2f Shrink: %d Iterations: %d hiddenNodes: %d label: %s", rate, shrink, iterations, hiddenNodes, targetLabel));

                } catch (NumberFormatException nfe) {
                    ModalDialogs.notifyProblem(myView.getContext(), "Numerical field lacks appropriate value");
                } catch (Exception exc) {
                    ModalDialogs.notifyException(myView.getContext(), exc);
                }
            }
        });

        return myView;
    }
}
