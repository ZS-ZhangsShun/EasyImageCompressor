package com.zs.easy.imgcompress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //场景一 把单张图片压缩到100k以内 同时像素不超过1200（宽、高都不大于1200）
        EasyImgCompress.with(this).load("")
                .maxPx(1200)
                .maxSize(100)
                .setOnCompressSinglePicListener(new OnCompressSinglePicListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File file) {

                    }

                    @Override
                    public void onError(String error) {

                    }
                }).start();


        //场景二 把多张图片每一张都压缩到100k以内 同时每张像素不超过1200（宽、高都不大于1200）
        EasyImgCompress.with(this).load(new ArrayList<String>())
                .maxPx(1200)
                .maxSize(100)
                .setOnCompressMultiplePicsListener(new OnCompressMultiplePicsListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(List<File> files) {

                    }

                    @Override
                    public void onError(List<String> images, String error) {

                    }
                }).start();
    }
}
