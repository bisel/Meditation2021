package com.soulfriends.meditation.view.nestedext;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class NetServiceExtViewPager extends ViewPager {

    public NetServiceExtViewPager(@NonNull Context context) {
        super(context);
    }

    public NetServiceExtViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private ParentItemExtAdapter parentItemExtAdapter;

    public void SetParentItemExtAdapter(ParentItemExtAdapter parentItemExtAdapter)
    {
        this.parentItemExtAdapter = parentItemExtAdapter;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = ev.getAction() & MotionEvent.ACTION_MASK;

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                //parentItemExtAdapter.pauseRun();

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
