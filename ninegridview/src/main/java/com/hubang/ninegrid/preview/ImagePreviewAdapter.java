package com.hubang.ninegrid.preview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bm.library.PhotoView;
import com.hubang.ninegrid.ImageInfo;
import com.hubang.ninegrid.NineGridView;
import com.hubang.ninegrid.R;

import java.util.List;


/**
 * 作者　　: 李坤
 * 创建时间: 14:14 Administrator
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */

public class ImagePreviewAdapter extends PagerAdapter {

    private List<ImageInfo> imageInfos;
    private Context context;
    private View currentView;
    private boolean isshowExcessPicOk = false;
    private int selectPosition;

    private ImageInfo currentImageInfo;

    public ImageInfo getCurrentImageInfo() {
        return currentImageInfo;
    }

    public ImagePreviewAdapter(Context context, List<ImageInfo> imageInfo, int selectPosition) {
        super();
        this.context = context;
        this.imageInfos = imageInfo;
        this.selectPosition = selectPosition;
    }

    @Override
    public int getCount() {
        return imageInfos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        currentView = (View) object;
        currentImageInfo = imageInfos.get(position);
    }

    public View getPrimaryItem() {
        return currentView;
    }

    public ImageView getPrimaryImageView() {
        return (ImageView) currentView.findViewById(R.id.pv);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photoview, container, false);
        final ProgressBar pb = (ProgressBar) view.findViewById(R.id.pb);
        final PhotoView imageView = (PhotoView) view.findViewById(R.id.pv);
        imageView.enable();
        if (!isshowExcessPicOk && selectPosition == position) {
            showExcessPic(imageView);
        }

        //如果需要加载的loading,需要自己改写,不能使用这个方法
        NineGridView.getImageLoader().onDisplayImage(view.getContext(), imageView, imageInfos.get(position).getBigImageUrl(), false);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPhotoTap();
            }
        });

        container.addView(view);
        return view;
    }

    /**
     * 展示过度图片
     */
    private void showExcessPic(PhotoView imageView) {
        //先获取大图的缓存图片
        Drawable cacheImage = ImageInfo.getThumbnailDrawable();
        ImageInfo.setThumbnailDrawable(null);
        //如果没有任何缓存,使用默认图片,否者使用缓存
        if (cacheImage == null) {
            imageView.setImageResource(R.drawable.ic_default_color);
        } else {
            imageView.setImageDrawable(cacheImage);
        }
        isshowExcessPicOk = false;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /**
     * 单击屏幕关闭
     */
    public void onPhotoTap() {
        ((ImagePreviewActivity) context).finishActivityAnim();
    }
}