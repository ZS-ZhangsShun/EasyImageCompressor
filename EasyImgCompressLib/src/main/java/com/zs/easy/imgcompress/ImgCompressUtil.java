package com.zs.easy.imgcompress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImgCompressUtil {

    /**
     * 采样率压缩
     *
     * @param bitmap
     * @param sampleSize 采样率为2的整数倍，非整数倍四舍五入，如4的话，就是原图的1/4
     * @return 尺寸变化
     */
    public static Bitmap getBitmap(Bitmap bitmap, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        Bitmap bit = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        Log.i("info", "图片大小：" + bit.getByteCount());//2665296  10661184
        return bit;
    }

    /**
     * 图片质量压缩
     *
     * @param bitmap
     * @param quality
     * @return 尺寸不变，质量变小
     */
    public static Bitmap compressByQuality(Bitmap bitmap, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        Bitmap bit = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Log.i("info", "图片大小：" + bit.getByteCount());//10661184
        return bit;
    }

    /**
     * 图片质量压缩
     *
     * @param src
     * @param maxByteSize
     * @return
     */
    public static Bitmap compressByQuality(Bitmap src, long maxByteSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        src.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        while (baos.toByteArray().length > maxByteSize && quality > 0) {
            baos.reset();
            src.compress(Bitmap.CompressFormat.JPEG, quality -= 5, baos);
        }
        if (quality < 0) return null;
        byte[] bytes = baos.toByteArray();
        Bitmap bit = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bit;
    }

    public static Bitmap compressByFormat(Bitmap bitmap, int format) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        Bitmap bit = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Log.i("info", "图片大小：" + bit.getByteCount());//10661184
        return bit;
    }

    /**
     * Matrix缩放
     *
     * @param bitmap
     * @param scaleWidth
     * @param scaleHeight
     * @return 尺寸和大小变化
     */
    public static Bitmap getBitmapBySize(Bitmap bitmap, float scaleWidth, float scaleHeight) {
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bit = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        Log.i("info", "图片大小：" + bit.getByteCount());
        return bit;
    }

    /**
     * 按照图片格式配置压缩
     *
     * @param path
     * @param config ALPHA_8,ARGB_4444,ARGB_8888,RGB_565;
     * @return RGB_565比ARGB_8888节省一半内存
     */
    public static Bitmap getBitmapByFormatConfig(String path, Bitmap.Config config) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = config;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        Log.i("info", "图片大小：" + bitmap.getByteCount());
        return bitmap;
    }

    /**
     * 指定大小缩放
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBitmapByScaleSize(Bitmap bitmap, int width, int height) {
        Bitmap bit = Bitmap.createScaledBitmap(bitmap, width, height, true);
        Log.i("info", "图片大小：" + bit.getByteCount());
        return bit;
    }

    /**
     * 通过保存格式压缩
     *
     * @param bitmap
     * @param format JPEG,PNG,WEBP
     * @return
     */
    public static Bitmap getBitmapByFormat(Bitmap bitmap, Bitmap.CompressFormat format) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        byte[] bytes = baos.toByteArray();
        Bitmap bit = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Log.i("info", "图片大小：" + bit.getByteCount());
        return bit;
    }

    /**
     * 文件加载压缩
     *
     * @param filePath
     * @param inSampleSize
     * @return
     */
    public static Bitmap getBitmap(String filePath, int inSampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);//此时不耗费和占用内存
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Bitmap getBitmap(String filePath) {
        return BitmapFactory.decodeFile(filePath);
    }

    public static Bitmap view2Bitmap(View view) {
        if (view == null) return null;
        Bitmap ret = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(ret);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return ret;
    }

    public static void saveBitmap(Bitmap bitmap) {
        File file = new File(Environment.getExternalStorageDirectory() + "/img.jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveBitmap(Bitmap bitmap, Bitmap.CompressFormat format) {
        File file = new File(Environment.getExternalStorageDirectory() + "/img.jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(format, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
