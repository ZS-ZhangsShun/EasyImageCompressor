package com.zs.easy.imgcompress;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片压缩核心类
 * 压缩步骤：
 * <p>
 * 1、使用邻近采样对原始的图片进行采样，将图片控制到比目标尺寸稍大的大小，防止 OOM；
 * 2、使用双线性采样对图片的尺寸进行压缩，控制图片的尺寸为目标的大小；
 * 3、对上述两个步骤之后得到的图片 Bitmap 进行质量压缩，并将其输出到磁盘上。
 *
 * @author zhangshun
 */
public class EasyImgCompress {
    private final String TAG = "EasyImgCompress";
    private Context context;
    private int unCompressMinPx;
    private int maxPx = 1200;
    private int maxSize = 200;
    private String cacheDir = context.getCacheDir().getPath() + File.separator + "CommpressCache";
    private boolean enablePxCompress = true;
    private boolean enableQualityCompress = true;
    private boolean enableReserveRaw = true;

    /**
     * builder设计模式
     *
     * @param builder
     */
    private EasyImgCompress(Builder builder) {
        this.context = builder.context;
        this.unCompressMinPx = builder.unCompressMinPx;
        this.maxPx = builder.maxPx;
        this.maxSize = builder.maxSize;
        this.cacheDir = builder.cacheDir;
        this.enablePxCompress = builder.enablePxCompress;
        this.enableQualityCompress = builder.enableQualityCompress;
        this.enableReserveRaw = builder.enableReserveRaw;

        init();
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    /**
     * Builder 构造类
     */
    public static final class Builder {

        private Context context;
        /**
         * 单张图片地址
         */
        private String imageUrl = "";
        /**
         * 单张图片地址
         */
        private List<String> imageUrls = new ArrayList<>();
        /**
         * 最小像素不压缩 默认1000 如果宽高都不大于1000 那么不进行像素采样压缩，质量压缩该怎么压缩就怎么压缩
         */
        private int unCompressMinPx = 1000;
        /**
         * 长或宽不超过最大像素 默认1200
         */
        private int maxPx = 1200;
        /**
         * 压缩到的最大大小 单位KB 默认200KB
         */
        private int maxSize = 200;
        /**
         * 图片压缩后的缓存路径 默认 /data/data/<application package>/cache/CommpressCache
         * <p>
         * 记录一下
         * public String getDiskCacheDir(Context context) {  
         *     String cachePath = null;  
         *     if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())  
         *             || !Environment.isExternalStorageRemovable()) {  
         *         cachePath = context.getExternalCacheDir().getPath();  
         *     } else {  
         *         cachePath = context.getCacheDir().getPath();  
         *     }  
         *     return cachePath;  
         * }  
         * 当SD卡存在或者SD卡不可被移除的时候，就调用getExternalCacheDir()方法来获取缓存路径，否则就调用getCacheDir()方法来获取缓存路径。
         * 前者获取到的就是 /sdcard/Android/data/<application package>/cache 这个路径
         * 而后者获取到的是 /data/data/<application package>/cache 这个路径。
         */
        private String cacheDir = context.getCacheDir().getPath() + File.separator + "CommpressCache";
        /**
         * 是否启用像素压缩 默认true
         */
        private boolean enablePxCompress = true;
        /**
         * 是否启用质量压缩 默认true
         */
        private boolean enableQualityCompress = true;
        /**
         * 是否保留源文件 默认true
         */
        private boolean enableReserveRaw = true;
        /**
         * 单张图片压缩的监听
         */
        private OnCompressSinglePicListener onCompressSinglePicListener;
        /**
         * 多张图片压缩的监听
         */
        private OnCompressMultiplePicsListener onCompressMultiplePicsListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder load(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder load(List<String> imageUrls) {
            this.imageUrls = imageUrls;
            return this;
        }

        public Builder unCompressMinPx(int unCompressMinPx) {
            this.unCompressMinPx = unCompressMinPx;
            return this;
        }

        public Builder maxPx(int maxPx) {
            this.maxPx = maxPx;
            return this;
        }

        public Builder maxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public Builder cacheDir(String autoReconnect) {
            this.cacheDir = cacheDir;
            return this;
        }

        public Builder enablePxCompress(boolean enablePxCompress) {
            this.enablePxCompress = enablePxCompress;
            return this;
        }

        public Builder enableReserveRaw(boolean enableReserveRaw) {
            this.enableReserveRaw = enableReserveRaw;
            return this;
        }

        public Builder enableQualityCompress(boolean enableQualityCompress) {
            this.enableQualityCompress = enableQualityCompress;
            return this;
        }

        public Builder setOnCompressSinglePicListener(OnCompressSinglePicListener onCompressSinglePicListener) {
            this.onCompressSinglePicListener = onCompressSinglePicListener;
            return this;
        }

        public Builder setOnCompressMultiplePicsListener(OnCompressMultiplePicsListener onCompressMultiplePicsListener) {
            this.onCompressMultiplePicsListener = onCompressMultiplePicsListener;
            return this;
        }

        public EasyImgCompress start() {
            return new EasyImgCompress(this);
        }
    }

    private void init() {

    }

}