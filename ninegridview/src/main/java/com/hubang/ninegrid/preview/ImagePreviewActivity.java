package com.hubang.ninegrid.preview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hubang.ninegrid.ImageInfo;
import com.hubang.ninegrid.R;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ImagePreviewActivity extends Activity implements ViewTreeObserver.OnPreDrawListener {

    public static final String IMAGE_INFO = "IMAGE_INFO";
    public static final String IMAGE_SELECT = "IMAGE_SELECT";
    public static final int ANIMATE_DURATION = 250;
    TextView numberTv;
    private RelativeLayout rootView;

    private ImagePreviewAdapter imagePreviewAdapter;
    private ArrayList<ImageInfo> imageInfos;
    private int currentItem;
    private int imageHeight;
    private int imageWidth;
    private int screenWidth;
    private int screenHeight;
    private float animAlpha = 0.4f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        full(true);
        setContentView(R.layout.activity_preview);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        numberTv = (TextView) findViewById(R.id.numberTv);
        rootView = (RelativeLayout) findViewById(R.id.rootView);


        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;
        screenHeight = metric.heightPixels;

        Intent intent = getIntent();
        currentItem = intent.getIntExtra(IMAGE_SELECT, 0);
        imageInfos = intent.getParcelableArrayListExtra(IMAGE_INFO);
        if (currentItem >= imageInfos.size()) {
            currentItem = imageInfos.size() - 1;
        }


        imagePreviewAdapter = new ImagePreviewAdapter(this, imageInfos, currentItem);
        viewPager.setAdapter(imagePreviewAdapter);
        viewPager.setCurrentItem(currentItem);
        viewPager.getViewTreeObserver().addOnPreDrawListener(this);
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                if (numberTv != null && imagePreviewAdapter != null) {
                    if (imagePreviewAdapter.getCount() > 0) {
                        numberTv.setText(String.format("%2s / %2s", currentItem + 1, imageInfos.size()));
                        numberTv.setVisibility(VISIBLE);
                        runnable.setGone(false);
                        numberTv.postDelayed(runnable = new NumberRunnable(), 3000);
                    }
                }
            }
        });
    }

    NumberRunnable runnable = new NumberRunnable();

    private class NumberRunnable implements Runnable {
        boolean isGone = true;

        public void setGone(boolean gone) {
            isGone = gone;
        }

        @Override
        public void run() {
            if (numberTv != null && isGone) {
                numberTv.setVisibility(GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finishActivityAnim();
    }

    /**
     * 绘制前开始动画
     */
    @Override
    public boolean onPreDraw() {
        rootView.getViewTreeObserver().removeOnPreDrawListener(this);
        final View view = imagePreviewAdapter.getPrimaryItem();

        if (view == null) {
            return false;
        }
        final ImageView imageView = imagePreviewAdapter.getPrimaryImageView();
        if (imageView == null) {
            return false;
        }

        computeImageWidthAndHeight(imageView);

        final ImageInfo imageInfo = imageInfos.get(currentItem);

        final float vx = imageInfo.imageViewWidth * 1.0f / imageWidth;
        final float vy = imageInfo.imageViewHeight * 1.0f / imageHeight;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long duration = animation.getDuration();
                long playTime = animation.getCurrentPlayTime();
                float fraction = duration > 0 ? (float) playTime / duration : 1f;
                if (fraction > 1) fraction = 1;
                view.setTranslationX(evaluateInt(fraction, imageInfo.imageViewX + imageInfo.imageViewWidth / 2 - imageView.getWidth() / 2, 0));
                view.setTranslationY(evaluateInt(fraction, imageInfo.imageViewY + imageInfo.imageViewHeight / 2 - imageView.getHeight() / 2, 0));


                view.setScaleX(evaluateFloat(fraction, vx, 1));
                view.setScaleY(evaluateFloat(fraction, vy, 1));
                view.setAlpha(animAlpha + (1 - animAlpha) * fraction);
                rootView.setBackgroundColor(evaluateArgb(fraction, Color.argb((int) (animAlpha * 0xff), 0, 0, 0), Color.BLACK));
            }
        });
        addIntoListener(valueAnimator);
        valueAnimator.setDuration(ANIMATE_DURATION);
        valueAnimator.start();
        return true;
    }

    /**
     * activity的退场动画
     */
    public void finishActivityAnim() {
        final View view = imagePreviewAdapter.getPrimaryItem();
        if (view == null) {
            finish();
            return;
        }
        final ImageView imageView = imagePreviewAdapter.getPrimaryImageView();
        if (imageView == null) {
            finish();
            return;
        }
        full(false);
        computeImageWidthAndHeight(imageView);
        final ImageInfo imageInfo = imagePreviewAdapter.getCurrentImageInfo();
        if (imageInfo == null) {
            finish();
            return;
        }


        final float vx = imageInfo.imageViewWidth * 1.0f / imageWidth;
        final float vy = imageInfo.imageViewHeight * 1.0f / imageHeight;
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long duration = animation.getDuration();
                long playTime = animation.getCurrentPlayTime();
                float fraction = duration > 0 ? (float) playTime / duration : 1f;
                if (fraction > 1) fraction = 1;
                view.setTranslationX(evaluateInt(fraction, 0, imageInfo.imageViewX + imageInfo.imageViewWidth / 2 - imageView.getWidth() / 2));
                view.setTranslationY(evaluateInt(fraction, 0, imageInfo.imageViewY + imageInfo.imageViewHeight / 2 - imageView.getHeight() / 2));
                view.setScaleX(evaluateFloat(fraction, 1, vx));
                view.setScaleY(evaluateFloat(fraction, 1, vy));
                view.setAlpha(animAlpha + (1 - animAlpha) * (1 - fraction));
                rootView.setBackgroundColor(evaluateArgb(fraction, Color.BLACK, Color.argb((int) (animAlpha * 0xff), 0, 0, 0)));
            }
        });
        addOutListener(valueAnimator);
        valueAnimator.setDuration(ANIMATE_DURATION);
        valueAnimator.start();
    }

    /**
     * 计算图片的宽高
     */
    private void computeImageWidthAndHeight(ImageView imageView) {

        // 获取真实大小
        Drawable drawable = imageView.getDrawable();
        if (drawable == null) return;

        int intrinsicHeight = drawable.getIntrinsicHeight();
        int intrinsicWidth = drawable.getIntrinsicWidth();
        // 计算出与屏幕的比例，用于比较以宽的比例为准还是高的比例为准，因为很多时候不是高度没充满，就是宽度没充满
        if (intrinsicHeight <= 0) {
            intrinsicHeight = imageView.getHeight();
        }
        if (intrinsicWidth <= 0) {
            intrinsicWidth = imageView.getWidth();
        }

        float h = screenHeight * 1.0f / intrinsicHeight;
        float w = screenWidth * 1.0f / intrinsicWidth;
        if (h > w) h = w;
        else w = h;

        // 得出当宽高至少有一个充满的时候图片对应的宽高
        imageHeight = (int) (intrinsicHeight * h);
        imageWidth = (int) (intrinsicWidth * w);
    }

    /**
     * 进场动画过程监听
     */
    private void addIntoListener(ValueAnimator valueAnimator) {
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                rootView.setBackgroundColor((int) (animAlpha * 0xff));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    /**
     * 退场动画过程监听
     */
    private void addOutListener(ValueAnimator valueAnimator) {
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                rootView.setBackgroundColor(0x0);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    /**
     * Integer 估值器
     */
    public Integer evaluateInt(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int) (startInt + fraction * (endValue - startInt));
    }

    /**
     * Float 估值器
     */
    public Float evaluateFloat(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }

    /**
     * Argb 估值器
     */
    public int evaluateArgb(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;

        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;

        return (startA + (int) (fraction * (endA - startA))) << 24//
                | (startR + (int) (fraction * (endR - startR))) << 16//
                | (startG + (int) (fraction * (endG - startG))) << 8//
                | (startB + (int) (fraction * (endB - startB)));
    }

    //全屏
    private void full(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

}
