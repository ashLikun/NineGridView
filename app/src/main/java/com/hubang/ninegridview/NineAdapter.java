package com.hubang.ninegridview;

import android.content.Context;

import com.hubang.ninegrid.ImageInfo;
import com.hubang.ninegrid.NineGridView;
import com.hubang.ninegrid.NineGridViewAdapter;
import com.hubang.ninegrid.preview.ImagePreview;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者　　: 李坤
 * 创建时间:2016/11/4　14:14
 * 邮箱　　：496546144@qq.com
 * <p>
 * 功能介绍：
 */

public class NineAdapter extends NineGridViewAdapter<String> {
    ArrayList<String> maxDatas;

    public NineAdapter(Context context, List<String> datas, ArrayList<String> maxDatas) {
        super(context, datas);
        this.maxDatas = maxDatas;
    }

    @Override
    protected void onImageItemClick(Context context, NineGridView nineGridView, int index, ArrayList<String> imageDatas) {
        ArrayList<ImageInfo> imageInfos = getImageInfos(nineGridView, index, maxDatas);
        ImagePreview.create().images(imageInfos).selectPostion(index).start(context);
    }
}
