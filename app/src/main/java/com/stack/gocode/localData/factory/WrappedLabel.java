package com.stack.gocode.localData.factory;

import android.support.annotation.NonNull;

/**
 * Created by gabriel on 11/8/18.
 */

public class WrappedLabel implements Comparable<WrappedLabel> {
    private String label;

    public WrappedLabel(String label) {
        this.label = label;
    }

    public void rename(String newName) {
        this.label = newName;
    }

    public String get() {return label;}

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof WrappedLabel)) {
            throw new IllegalArgumentException("Type error in WrappedLabel");
        } else {
            return this.label.equals(((WrappedLabel)other).label);
        }
    }

    @Override
    public int hashCode() {return label.hashCode();}

    @Override
    public String toString() {
        return "WrappedLabel(" + label + ")";
    }

    @Override
    public int compareTo(@NonNull WrappedLabel wrappedLabel) {
        return this.label.compareTo(wrappedLabel.label);
    }
}
