package com.stack.gocode.communications;

/**
 * Created by gabriel on 6/3/18.
 */

public interface TalkerListener {
    public void sendComplete(int status);
    public void receiveComplete(byte[] received);
    public void error();
}