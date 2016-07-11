package com.warpath.pasterview.PasterView;

import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.warpath.pasterview.R;


class PushBtnTouchListener implements View.OnTouchListener {
    Point pushPoint;
    int lastImgWidth;
    int lastImgHeight;
    int lastImgLeft;
    int lastImgTop;
    int lastImgAngle;
    double lastComAngle;

    int pushImgWidth;
    int pushImgHeight;
    int deleteImageWidth;
    int deleteImageHeight;

    int lastPushBtnLeft;
    int lastPushBtnTop;
    int lastDeleteBtnLeft;
    int lastDeleteBtnTop;

    private View mView;
    private View mDeleteView;
    private Point mViewCenter;
    private static final double PI = 3.14159265359;

    public PushBtnTouchListener(View mView, View mDeleteView) {
        this.mView = mView;
        this.mDeleteView = mDeleteView;
    }

    private FrameLayout.LayoutParams pushBtnLP;
    private FrameLayout.LayoutParams imgLP;
    private FrameLayout.LayoutParams deleteBtnLP;
    float lastX = -1;
    float lastY = -1;

    @Override
    public boolean onTouch(View pushView, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // 主点按下
            case MotionEvent.ACTION_DOWN:
                pushBtnLP = (FrameLayout.LayoutParams) pushView.getLayoutParams();
                imgLP = (FrameLayout.LayoutParams) mView.getLayoutParams();
                deleteBtnLP = (FrameLayout.LayoutParams) mDeleteView.getLayoutParams();

                pushPoint = getPushPoint(pushBtnLP, event);
                lastImgWidth = imgLP.width;
                lastImgHeight = imgLP.height;
                lastImgLeft = imgLP.leftMargin;
                lastImgTop = imgLP.topMargin;
                lastImgAngle = (int) mView.getRotation();

                lastPushBtnLeft = pushBtnLP.leftMargin;
                lastPushBtnTop = pushBtnLP.topMargin;
                lastDeleteBtnLeft = deleteBtnLP.leftMargin;
                lastDeleteBtnTop = deleteBtnLP.topMargin;
                deleteImageWidth = deleteBtnLP.width;
                deleteImageHeight = deleteBtnLP.height;

                pushImgWidth = pushBtnLP.width;
                pushImgHeight = pushBtnLP.height;

                lastX = event.getRawX();
                lastY = event.getRawY();
                refreshImageCenter();
                break;
            // 副点按下
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_UP: {
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                float rawX = event.getRawX();
                float rawY = event.getRawY();
                if (lastX != -1) {
                    if (Math.abs(rawX - lastX) < 5 && Math.abs(rawY - lastY) < 5) {
                        return false;
                    }
                }
                lastX = rawX;
                lastY = rawY;

                Point O = mViewCenter, A = pushPoint, B = getPushPoint(pushBtnLP, event);
                float dOA = getDistance(O, A);
                float dOB = getDistance(O, B);
                float f = dOB / dOA;

                int newWidth = (int) (lastImgWidth * f);
                int newHeight = (int) (lastImgHeight * f);
                mView.setTag(R.id.single_finger_view_scale, f);

                imgLP.leftMargin = lastImgLeft - ((newWidth - lastImgWidth) / 2);
                imgLP.topMargin = lastImgTop - ((newHeight - lastImgHeight) / 2);
                imgLP.width = newWidth;
                imgLP.height = newHeight;
                mView.setLayoutParams(imgLP);

                float fz = (((A.x - O.x) * (B.x - O.x)) + ((A.y - O.y) * (B.y - O.y)));
                float fm = dOA * dOB;
                double comAngle = (180 * Math.acos(fz / fm) / PI);
                if (Double.isNaN(comAngle)) {
                    comAngle = (lastComAngle < 90 || lastComAngle > 270) ? 0 : 180;
                } else if ((B.y - O.y) * (A.x - O.x) < (A.y - O.y) * (B.x - O.x)) {
                    comAngle = 360 - comAngle;
                }
                lastComAngle = comAngle;

                float angle = (float) (lastImgAngle + comAngle);
                angle = angle % 360;
                mView.setRotation(angle);
                Point imageRB = new Point(mView.getLeft() + mView.getWidth(), mView.getTop() + mView.getHeight());
                Point anglePoint = getAnglePoint(O, imageRB, angle);
                Point deleteAnglePoint = getAnglePoint1(O , imageRB, angle);

                pushBtnLP.leftMargin = (int) (anglePoint.x - pushImgWidth / 2);
                pushBtnLP.topMargin = (int) (anglePoint.y - pushImgHeight / 2);
                pushView.setLayoutParams(pushBtnLP);
                deleteBtnLP.leftMargin = (int) (deleteAnglePoint.x - deleteImageWidth / 2);
                deleteBtnLP.topMargin = (int) (deleteAnglePoint.y  - deleteImageHeight / 2);
                mDeleteView.setLayoutParams(deleteBtnLP);
                break;
        }
        return false;
    }

    private void refreshImageCenter() {
        int x = mView.getLeft() + mView.getWidth() / 2;
        int y = mView.getTop() + mView.getHeight() / 2;
        mViewCenter = new Point(x, y);
    }


    private Point getPushPoint(FrameLayout.LayoutParams lp, MotionEvent event) {
        return new Point(lp.leftMargin + (int) event.getX(), lp.topMargin + (int) event.getY());
    }

    private float getDistance(Point a, Point b) {
        float v = ((a.x - b.x) * (a.x - b.x)) + ((a.y - b.y) * (a.y - b.y));
        return ((int) (Math.sqrt(v) * 100)) / 100f;
    }

    private Point getAnglePoint(Point O, Point A, float angle) {
        int x, y;
        float dOA = getDistance(O, A);
        double p1 = angle * PI / 180f;
        double p2 = Math.acos((A.x - O.x) / dOA);
        x = (int) (O.x + dOA * Math.cos(p1 + p2));

        double p3 = Math.acos((A.x - O.x) / dOA);
        y = (int) (O.y + dOA * Math.sin(p1 + p3));
        return new Point(x, y);
    }

    private Point getAnglePoint1(Point O, Point A, float angle) {
        int x, y;
        float dOA = getDistance(O, A);
        double p1 = angle * PI / 180f;
        double p2 = Math.acos((A.x - O.x) / dOA);
        x = (int) (O.x - dOA * Math.cos(p1 + p2));

        double p3 = Math.acos((A.x - O.x) / dOA);
        y = (int) (O.y - dOA * Math.sin(p1 + p3));
        return new Point(x, y);
    }

}
