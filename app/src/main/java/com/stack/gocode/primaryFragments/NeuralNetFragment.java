package com.stack.gocode.primaryFragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.stack.gocode.ModalDialogs;
import com.stack.gocode.R;
import com.stack.gocode.localData.ConfusionMatrix;
import com.stack.gocode.localData.DatabaseHelper;
import com.stack.gocode.localData.NeuralNetTrainingData;
import com.stack.gocode.localData.factory.WrappedLabel;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.ml.ANN_MLP;
import org.opencv.ml.Ml;

public class NeuralNetFragment extends Fragment {
    public static final String TAG = NeuralNetFragment.class.getSimpleName();

    private View myView;
    private EditText learningRateBox, shrinkBox, hiddenBox, iterationsBox;
    private Spinner targetLabelSpinner;
    private Button train;
    private TextView trainingLabel, truePos, falsePos, trueNeg, falseNeg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.neural_net, container, false);

        learningRateBox = myView.findViewById(R.id.rate);
        shrinkBox = myView.findViewById(R.id.shrink);
        hiddenBox = myView.findViewById(R.id.hidden);
        iterationsBox = myView.findViewById(R.id.iterations);
        trainingLabel = myView.findViewById(R.id.TrainThreadLabel);
        truePos = myView.findViewById(R.id.truePositive);
        trueNeg = myView.findViewById(R.id.trueNegative);
        falsePos = myView.findViewById(R.id.falsePositive);
        falseNeg = myView.findViewById(R.id.falseNegative);

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
                    final double rate = Double.parseDouble(learningRateBox.getText().toString());
                    final int shrink = (int)Double.parseDouble(shrinkBox.getText().toString());
                    final int iterations = (int)Double.parseDouble(iterationsBox.getText().toString());
                    final int hiddenNodes = (int)Double.parseDouble(hiddenBox.getText().toString());
                    final WrappedLabel targetLabel = new WrappedLabel(targetLabelSpinner.getSelectedItem().toString());

                    if (rate < 0.0) {
                        ModalDialogs.notifyProblem(myView.getContext(), String.format("Learning rate %3.2f is below zero", rate));
                    } else if (rate > 1.0) {
                        ModalDialogs.notifyProblem(myView.getContext(), String.format("Learning rate %3.2f is above one", rate));
                    } else if (iterations < 1) {
                        ModalDialogs.notifyProblem(myView.getContext(), String.format("%d iterations is below one", iterations));
                    } else if (shrink < 1) {
                        ModalDialogs.notifyProblem(myView.getContext(), String.format("Shrink level %d is below one", shrink));
                    } else if (hiddenNodes < 1) {
                        ModalDialogs.notifyProblem(myView.getContext(), String.format("Number of hidden nodes (%d) is below one", hiddenNodes));
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                trainingLabel.setText("Training...");
                            }
                        });
                        new Thread() {
                            @Override
                            public void run() {
                                DatabaseHelper db = new DatabaseHelper(getActivity());
                                int totalInputs = db.getImageHeights() * db.getImageWidths() / shrink;
                                ANN_MLP network = setupANN_MLP(totalInputs, hiddenNodes, iterations, rate);
                                Log.i(TAG, String.format("Train: Rate: %3.2f Shrink: %d Inputs: %d Iterations: %d hiddenNodes: %d label: %s", rate, shrink, totalInputs, iterations, hiddenNodes, targetLabel));
                                NeuralNetTrainingData data = db.makeTrainingTestingSets(targetLabel, 0.8);
                                network.train(data.getTrainingExamples(), Ml.ROW_SAMPLE, data.getTrainingLabels());
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        trainingLabel.setText("Training complete");
                                    }
                                });
                                db.addNeuralNetwork(network, targetLabel, hiddenNodes, myView.getContext());

                                Mat testExamples = data.getTestingExamples();
                                Mat testOutputs = data.getTestingLabels();
                                final ConfusionMatrix conf = new ConfusionMatrix();
                                for (int i = 0; i < testExamples.rows(); i++) {
                                    Mat output = Mat.zeros(1, 1, CvType.CV_32FC1);
                                    network.predict(testExamples.row(i), output, 0);
                                    conf.count(output, testOutputs.row(i));
                                }

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        truePos.setText(Integer.toString(conf.getTruePositives()));
                                        trueNeg.setText(Integer.toString(conf.getTrueNegatives()));
                                        falsePos.setText(Integer.toString(conf.getFalsePositives()));
                                        falseNeg.setText(Integer.toString(conf.getFalseNegatives()));
                                    }
                                });

                            }
                        }.start();
                    }

                } catch (NumberFormatException nfe) {
                    ModalDialogs.notifyProblem(myView.getContext(), "Numerical field lacks appropriate value");
                } catch (Exception exc) {
                    ModalDialogs.notifyException(myView.getContext(), exc);
                }
            }
        });

        return myView;
    }

    private ANN_MLP setupANN_MLP(int totalInputs, int hiddenNodes, int iterations, double rate) {
        Mat layout = new Mat(3, 1, CvType.CV_32S);
        layout.put(0, 0, new int[]{totalInputs, hiddenNodes, 1});
        ANN_MLP network = ANN_MLP.create();
        network.setLayerSizes(layout);
        network.setActivationFunction(ANN_MLP.SIGMOID_SYM);
        network.setTermCriteria(new TermCriteria(TermCriteria.MAX_ITER, iterations, 0));
        network.setTrainMethod(ANN_MLP.BACKPROP, rate, 0);
        return network;
    }
}
