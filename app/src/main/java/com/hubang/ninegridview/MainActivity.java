package com.hubang.ninegridview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hubang.ninegrid.NineGridView;
import com.lzy.ninegridview.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //
    String friendPhoto = "/upload/201611/03/big/friendquan/201611031829180219.jpg,/upload/201611/03/big/friendquan/201611031829199459.jpg,/upload/201611/03/big/friendquan/201611031829212599.jpg,/upload/201611/03/big/friendquan/201611031829213319.jpg,/upload/201611/03/big/friendquan/201611031829214009.jpg,/upload/201611/03/big/friendquan/201611031829214679.jpg,/upload/201611/03/big/friendquan/201611031829215339.jpg,/upload/201611/03/big/friendquan/201611031829216479.jpg";
    String friendPhotoMid = "/upload/201611/03/middle/friendquan/201611031829180219_Mid.jpg,/upload/201611/03/middle/friendquan/201611031829199459_Mid.jpg,/upload/201611/03/middle/friendquan/201611031829212599_Mid.jpg,/upload/201611/03/middle/friendquan/201611031829213319_Mid.jpg,/upload/201611/03/middle/friendquan/201611031829214009_Mid.jpg,/upload/201611/03/middle/friendquan/201611031829214679_Mid.jpg,/upload/201611/03/middle/friendquan/201611031829215339_Mid.jpg,/upload/201611/03/middle/friendquan/201611031829216479_Mid.jpg";
    String friendPhotoSma = "/upload/201611/03/small/friendquan/201611031829180219_Sma.jpg,/upload/201611/03/small/friendquan/201611031829199459_Sma.jpg,/upload/201611/03/small/friendquan/201611031829212599_Sma.jpg,/upload/201611/03/small/friendquan/201611031829213319_Sma.jpg,/upload/201611/03/small/friendquan/201611031829214009_Sma.jpg,/upload/201611/03/small/friendquan/201611031829214679_Sma.jpg,/upload/201611/03/small/friendquan/201611031829215339_Sma.jpg,/upload/201611/03/small/friendquan/201611031829216479_Sma.jpg";
    NineGridView photosGv;

    //    String friendPhotoMid = "http://f.hiphotos.baidu.com/image/pic/item/bba1cd11728b47101489df48c0cec3fdfd03238b.jpg,http://f.hiphotos.baidu.com/image/pic/item/203fb80e7bec54e753da379aba389b504fc26a7b.jpg,http://g.hiphotos.baidu.com/image/pic/item/ac6eddc451da81cb87d0ae495166d0160924317b.jpg,http://g.hiphotos.baidu.com/image/pic/item/f703738da97739125daca7e5fb198618377ae2a8.jpg";
//    String friendPhotoSma = "http://f.hiphotos.baidu.com/image/pic/item/bba1cd11728b47101489df48c0cec3fdfd03238b.jpg,http://f.hiphotos.baidu.com/image/pic/item/203fb80e7bec54e753da379aba389b504fc26a7b.jpg,http://g.hiphotos.baidu.com/image/pic/item/ac6eddc451da81cb87d0ae495166d0160924317b.jpg,http://g.hiphotos.baidu.com/image/pic/item/f703738da97739125daca7e5fb198618377ae2a8.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NineGridView.setImageLoader(new NineGridView.ImageLoader<String>() {
            @Override
            public void onDisplayImage(Context context, ImageView imageView, String urlData, boolean isSelect) {
                Glide.with(context).load(urlData).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
            }
        });

        photosGv = (NineGridView) findViewById(R.id.photosGv);
        photosGv.setAdapter(new NineAdapter(this, getFriendPhotoListSma(), getFriendPhotoListMid()));
    }

    public ArrayList<String> getFriendPhotoListMid() {

        String[] photos = friendPhotoMid.split(",");
        ArrayList<String> list = new ArrayList<>();
        for (String a : photos) {
            list.add("http://maige.hbung.com" + a);
        }
        return list;
    }
//"http://maige.hbung.com"

    public ArrayList<String> getFriendPhotoListSma() {

        String[] photos = friendPhotoSma.split(",");
        ArrayList<String> list = new ArrayList<>();
        for (String a : photos) {
            list.add("http://maige.hbung.com" + a);
        }
        return list;
    }
}