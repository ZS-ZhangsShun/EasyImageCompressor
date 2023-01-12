package com.zs.easy.imgcompress.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zs.easy.common.utils.LogUtil;
import com.zs.easy.common.utils.ToastAndLogUtil;
import com.zs.easy.imgcompress.EasyImgCompress;
import com.zs.easy.imgcompress.listener.OnCompressSinglePicListener;
import com.zs.easy.imgcompress.util.EasyLogUtil;
import com.zs.easy.imgcompress.util.GBMBKBUtil;
//import com.zs.easy.imgcompress.EasyImgCompress;
//import com.zs.easy.imgcompress.bean.ErrorBean;
//import com.zs.easy.imgcompress.listener.OnCompressMultiplePicsListener;
//import com.zs.easy.imgcompress.util.EasyLogUtil;
//import com.zs.easy.imgcompress.util.GBMBKBUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
    private EditText main_compress_size_et, main_compress_px_et, main_save_address_et;
    private Button main_select_pics_btn, main_compress_btn;
    private ImageView main_before_compress_pic_iv, main_after_compress_pic_iv;
    private ImageView main_after_compress_wx_iv, main_after_compress_qq_iv;
    private TextView main_before_compress_path_tv, main_before_compress_size_tv;
    private TextView main_after_compress_path_tv, main_after_compress_size_tv;
    private CheckBox main_compress_over_original_pic_cb;

    private int REQUEST_CODE_CHOOSE = 1001;
    private List<String> selectPicUrlList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.CAMERA}, 100);
        }
        EasyLogUtil.enableLog = true;

        initView();
        initListener();
        initData();
    }

    private void initView() {
        main_compress_size_et = findViewById(R.id.main_compress_size_et);
        main_compress_px_et = findViewById(R.id.main_compress_px_et);
        main_save_address_et = findViewById(R.id.main_save_address_et);
        main_select_pics_btn = findViewById(R.id.main_select_pics_btn);
        main_compress_btn = findViewById(R.id.main_compress_btn);
        main_before_compress_pic_iv = findViewById(R.id.main_before_compress_pic_iv);
        main_after_compress_pic_iv = findViewById(R.id.main_after_compress_pic_iv);
        main_before_compress_path_tv = findViewById(R.id.main_before_compress_path_tv);
        main_before_compress_size_tv = findViewById(R.id.main_before_compress_size_tv);
        main_after_compress_path_tv = findViewById(R.id.main_after_compress_path_tv);
        main_after_compress_size_tv = findViewById(R.id.main_after_compress_size_tv);
        main_after_compress_wx_iv = findViewById(R.id.main_after_compress_wx_iv);
        main_after_compress_qq_iv = findViewById(R.id.main_after_compress_qq_iv);
        main_compress_over_original_pic_cb = findViewById(R.id.main_compress_over_original_pic_cb);
    }

    private void initListener() {
        main_select_pics_btn.setOnClickListener(this);
        main_compress_btn.setOnClickListener(this);
        main_after_compress_wx_iv.setOnClickListener(this);
        main_after_compress_qq_iv.setOnClickListener(this);
    }

    private void initData() {
        main_compress_size_et.setText("300");
        main_compress_px_et.setText("1000");
        main_save_address_et.setText(Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    /**
     * 加载本地图片
     *
     * @param file
     * @return
     */
    public static Bitmap getLocalBitmap(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == main_select_pics_btn) {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //此参数不生效 仅仅对录制生效
//        i.putExtra(MediaStore.EXTRA_DURATION_LIMIT, StarConstants.SV_MAX_DURATION);
//        i.putExtra(MediaStore.EXTRA_SIZE_LIMIT, StarConstants.SV_MAX_SIZE);
            startActivityForResult(i, REQUEST_CODE_CHOOSE);
        } else if (v == main_compress_btn) {
            compressSinglePic();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK && null != data) {
            final Uri uri = data.getData();
            ImageInfo info = GetInfoFromUri.getImageInfoFromUri(this, uri);
            final String path = info.filePath;
            //content://media/external/video/media/574778
            EasyLogUtil.i("select result uri = " + uri.toString());
            ///storage/emulated/0/DCIM/Camera/8cf28f4644c61e0c012c9a1714dc997f.mp4
            EasyLogUtil.i("select result path = " + path);
            selectPicUrlList.add(0, path);
            main_before_compress_path_tv.setText(path);
            main_before_compress_size_tv.setText(GBMBKBUtil.getSize(new File(path).length()));
            Glide.with(MainActivity.this).load(path).into(main_before_compress_pic_iv);
        }
    }

    private void compressSinglePic() {
        //场景一 把单张图片压缩到100k以内 同时像素不超过1200（宽、高都不大于1200）
        if (!isInputOk()) {
            return;
        }
        String sizeStr = main_compress_size_et.getText().toString().trim();
        String pxStr = main_compress_px_et.getText().toString().trim();
        EasyImgCompress.withSinglePic(MainActivity.this, selectPicUrlList.get(0))
                .maxPx(Integer.parseInt(pxStr))
                .maxSize(Integer.parseInt(sizeStr))
                .enablePxCompress(true)
                //打开此开关 会对尺寸精确缩放 但有可能边缘被裁剪
                .enableMatrixCompress(false)
                .enableLog(true)
                .enableQualityCompress(true)
                .setOnCompressSinglePicListener(new OnCompressSinglePicListener() {
                    @Override
                    public void onStart() {
                        Toast.makeText(MainActivity.this, "start", Toast.LENGTH_SHORT).show();
                        EasyLogUtil.i("withSinglePic onStart");
                    }

                    @Override
                    public void onSuccess(File file) {
                        try {
                            main_after_compress_path_tv.setText(file.getAbsolutePath());
                            main_after_compress_size_tv.setText(GBMBKBUtil.getSize(file.length()));
                            Glide.with(MainActivity.this).load(file).into(main_after_compress_pic_iv);
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
    }
/*
    private void compressWithPics(List<String> imgs) {
        boolean isInputOk = isInputOk();
        if (!isInputOk) {
            return;
        }
        EasyImgCompress.withMultiPics(this, imgs)
                .maxPx(1200)
                .maxSize(100)
                .enablePxCompress(true)
                .enableQualityCompress(true)
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
                        if (successFiles.size() == 1) {
                            //单张图片 展示出来
                            Bitmap localBitmap = getLocalBitmap(successFiles.get(0));
                            main_after_compress_pic_iv.setImageBitmap(localBitmap);
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
    }*/

    private boolean isInputOk() {
        String sizeStr = main_compress_size_et.getText().toString().trim();
        if (TextUtils.isEmpty(sizeStr)) {
            ToastAndLogUtil.TL("请输入压缩大小，例如 300 单位 kb");
            return false;
        }

        String pxStr = main_compress_px_et.getText().toString().trim();
        if (TextUtils.isEmpty(pxStr)) {
            ToastAndLogUtil.TL("请输入压缩尺寸，例如 1000 单位 px");
            return false;
        }

        String savePathStr = main_save_address_et.getText().toString().trim();
        if (TextUtils.isEmpty(savePathStr)) {
            ToastAndLogUtil.TL("请输入保存地址，例如根路径 ： " + Environment.getExternalStorageDirectory().getAbsolutePath());
            return false;
        }

        if (selectPicUrlList == null || selectPicUrlList.size() == 0) {
            ToastAndLogUtil.TL("请先选择图片");
            return false;
        }
        return true;
    }
}
