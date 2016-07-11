package com.warpath.pasterview.PasterView;

import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;


public class ViewOnTouchListener implements View.OnTouchListener {
    Point pushPoint;
    int lastImgLeft;
    int lastImgTop;
    int lastImgRight;
    int lastImgBottom;
    FrameLayout.LayoutParams viewLP;
    FrameLayout.LayoutParams pushBtnLP;
    FrameLayout.LayoutParams deleteBtnLP;
    int lastPushBtnLeft;
    int lastPushBtnTop;
    int lastDeleteBtnLeft;
    int lastDeleteBtnTop;
    private View mPushView;
    private View mDeleteView;
    int maxX;
    int maxY;
    float moveX;
    float moveY;

    public ViewOnTouchListener(View mPushView, View mDeleteView, int maxX, int maxY) {
        this.mPushView = mPushView;
        this.mDeleteView = mDeleteView;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mPushView.setVisibility(View.VISIBLE);
                mDeleteView.setVisibility(View.VISIBLE);
                if (null == viewLP) {
                    viewLP = (FrameLayout.LayoutParams) view.getLayoutParams();
                }
                if (null == pushBtnLP) {
                    pushBtnLP = (FrameLayout.LayoutParams) mPushView.getLayoutParams();
                }
                if (null == deleteBtnLP) {
                    deleteBtnLP = (FrameLayout.LayoutParams) mDeleteView.getLayoutParams();
                }
                pushPoint = getRawPoint(event);
                lastImgLeft = viewLP.leftMargin;
                lastImgTop = viewLP.topMargin;
                lastImgRight = viewLP.rightMargin;
                lastImgBottom = viewLP.bottomMargin;
                lastPushBtnLeft = pushBtnLP.leftMargin;
                lastPushBtnTop = pushBtnLP.topMargin;
                lastDeleteBtnLeft = deleteBtnLP.leftMargin;
                lastDeleteBtnTop = deleteBtnLP.topMargin;
                return true;
            case MotionEvent.ACTION_MOVE:
                Point newPoint = getRawPoint(event);
                moveX = newPoint.x - pushPoint.x;
                moveY = newPoint.y - pushPoint.y;
                if (lastImgLeft + moveX <= 0 - viewLP.width/2) {
                    return true;
                } else if (lastImgLeft + viewLP.width + moveX > maxX + viewLP.width/2) {
                    return true;
                }
                if (lastImgTop + moveY <= 0 - viewLP.height/2) {
                    return true;
                } else if (lastImgTop + viewLP.height + moveY > maxY + viewLP.height/2) {
                    return true;
                }

                viewLP.leftMargin = (int) (lastImgLeft + moveX);
                viewLP.topMargin = (int) (lastImgTop + moveY);
                view.setLayoutParams(viewLP);
                pushBtnLP.leftMargin = (int) (lastPushBtnLeft + moveX);
                pushBtnLP.topMargin = (int) (lastPushBtnTop + moveY);
                mPushView.setLayoutParams(pushBtnLP);

                deleteBtnLP.leftMargin = (int) (lastDeleteBtnLeft + moveX);
                deleteBtnLP.topMargin = (int) (lastDeleteBtnTop + moveY);
                mDeleteView.setLayoutParams(deleteBtnLP);
                break;

        }
        return false;
    }


    private Point getRawPoint(MotionEvent event) {
        return new Point((int) event.getRawX(), (int) event.getRawY());
    }
}
