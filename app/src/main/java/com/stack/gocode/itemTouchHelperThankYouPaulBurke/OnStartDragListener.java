package com.stack.gocode.itemTouchHelperThankYouPaulBurke;


import android.support.v7.widget.RecyclerView;

/*
 * Listener for manual initiation of a drag.
 *
 * Also from Paul Burke
 */
public interface OnStartDragListener {

    /*
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);

}
