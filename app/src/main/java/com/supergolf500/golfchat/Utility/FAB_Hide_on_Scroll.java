package com.supergolf500.golfchat.Utility;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by supergolf500 on 10/11/2559.
 */
public class FAB_Hide_on_Scroll extends FloatingActionButton.Behavior {

    public FAB_Hide_on_Scroll(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);


        //Log.i("scoll","Tag = " + child.getTag().toString());

        if(child.getTag().toString().equals("0")) {  //--- ใส่มาเองเพื่อจะให้ทำงานเฉพาะ Tag 0

            //child -> Floating Action Button
            if (child.getVisibility() == View.VISIBLE && dyConsumed > 0) {
                child.hide();
            } else if (child.getVisibility() == View.GONE && dyConsumed < 0) {
                child.show();
            }
        }

    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }
}