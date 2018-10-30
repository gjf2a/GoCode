package com.stack.gocode.localData.factory;

import com.stack.gocode.localData.fuzzy.FuzzyFlag;

/**
 * Created by gabriel on 10/30/18.
 */

public interface FuzzyFlagFinder {
    public boolean fuzzyFlagExists(String name);
    public FuzzyFlag getFuzzyFlag(String name);
}
