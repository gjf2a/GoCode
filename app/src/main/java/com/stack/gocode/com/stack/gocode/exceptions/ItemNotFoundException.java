package com.stack.gocode.com.stack.gocode.exceptions;

/**
 * Created by gabriel on 10/23/18.
 */

public class ItemNotFoundException extends Exception {
    public ItemNotFoundException(String itemType) {
        super(itemType + " not found");
    }
}
