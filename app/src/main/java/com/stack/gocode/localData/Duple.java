package com.stack.gocode.localData;

public class Duple<X,Y> { //https://github.com/gjf2a/csci235spr17/blob/master/src/edu/hendrix/modeselection/util/Duple.java
    private X x;
    private Y y;

    public Duple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public X getFirst() {return x;}
    public Y getSecond() {return y;}

    public void setFirst(X x) {this.x = x;}
    public void setSecond(Y y) {this.y = y;}

    @Override
    public String toString() {
        return "{" + x + "}{" + y + "}";
    }

    @Override
    public int hashCode() {
        return x.hashCode() + y.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Duple<?,?>) {
            @SuppressWarnings("unchecked")
            Duple<X,Y> that = (Duple<X,Y>)other;
            return this.x.equals(that.x) && this.y.equals(that.y);
        } else {
            return false;
        }
    }
}