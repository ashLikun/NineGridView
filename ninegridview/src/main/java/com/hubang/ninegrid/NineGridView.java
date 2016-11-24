package com.hubang.ninegrid;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class NineGridView<T> extends ViewGroup {

    public static final int MODE_FILL = 0;          //填充模式，类似于微信
    public static final int MODE_GRID = 1;          //网格模式，类似于QQ，4张图会 2X2布局

    private static ImageLoader mImageLoader;        //全局的图片加载器(必须设置,否者不显示图片)

    private int singleImageSize = 250;              // 单张图片时的最大大小,单位dp
    private float singleImageRatio = 1.0f;          // 单张图片的宽高比(宽/高)
    private int maxImageSize = 9;                   // 最大显示的图片数
    private int gridSpacing = 3;                    // 宫格间距，单位dp
    private int mode = MODE_GRID;                   // 默认使用MODE_GRID模式
    private boolean showMoreNumber = false;         // 显示更多数字
    private ImageView.ScaleType singleScaleType = ImageView.ScaleType.FIT_START;         // 单张图的裁剪类型

    private int columnCount;    // 列数
    private int rowCount;       // 行数
    private int gridWidth;      // 宫格宽度
    private int gridHeight;     // 宫格高度
    private int fourTotalWidth;     // 2-4张时候容器大小

    private List<ImageView> imageViews;
    private List<T> mImgDatas;
    private NineGridViewAdapter mAdapter;

    public NineGridView(Context context) {
        this(context, null);
    }

    public NineGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NineGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        gridSpacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, gridSpacing, dm);
        singleImageSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, singleImageSize, dm);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NineGridView);
        gridSpacing = (int) a.getDimension(R.styleable.NineGridView_ngv_gridSpacing, gridSpacing);
        singleImageSize = a.getDimensionPixelSize(R.styleable.NineGridView_ngv_singleImageSize, singleImageSize);
        singleImageRatio = a.getFloat(R.styleable.NineGridView_ngv_singleImageRatio, singleImageRatio);
        maxImageSize = a.getInt(R.styleable.NineGridView_ngv_maxSize, maxImageSize);
        showMoreNumber = a.getBoolean(R.styleable.NineGridView_ngv_showMoreNumber, showMoreNumber);
        int singleScaleTypeInt = a.getInt(R.styleable.NineGridView_ngv_singleScaleType, 5);
        if (singleScaleTypeInt == 1) {
            singleScaleType = ImageView.ScaleType.CENTER_CROP;
        } else if (singleScaleTypeInt == 5) {
            singleScaleType = ImageView.ScaleType.FIT_START;
        } else if (singleScaleTypeInt == 3) {
            singleScaleType = ImageView.ScaleType.FIT_CENTER;
        }
        mode = a.getInt(R.styleable.NineGridView_ngv_mode, mode);
        a.recycle();

        imageViews = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;
        int totalWidth = width - getPaddingLeft() - getPaddingRight();

        int childCount = getChildCount();
        if (childCount > 0) {
            if (childCount == 1) {
                gridWidth = singleImageSize > totalWidth ? totalWidth : singleImageSize;
                gridHeight = (int) (gridWidth / singleImageRatio);
                //矫正图片显示区域大小，不允许超过最大显示范围
                if (gridHeight > singleImageSize) {
                    float ratio = singleImageSize * 1.0f / gridHeight;
                    gridWidth = (int) (gridWidth * ratio);
                    gridHeight = singleImageSize;
                }
                measureChild(getChildAt(0), MeasureSpec.makeMeasureSpec(gridWidth, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(gridHeight, MeasureSpec.EXACTLY));
            } else {
                if (childCount <= 4 && mode == MODE_GRID && maxImageSize > 4) {
                    int scale = (int) (totalWidth * 0.8f);//如果小于2-4张  那么容器的大小缩小
                    if (fourTotalWidth < scale) {
                        fourTotalWidth = scale;
                    }
                    totalWidth = fourTotalWidth;
                }
                //多于1张后计算宽高
                gridWidth = gridHeight = (totalWidth - gridSpacing * (columnCount - 1)) / columnCount;
                if (maxImageSize <= 4) {//最大显示小于4张 正方形
                    gridHeight = (totalWidth - gridSpacing * (rowCount - 1)) / rowCount;
                }
                for (int i = 0; i < childCount; i++) {
                    View childView = getChildAt(i);
                    measureChild(childView, MeasureSpec.makeMeasureSpec(gridWidth, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(gridHeight, MeasureSpec.EXACTLY));
                }
            }
            width = gridWidth * columnCount + gridSpacing * (columnCount - 1) + getPaddingLeft() + getPaddingRight();
            height = gridHeight * rowCount + gridSpacing * (rowCount - 1) + getPaddingTop() + getPaddingBottom();
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mImgDatas == null) return;
        int childrenCount = mImgDatas.size();
        for (int i = 0; i < childrenCount; i++) {
            ImageView childrenView = (ImageView) getChildAt(i);
            if (mImageLoader != null) {
                mImageLoader.onDisplayImage(getContext(), childrenView, mImgDatas.get(i), childrenCount == 1);
            }
            int rowNum = i / columnCount;
            int columnNum = i % columnCount;
            int left = (gridWidth + gridSpacing) * columnNum + getPaddingLeft();
            int top = (gridHeight + gridSpacing) * rowNum + getPaddingTop();
            int right;
            if (maxImageSize <= 4 && childrenCount == 3 && i == 2) {
                right = left + gridWidth * 2 + gridSpacing;
            } else {
                right = left + gridWidth;
            }
            int bottom = top + gridHeight;
            childrenView.layout(left, top, right, bottom);
        }
    }

    /**
     * 设置适配器
     */
    public void setAdapter(@NonNull NineGridViewAdapter adapter) {
        mAdapter = adapter;
        List<T> imgDatas = adapter.getDatas();

        if (imgDatas == null || imgDatas.isEmpty()) {
            setVisibility(GONE);
            return;
        } else {
            setVisibility(VISIBLE);
        }

        int imageCount = imgDatas.size();
        if (maxImageSize > 0 && imageCount > maxImageSize) {
            imgDatas = imgDatas.subList(0, maxImageSize);
            imageCount = imgDatas.size();   //再次获取图片数量
        }

        //默认是3列显示，行数根据图片的数量决定
        rowCount = imageCount / 3 + (imageCount % 3 == 0 ? 0 : 1);
        columnCount = imageCount >= 3 ? 3 : imageCount % 3;
        //grid模式下，显示4张使用2X2模式
        if (mode == MODE_GRID) {
            if (imageCount >= 1 && imageCount <= 4) {
                rowCount = imageCount >= 3 ? 2 : 1;
                columnCount = imageCount > 1 ? 2 : 1;
            }
        }
        //如果max为4 就2列  2行显示
        if (maxImageSize <= 4) {
            rowCount = imageCount >= maxImageSize ? 2 : (imageCount > 2 ? 2 : 1);
            columnCount = 2;
        }
        //如果是单张  就一行一列
        if (imageCount == 1) {
            rowCount = 1;
            columnCount = 1;
        }

        //保证View的复用，避免重复创建
        if (mImgDatas == null) {
            for (int i = 0; i < imageCount; i++) {
                ImageView iv = getImageView(i);
                if (iv == null) return;
                addView(iv, generateDefaultLayoutParams());
            }
        } else {
            int oldViewCount = mImgDatas.size();
            int newViewCount = imageCount;
            if (oldViewCount > newViewCount) {
                removeViews(newViewCount, oldViewCount - newViewCount);
            } else if (oldViewCount < newViewCount) {
                for (int i = oldViewCount; i < newViewCount; i++) {
                    ImageView iv = getImageView(i);
                    if (iv == null) return;
                    addView(iv, generateDefaultLayoutParams());
                }
            }
        }
        //修改最后一个条目，决定是否显示更多
        if (showMoreNumber && adapter.getDatas().size() > maxImageSize) {
            View child = getChildAt(maxImageSize - 1);
            if (child instanceof NineGridViewWrapper) {
                NineGridViewWrapper imageView = (NineGridViewWrapper) child;
                imageView.setMoreNum(adapter.getDatas().size() - maxImageSize);
            }
        }

        if (getChildCount() == 1) {
            ImageView child = (ImageView) getChildAt(0);
            child.setAdjustViewBounds(true);
            child.setScaleType(singleScaleType);

        }
        mImgDatas = imgDatas;
        requestLayout();
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        LayoutParams params = super.generateDefaultLayoutParams();
        params.width = gridWidth;
        params.height = gridHeight;
        return params;
    }

    /**
     * 获得 ImageView 保证了 ImageView 的重用
     */
    private ImageView getImageView(final int position) {
        ImageView imageView;
        if (position < imageViews.size()) {
            imageView = imageViews.get(position);
        } else {
            imageView = mAdapter.generateImageView(getContext());
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.onImageItemClick(getContext(), NineGridView.this, position, (ArrayList) mAdapter.getDatas());
                }
            });
            imageViews.add(imageView);
        }
        imageView.setAdjustViewBounds(false);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    /**
     * 设置宫格间距
     */
    public void setGridSpacing(int spacing) {
        gridSpacing = spacing;
    }

    /**
     * 设置只有一张图片时的宽
     */
    public void setSingleImageSize(int maxImageSize) {
        singleImageSize = maxImageSize;
    }

    /**
     * 设置只有一张图片时的宽高比
     */
    public void setSingleImageRatio(float ratio) {
        singleImageRatio = ratio;
    }

    /**
     * 设置最大图片数
     */
    public void setMaxSize(int maxSize) {
        maxImageSize = maxSize;
    }

    public int getMaxSize() {
        return maxImageSize;
    }

    public static void setImageLoader(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    public static ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public NineGridViewAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 这个方法是刷新重用adapter的
     *
     * @param datas
     */
    public void setData(List<T> datas) {
        mAdapter.setDatas(datas);
        setAdapter(mAdapter);
        return;
    }

    public interface ImageLoader<T> {
        /**
         * 需要子类实现该方法，以确定如何加载和显示图片
         *
         * @param context   上下文
         * @param imageView 需要展示图片的ImageView
         * @param urlData   图片地址泛型
         */
        void onDisplayImage(Context context, ImageView imageView, T urlData, boolean isSingle);

    }
}