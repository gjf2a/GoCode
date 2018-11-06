package com.stack.gocode.localData;

/**
 * Created by gabriel on 11/6/18.
 */

public class TransitionTableWrapper {
    private TransitionTable table;

    public TransitionTableWrapper(TransitionTable table) {
        setTable(table);
    }

    public void setTable(TransitionTable table) {this.table = table;}

    public TransitionTable get() {return table;}
}
