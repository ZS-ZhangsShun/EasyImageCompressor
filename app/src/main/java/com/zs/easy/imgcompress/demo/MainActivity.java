package com.zs.easy.imgcompress.demo;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zs.easy.imgcompress.EasyImgCompress;
import com.zs.easy.imgcompress.OnCompressMultiplePicsListener;
import com.zs.easy.imgcompress.OnCompressSinglePicListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

        //场景一 把单张图片压缩到100k以内 同时像素不超过1200（宽、高都不大于1200）
        EasyImgCompress.withSinglePic(this, "/mnt/sdcard/ttt.jpg")
                .maxPx(1200)
                .maxSize(100)
                .setOnCompressSinglePicListener(new OnCompressSinglePicListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.i("EasyImgCompress", "size = " + file.length() + " path= " + file.getAbsolutePath());
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("EasyImgCompress", "error = " + error);
                    }
                }).start();


        //场景二 把多张图片每一张都压缩到100k以内 同时每张像素不超过1200（宽、高都不大于1200）
        EasyImgCompress.withMultiPics(this, new ArrayList<String>())
                .maxPx(1200)
                .maxSize(100)
                .setOnCompressMultiplePicsListener(new OnCompressMultiplePicsListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(List<File> successFiles) {

                    }

                    @Override
                    public void onHasError(List<File> successFiles, List<String> errorImages, List<String> errorMsgs) {

                    }
                })
                .start();
    }
}
