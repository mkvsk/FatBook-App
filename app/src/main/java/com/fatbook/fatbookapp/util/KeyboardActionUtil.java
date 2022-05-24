package com.fatbook.fatbookapp.util;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import com.fatbook.fatbookapp.R;

public class KeyboardActionUtil {

    private final View view;
    private final Activity activity;
    private boolean isKeyboardVisible = false;
    private View viewToHide;

    public final ViewTreeObserver.OnGlobalLayoutListener listenerForAdjustResize = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            final Rect rectangle = new Rect();
            final View contentView = view;
            contentView.getWindowVisibleDisplayFrame(rectangle);
            int screenHeight = contentView.getRootView().getHeight();
            int keypadHeight = screenHeight - rectangle.bottom;
            boolean isKeyboardNowVisible = keypadHeight > screenHeight * 0.15;

            if (isKeyboardVisible != isKeyboardNowVisible) {
                if (isKeyboardNowVisible) {
                    activity.findViewById(R.id.bottom_navigation).setVisibility(View.GONE);
                } else {
                    activity.findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
                }
            }
            isKeyboardVisible = isKeyboardNowVisible;
        }
    };

    public final ViewTreeObserver.OnGlobalLayoutListener listenerForAdjustPan = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            final Rect rectangle = new Rect();
            final View contentView = view;
            contentView.getWindowVisibleDisplayFrame(rectangle);
            int screenHeight = contentView.getRootView().getHeight();
            int keypadHeight = screenHeight - rectangle.bottom;
            boolean isKeyboardNowVisible = keypadHeight > screenHeight * 0.15;

            if (isKeyboardVisible != isKeyboardNowVisible) {
                if (isKeyboardNowVisible) {
                    viewToHide.setVisibility(View.VISIBLE);
                } else {
                    viewToHide.setVisibility(View.GONE);
                }
            }
            isKeyboardVisible = isKeyboardNowVisible;
        }
    };

    public KeyboardActionUtil(View view, Activity activity) {
        this.view = view;
        this.activity = activity;
    }

    public KeyboardActionUtil(View view, Activity activity, View viewToHide) {
        this.view = view;
        this.activity = activity;
        this.viewToHide = viewToHide;
    }
}
