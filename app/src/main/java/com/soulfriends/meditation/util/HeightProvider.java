package com.soulfriends.meditation.util;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;

public class HeightProvider extends PopupWindow {
    public HeightProvider(Activity context, WindowManager windowManager, View parentView, KeyboardHeightListener listener) {
        super(context);

        LinearLayout popupView = new LinearLayout(context);
        popupView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        popupView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            DisplayMetrics metrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(metrics);

            Rect rect = new Rect();
            popupView.getWindowVisibleDisplayFrame(rect);

            int keyboardHeight = metrics.heightPixels - (rect.bottom - rect.top);
            int resourceID = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceID > 0) {
                keyboardHeight -= context.getResources().getDimensionPixelSize(resourceID);
            }
            if (keyboardHeight < 100) {
                keyboardHeight = 0;
            }
            boolean isLandscape = metrics.widthPixels > metrics.heightPixels;
            boolean keyboardOpen = keyboardHeight > 0;
            if (listener != null) {
                listener.onKeyboardHeightChanged(keyboardHeight, keyboardOpen, isLandscape);
            }
        });

        setContentView(popupView);

        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        setWidth(0);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new ColorDrawable(0));

        parentView.post(() -> showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0));
    }

    public interface KeyboardHeightListener {
        void onKeyboardHeightChanged(int keyboardHeight, boolean keyboardOpen, boolean isLandscape);
    }
}
//
//public class HeightProvider extends PopupWindow implements OnGlobalLayoutListener {
//    private Activity mActivity;
//    private View rootView;
//    private HeightListener listener;
//    private int heightMax; // Record the maximum height of the pop content area
//
//    public HeightProvider(Activity activity) {
//        super(activity);
//        this.mActivity = activity;
//
//        // Basic configuration
//        rootView = new View(activity);
//        setContentView(rootView);
//
//        // Monitor global Layout changes
//        rootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
//        setBackgroundDrawable(new ColorDrawable(0));
//
//        // Set width to 0 and height to full screen
//        setWidth(0);
//        setHeight(LayoutParams.MATCH_PARENT);
//
//        // Set keyboard pop-up mode
//        setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
//    }
//
//    public HeightProvider init() {
//        if (!isShowing()) {
//            final View view = mActivity.getWindow().getDecorView();
//            // Delay loading popupwindow, if not, error will be reported
//            view.post(new Runnable() {
//                @Override
//                public void run() {
//                    showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
//                }
//            });
//        }
//        return this;
//    }
//
//    public HeightProvider setHeightListener(HeightListener listener) {
//        this.listener = listener;
//        return this;
//    }
//
//    @Override
//    public void onGlobalLayout() {
//        Rect rect = new Rect();
//        rootView.getWindowVisibleDisplayFrame(rect);
//        if (rect.bottom > heightMax) {
//            heightMax = rect.bottom;
//        }
//
//        // The difference between the two is the height of the keyboard
//        int keyboardHeight = heightMax - rect.bottom;
//        if (listener != null) {
//            listener.onHeightChanged(keyboardHeight);
//        }
//    }
//
//    public interface HeightListener {
//        void onHeightChanged(int height);
//    }
//}