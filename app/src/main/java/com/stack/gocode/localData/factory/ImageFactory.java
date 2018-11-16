package com.stack.gocode.localData.factory;

import com.stack.gocode.localData.Duple;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by gabriel on 11/8/18.
 */

public class ImageFactory {
    private TreeMap<String,WrappedLabel> labels = new TreeMap<>();
    private ArrayList<Duple<WrappedLabel,Mat>> images = new ArrayList<>();
    private int height = 0, width = 0;

    public ArrayList<String> getAllLabelNames() {
        return new ArrayList<>(labels.keySet());
    }

    public String generateLabel() {
        String generated = "label" + (labels.size() + 1);
        labels.put(generated, new WrappedLabel(generated));
        return generated;
    }

    public void addLabel(String label) {
        if (!labels.containsKey(label)) {
            labels.put(label, new WrappedLabel(label));
        }
    }

    public void renameLabel(String original, String updated) {
        WrappedLabel wl = labels.remove(original);
        wl.rename(updated);
        labels.put(updated, wl);
    }

    public void addImage(String label, Mat image) {
        if (!labels.containsKey(label)) {
            addLabel(label);
        }
        images.add(new Duple<>(labels.get(label), image));
        height = Math.max(height, image.height());
        width = Math.max(width, image.width());
    }

    public int numImages() {
        return images.size();
    }

    public Mat getImage(int i) {
        return images.get(i).getSecond();
    }

    public String getLabel(int i) {
        return images.get(i).getFirst().get();
    }

    public int numLabels() {
        return labels.size();
    }

    public int imageWidths() {
        return width;
    }

    public int imageHeights() {
        return height;
    }

    public ArrayList<Duple<WrappedLabel,Mat>> getShuffledImageList() {
        ArrayList<Duple<WrappedLabel,Mat>> result = new ArrayList<>();
        result.addAll(images);
        Collections.shuffle(result);
        return result;
    }
}
