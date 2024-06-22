package com.benny.openlauncher.viewutil;



import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class ResizableView extends ViewGroup {
    private static final int MIN_WIDTH = 150;
    private static final int MIN_HEIGHT = 150;
    private static final int BORDER_THRESHOLD = 40; // The threshold in pixels for the touch area
    private int lastTouchX;
    private int lastTouchY;
    private boolean isResizing = false;

    public ResizableView(Context context) {
        super(context);
    }

    public ResizableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
protected void onLayout(boolean changed, int l, int t, int r, int b) {
    // Loop through all the children of this ViewGroup
    for (int i = 0; i < getChildCount(); i++) {
        View child = getChildAt(i);

        // Lay out this child to fill the entire area of the ViewGroup
        if (child.getVisibility() != GONE) {
            child.layout(0, 0, r - l, b - t);
        }
    }
}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isWithinBorder(x, y)) {
                    lastTouchX = x;
                    lastTouchY = y;
                    isResizing = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isResizing) {
                    int dx = x - lastTouchX;
                    int dy = y - lastTouchY;
                    int newRight = getRight() + dx;
                    int newBottom = getBottom() + dy;

                    if (newRight - getLeft() > MIN_WIDTH && newBottom - getTop() > MIN_HEIGHT) {
                        this.layout(getLeft(), getTop(), newRight, newBottom);
                    }
                    lastTouchX = x;
                    lastTouchY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isResizing = false;
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean isWithinBorder(int x, int y) {
//        return (x >= getWidth() - BORDER_THRESHOLD || y >= getHeight() - BORDER_THRESHOLD);
        return ( y >= getHeight() - BORDER_THRESHOLD && y<=getHeight()+BORDER_THRESHOLD);
    }
}

