package com.hubang.ninegrid.preview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.hubang.ninegrid.ImageInfo;

import java.util.ArrayList;

/**
 * 作者　　: 李坤
 * 创建时间:2016/11/4　13:37
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */

public class ImagePreview {
    private static ImagePreview imagePreview;

    private Intent intent;

    public static ImagePreview create() {
        if (imagePreview == null) {
            imagePreview = new ImagePreview();
        }
        imagePreview.intent = new Intent();
        return imagePreview;
    }

    public ImagePreview images(ArrayList<ImageInfo> imageInfo) {
        intent.putParcelableArrayListExtra(ImagePreviewActivity.IMAGE_INFO, imageInfo);
        return this;
    }

    public ImagePreview selectPostion(int IMAGE_SELECT) {
        intent.putExtra(ImagePreviewActivity.IMAGE_SELECT, IMAGE_SELECT);
        return this;
    }

    public void start(Context context) {
        intent = createIntent(context);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(0, 0);
        }
        intent = null;
    }

    private Intent createIntent(Context context) {
        intent.setClass(context, ImagePreviewActivity.class);
        return intent;
    }
}
