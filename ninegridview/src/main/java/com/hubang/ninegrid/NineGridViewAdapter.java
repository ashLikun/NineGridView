package com.hubang.ninegrid;

import android.content.Context;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public abstract class NineGridViewAdapter<T> {

    protected Context context;
    private List<T> datas;
    private int statusHeight;

    public NineGridViewAdapter(Context context, List<T> datas) {
        this.context = context;
        this.datas = datas;
        statusHeight = getStatusHeight(context);
    }

    /**
     * 如果要实现图片点击的逻辑，重写此方法即可
     *
     * @param context      上下文
     * @param nineGridView 九宫格控件
     * @param index        当前点击图片的的索引
     * @param imageDatas   图片地址的数据集合
     */
    protected void onImageItemClick(Context context, NineGridView nineGridView, int index, ArrayList<T> imageDatas) {

    }

    /**
     * 生成ImageView容器的方式，默认使用NineGridImageViewWrapper类，即点击图片后，图片会有蒙板效果
     * 如果需要自定义图片展示效果，重写此方法即可
     *
     * @param context 上下文
     * @return 生成的 ImageView
     */
    protected ImageView generateImageView(Context context) {
        NineGridViewWrapper imageView = new NineGridViewWrapper(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.drawable.ic_default_color);
        return imageView;
    }

    public ArrayList<ImageInfo> getImageInfos(NineGridView nineGridView, int index, ArrayList<String> maxDatas) {
        ArrayList<ImageInfo> list = new ArrayList<>();
        for (int i = 0; i < nineGridView.getChildCount(); i++) {
            ImageInfo info = new ImageInfo();
            ImageView imageView;
            if (index < nineGridView.getChildCount()) {
                imageView = (ImageView) nineGridView.getChildAt(i);
            } else {
                //如果图片的数量大于显示的数量，则超过部分的返回动画统一退回到最后一个图片的位置
                imageView = (ImageView) nineGridView.getChildAt(nineGridView.getMaxSize() - 1);
            }
            ImageInfo.setThumbnailDrawable(imageView.getDrawable());
            info.imageViewWidth = imageView.getWidth();
            info.imageViewHeight = imageView.getHeight();
            int[] points = new int[2];
            imageView.getLocationInWindow(points);
            info.imageViewX = points[0];
            info.imageViewY = points[1] - statusHeight;
            info.setBigImageUrl(maxDatas.get(i));
            list.add(info);
        }


        return list;
    }

    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    /**
     * 获得状态栏的高度
     */
    public int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    public void setStatusHeight(int statusHeight) {
        this.statusHeight = statusHeight;
    }
}