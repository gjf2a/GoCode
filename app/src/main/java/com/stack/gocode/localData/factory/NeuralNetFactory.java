package com.stack.gocode.localData.factory;

import android.content.Context;
import android.util.Log;

import com.stack.gocode.localData.flagtypes.NeuralNetFlag;

import org.opencv.ml.ANN_MLP;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by gabriel on 11/15/18.
 */

public class NeuralNetFactory {
    private static final String ANN_PREFIX = "ANN_";
    private static final String TAG = NeuralNetFactory.class.getSimpleName();

    private TreeMap<String,ANN_MLP> file2net = new TreeMap<>();
    private TreeMap<String,ArrayList<String>> targetLabel2filenames = new TreeMap<>();

    public static NeuralNetFactory loadAll(Context context) {
        NeuralNetFactory factory = new NeuralNetFactory();
        for (String filename: context.getFilesDir().list()) {
            if (filename.startsWith(ANN_PREFIX)) {
                ANN_MLP net = ANN_MLP.load(filename);
                Log.i(TAG, "Opened " + filename);
                String[] parts = filename.split("_");
                int targetIndex = parts.length - 3;
                if (targetIndex >= 0) {
                    String targetLabel = parts[targetIndex];
                    factory.insertNeuralNet(targetLabel, filename, net);
                    Log.i(TAG, "Inserted into factory");
                } else {
                    Log.i(TAG, "Failed to insert into factory");
                }
            }
        }
        return factory;
    }

    private void insertNeuralNet(String targetLabel, String filename, ANN_MLP net) {
        if (!targetLabel2filenames.containsKey(targetLabel)) {
            targetLabel2filenames.put(targetLabel, new ArrayList<String>());
        }
        file2net.put(filename, net);
        targetLabel2filenames.get(targetLabel).add(filename);
    }

    public String addNeuralNet(ANN_MLP net, String targetLabel, int numHidden, Context context) {
        if (!targetLabel2filenames.containsKey(targetLabel)) {
            targetLabel2filenames.put(targetLabel, new ArrayList<String>());
        }
        int number = 1 + targetLabel2filenames.get(targetLabel).size();
        String filename = ANN_PREFIX + targetLabel + "_" + numHidden + "_" + number;
        insertNeuralNet(targetLabel, filename, net);
        save(net, filename, context);
        return filename;
    }

    public void save(ANN_MLP net, String filename, Context context) {
        File dir = context.getFilesDir();
        String totalName = dir.getAbsolutePath() + File.separator + filename;
        net.save(totalName);
    }

    public ArrayList<NeuralNetFlag> getAllNeuralNets() {
        ArrayList<NeuralNetFlag> nets = new ArrayList<>();
        for (Map.Entry<String,ANN_MLP> entry: file2net.entrySet()) {
            nets.add(new NeuralNetFlag(entry.getKey(), entry.getValue()));
        }
        return nets;
    }

    public boolean hasNet(String name) {
        return file2net.containsKey(name);
    }

    public NeuralNetFlag getNet(String name) {
        return new NeuralNetFlag(name, file2net.get(name));
    }
}
