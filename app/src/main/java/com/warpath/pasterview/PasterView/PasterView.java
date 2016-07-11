package com.warpath.pasterview.PasterView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.warpath.pasterview.R;


public class PasterView extends LinearLayout {
    public ImageView mView;
    private ImageView mPushView;
    private ImageView mDeleteView;
    private float _1dp;
    private boolean mCenterInParent;
    private Drawable mImageDrawable, mPushImageDrawable, mDeleteImageDrawable;
    private float mImageHeight, mImageWidth, mPushImageHeight, mPushImageWidth, mDeleteImageHeight, mDeleteImageWidth;
    public int mLeft = 0, mTop = 0;
    private boolean mIconVisibility = true;
    /**
     * callback interface to be invoked when the delete icon has clicked
     */
    private OnPasterDeleteIconClickListener mOnPasterDeleteIconClickListener;

    /**
     * callback interface to be invoked when the image view has clicked
     */
    private OnImageViewClickListener mOnImageViewClickListener;


    public PasterView(Context context) {
        this(context, null, 0);
    }

    public PasterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this._1dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());
        this.parseAttr(context, attrs);
        View mRoot = View.inflate(context, R.layout.test_image_view, null);
        addView(mRoot, -1, -1);
        mPushView = (ImageView) mRoot.findViewById(R.id.push_view);
        mView = (ImageView) mRoot.findViewById(R.id.view);
        mView.setTag(R.id.single_finger_view_scale, (float)1.0);
        mDeleteView = (ImageView) mRoot.findViewById(R.id.delete_view);
        mPushView.setOnTouchListener(new PushBtnTouchListener(mView, mDeleteView));

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mView.setOnTouchListener(new ViewOnTouchListener(mPushView, mDeleteView, wm.getDefaultDisplay().getWidth(), wm.getDefaultDisplay().getHeight()));

        mDeleteView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePasterImage();
            }
        });
    }

    public void showIconAndBorder() {
        if (null != mDeleteView) {
            mDeleteView.setVisibility(View.VISIBLE);
        }
        if (null != mPushView) {
            mPushView.setVisibility(View.VISIBLE);
        }
        if (null != mView) {
            mView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.paster_image_border));
        }
        mIconVisibility = true;
    }

    private void parseAttr(Context context, AttributeSet attrs) {
        if (null == attrs) return;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PasterView);
        if (a != null) {
            int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.PasterView_centerInParent) {
                    this.mCenterInParent = a.getBoolean(attr, false);
                } else if (attr == R.styleable.PasterView_image) {
                    this.mImageDrawable = a.getDrawable(attr);
                } else if (attr == R.styleable.PasterView_image_height) {
                    this.mImageHeight = a.getDimension(attr, 200 * _1dp);
                } else if (attr == R.styleable.PasterView_image_width) {
                    this.mImageWidth = a.getDimension(attr, 200 * _1dp);
                } else if (attr == R.styleable.PasterView_push_image) {
                    this.mPushImageDrawable = a.getDrawable(attr);
                } else if (attr == R.styleable.PasterView_push_image_width) {
                    this.mPushImageWidth = a.getDimension(attr, 50 * _1dp);
                } else if (attr == R.styleable.PasterView_push_image_height) {
                    this.mPushImageHeight = a.getDimension(attr, 50 * _1dp);
                } else if (attr == R.styleable.PasterView_left) {
                    this.mLeft = (int) a.getDimension(attr, 0 * _1dp);
                } else if (attr == R.styleable.PasterView_top) {
                    this.mTop = (int) a.getDimension(attr, 0 * _1dp);
                } else if (attr == R.styleable.PasterView_delete_image) {
                    this.mDeleteImageDrawable = a.getDrawable(attr);
                } else if (attr == R.styleable.PasterView_delete_image_width) {
                    this.mDeleteImageWidth = a.getDimension(attr, 50 * _1dp);
                } else if (attr == R.styleable.PasterView_delete_image_height) {
                    this.mDeleteImageHeight = a.getDimension(attr, 50 * _1dp);
                }
            }
        }
    }

    private void setViewToAttr(int pWidth, int pHeight) {
        if (null != mImageDrawable) {
            this.mView.setBackgroundDrawable(mImageDrawable);
        }
        if (null != mPushImageDrawable) {
            this.mPushView.setBackgroundDrawable(mPushImageDrawable);
        }
        if (null != mDeleteImageDrawable) {
            this.mDeleteView.setBackgroundDrawable(mDeleteImageDrawable);
        }
        FrameLayout.LayoutParams viewLP = (FrameLayout.LayoutParams) this.mView.getLayoutParams();
        viewLP.width = (int) mImageWidth;
        viewLP.height = (int) mImageHeight;
        int left = 0, top = 0;
        if (mCenterInParent) {
            left = pWidth / 2 - viewLP.width / 2;
            top = pHeight / 2 - viewLP.height / 2;
        } else {
            if (mLeft > 0) left = mLeft;
            if (mTop > 0) top = mTop;
        }
        viewLP.leftMargin = left;
        viewLP.topMargin = top;
        this.mView.setLayoutParams(viewLP);

        FrameLayout.LayoutParams pushViewLP = (FrameLayout.LayoutParams) mPushView.getLayoutParams();
        pushViewLP.width = (int) mPushImageWidth;
        pushViewLP.height = (int) mPushImageHeight;
        pushViewLP.leftMargin = (int) (viewLP.leftMargin + mImageWidth - mPushImageWidth / 2);
        pushViewLP.topMargin = (int) (viewLP.topMargin + mImageHeight - mPushImageHeight / 2);
        mPushView.setLayoutParams(pushViewLP);

        FrameLayout.LayoutParams deleteViewLP = (FrameLayout.LayoutParams) mDeleteView.getLayoutParams();
        deleteViewLP.width = (int) mDeleteImageWidth;
        deleteViewLP.height = (int) mDeleteImageHeight;
        deleteViewLP.leftMargin = (int) (viewLP.leftMargin - mDeleteImageWidth / 2);
        deleteViewLP.topMargin = (int) (viewLP.topMargin - mDeleteImageHeight / 2);
        mDeleteView.setLayoutParams(deleteViewLP);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setParamsForView(widthMeasureSpec, heightMeasureSpec);
    }

    private boolean hasSetParamsForView = false;

    private void setParamsForView(int widthMeasureSpec, int heightMeasureSpec) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (null != layoutParams && !hasSetParamsForView) {
            hasSetParamsForView = true;
            int width;
            if ((getLayoutParams().width == LayoutParams.MATCH_PARENT)) {
                width = MeasureSpec.getSize(widthMeasureSpec);
            } else {
                width = getLayoutParams().width;
            }
            int height;
            if ((getLayoutParams().height == LayoutParams.MATCH_PARENT)) {
                height = MeasureSpec.getSize(heightMeasureSpec);
            } else {
                height = getLayoutParams().height;
            }
            setViewToAttr(width, height);
            Log.d("tag", "width :" + width + " height :" + height);
        }
    }

    /**
     * Interface definition for a callback to be invoked when the delete icon has clicked.
     */
    public interface OnPasterDeleteIconClickListener {
        /***
         * callback method to be invoked when the delete icon has clicked
         */
        public void onDeleteIconClick(PasterView singleFingerView);
    }

    /**
     * Interface definition for a callback to be invoked when the image view has clicked
     */
    public interface OnImageViewClickListener {
        /**
         * callback method to be invoked when the image view has clicked
         */
        public void onImageViewClick(PasterView singleFingerView);
    }

    /**
     * call to delete the icon
     */
    public void deletePasterImage() {
        //if the delete icon clicked then notify listener if there is one
        if (mOnPasterDeleteIconClickListener != null) {
            mOnPasterDeleteIconClickListener.onDeleteIconClick(this);
        }
    }

    /**
     * call to change the index of imageview
     */
    public void changeViewIndexToTop() {
        //if the view clicked then notify listener if there is one
        if (mOnImageViewClickListener != null) {
            mOnImageViewClickListener.onImageViewClick(this);
        }
    }

}
