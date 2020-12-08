package com.zs.easy.imgcompress.demo;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.zs.easy.imgcompress.EasyImgCompress;
import com.zs.easy.imgcompress.bean.ErrorBean;
import com.zs.easy.imgcompress.listener.OnCompressMultiplePicsListener;
import com.zs.easy.imgcompress.listener.OnCompressSinglePicListener;
import com.zs.easy.imgcompress.util.EasyLogUtil;
import com.zs.easy.imgcompress.util.GBMBKBUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);

        //场景一 把单张图片压缩到100k以内 同时像素不超过1200（宽、高都不大于1200）

        EasyImgCompress.withSinglePic(MainActivity.this, "/mnt/sdcard/111.jpg")
                .maxPx(1800)
                .maxSize(200)
                .enablePxCompress(true)
                .enableQualityCompress(true)
                .enableLog(true)
                .setOnCompressSinglePicListener(new OnCompressSinglePicListener() {
                    @Override
                    public void onStart() {
                        Toast.makeText(MainActivity.this, "start", Toast.LENGTH_SHORT).show();
                        EasyLogUtil.i("withSinglePic onStart");
                    }

                    @Override
                    public void onSuccess(File file) {
                        try {
                            Bitmap loacalBitmap = getLoacalBitmap(file);
                            ((ImageView) findViewById(R.id.easy_image_iv)).setImageBitmap(loacalBitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        EasyLogUtil.i("onSuccess size = " + GBMBKBUtil.getSize(file.length()) + " getAbsolutePath= " + file.getAbsolutePath());
                    }

                    @Override
                    public void onError(String error) {
                        EasyLogUtil.e("onError error = " + error);
                    }
                }).start();

        //场景二 把多张图片每一张都压缩到100k以内 同时每张像素不超过1200（宽、高都不大于1200）
        List<String> imgs = new ArrayList<>();
        imgs.add(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test1.jpg");
        imgs.add(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "点播二级.png");
        EasyImgCompress.withMultiPics(this, imgs)
                .maxPx(1200)
                .maxSize(100)
                .enablePxCompress(true)
                .enableQualityCompress(true)
                .enableLog(true)
                .setOnCompressMultiplePicsListener(new OnCompressMultiplePicsListener() {
                    @Override
                    public void onStart() {
                        Toast.makeText(MainActivity.this, "onStart", Toast.LENGTH_SHORT).show();
                        EasyLogUtil.i("withMultiPics onStart");
                    }

                    @Override
                    public void onSuccess(List<File> successFiles) {
                        Toast.makeText(MainActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < successFiles.size(); i++) {
                            EasyLogUtil.i("onSuccess: successFile size = " + GBMBKBUtil.getSize(successFiles.get(i).length()) + "path = " + successFiles.get(i).getAbsolutePath());
                        }
                    }

                    @Override
                    public void onHasError(List<File> successFiles, List<ErrorBean> errorImages) {
                        for (int i = 0; i < successFiles.size(); i++) {
                            EasyLogUtil.i("onHasError: successFile  size = " + GBMBKBUtil.getSize(successFiles.get(i).length()) + "path = " + successFiles.get(i).getAbsolutePath());
                        }
                        for (int i = 0; i < errorImages.size(); i++) {
                            EasyLogUtil.e("onHasError: errorImg url = " + errorImages.get(i).getErrorImgUrl());
                            EasyLogUtil.e("onHasError: errorImg msg = " + errorImages.get(i).getErrorMsg());
                        }
                    }
                }).start();
    }

    /**
     * 加载本地图片
     *
     * @param file
     * @return
     */
    public static Bitmap getLoacalBitmap(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
