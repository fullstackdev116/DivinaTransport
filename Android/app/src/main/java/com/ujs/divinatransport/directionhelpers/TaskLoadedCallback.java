package com.ujs.divinatransport.directionhelpers;

/**
 * Created by Vishal on 10/20/2018.
 */

public interface TaskLoadedCallback {
    void onTaskDone(long distance, long duration, Object... values);
}
