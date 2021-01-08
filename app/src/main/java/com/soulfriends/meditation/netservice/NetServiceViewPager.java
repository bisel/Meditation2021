package com.soulfriends.meditation.netservice;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.soulfriends.meditation.view.nested.ParentItemAdapter;

public class NetServiceViewPager extends ViewPager {

    public NetServiceViewPager(@NonNull Context context) {
        super(context);
    }

    public NetServiceViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private ParentItemAdapter parentItemAdapter;

    public void SetParentItemAdapter(ParentItemAdapter parentItemAdapter)
    {
        this.parentItemAdapter = parentItemAdapter;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = ev.getAction() & MotionEvent.ACTION_MASK;

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                parentItemAdapter.pauseRun();

                //Log.d("Meditation", "ACTION_DOWN");
            }
            break;
            case MotionEvent.ACTION_MOVE:
                break;
//            case MotionEvent.ACTION_UP: {
//                parentItemAdapter.startRun();
//
//                Log.d("Meditation", "ACTION_UP");
//            }
//            break;
        }

        return super.onInterceptTouchEvent(ev);
    }
}