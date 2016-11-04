package com.hubang.ninegrid;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**

 */
public class ImageInfo implements Parcelable {
    public String bigImageUrl;
    public int imageViewHeight;
    public int imageViewWidth;
    public int imageViewX;
    public int imageViewY;

    public static Drawable thumbnailDrawable;

    public static Drawable getThumbnailDrawable() {
        return thumbnailDrawable;
    }

    public static void setThumbnailDrawable(Drawable thumbnailDrawable) {
        ImageInfo.thumbnailDrawable = thumbnailDrawable;
    }

    public String getBigImageUrl() {
        return bigImageUrl;
    }

    public void setBigImageUrl(String bigImageUrl) {
        this.bigImageUrl = bigImageUrl;
    }

    public int getImageViewHeight() {
        return imageViewHeight;
    }

    public void setImageViewHeight(int imageViewHeight) {
        this.imageViewHeight = imageViewHeight;
    }

    public int getImageViewWidth() {
        return imageViewWidth;
    }

    public void setImageViewWidth(int imageViewWidth) {
        this.imageViewWidth = imageViewWidth;
    }

    public int getImageViewX() {
        return imageViewX;
    }

    public void setImageViewX(int imageViewX) {
        this.imageViewX = imageViewX;
    }

    public int getImageViewY() {
        return imageViewY;
    }

    public void setImageViewY(int imageViewY) {
        this.imageViewY = imageViewY;
    }


    @Override
    public String toString() {
        return "ImageInfo{" +
                "imageViewY=" + imageViewY +
                ", imageViewX=" + imageViewX +
                ", imageViewWidth=" + imageViewWidth +
                ", imageViewHeight=" + imageViewHeight +
                ", bigImageUrl='" + bigImageUrl + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bigImageUrl);
        dest.writeInt(this.imageViewHeight);
        dest.writeInt(this.imageViewWidth);
        dest.writeInt(this.imageViewX);
        dest.writeInt(this.imageViewY);
    }

    public ImageInfo() {
    }

    protected ImageInfo(Parcel in) {
        this.bigImageUrl = in.readString();
        this.imageViewHeight = in.readInt();
        this.imageViewWidth = in.readInt();
        this.imageViewX = in.readInt();
        this.imageViewY = in.readInt();
    }

    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(Parcel source) {
            return new ImageInfo(source);
        }

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };
}
