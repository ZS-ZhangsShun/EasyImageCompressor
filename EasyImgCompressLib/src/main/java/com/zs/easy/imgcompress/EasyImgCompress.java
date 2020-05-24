package com.zs.easy.imgcompress;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
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
    private int unCompressMinPx = 1000;
    private int maxPx = 1200;
    private int maxSize = 200;
    private String cacheDir = "";
    private boolean enablePxCompress = true;
    private boolean enableQualityCompress = true;
    private boolean enableReserveRaw = true;
    private String imageUrl = "";
    private List<String> imageUrls = new ArrayList<>();
    private OnCompressSinglePicListener onCompressSinglePicListener;
    private OnCompressMultiplePicsListener onCompressMultiplePicsListener;

    /**
     * builder设计模式
     *
     * @param builder
     */
    private EasyImgCompress(SinglePicBuilder builder) {
        this.context = builder.context;
        this.unCompressMinPx = builder.unCompressMinPx;
        this.maxPx = builder.maxPx;
        this.maxSize = builder.maxSize;
        this.cacheDir = builder.cacheDir;
        this.enablePxCompress = builder.enablePxCompress;
        this.enableQualityCompress = builder.enableQualityCompress;
        this.enableReserveRaw = builder.enableReserveRaw;
        this.imageUrl = builder.imageUrl;
        this.onCompressSinglePicListener = builder.onCompressSinglePicListener;

        startCompressForSingle();
    }

    /**
     * builder设计模式
     *
     * @param builder
     */
    private EasyImgCompress(MultiPicsBuilder builder) {
        this.context = builder.context;
        this.unCompressMinPx = builder.unCompressMinPx;
        this.maxPx = builder.maxPx;
        this.maxSize = builder.maxSize;
        this.cacheDir = builder.cacheDir;
        this.enablePxCompress = builder.enablePxCompress;
        this.enableQualityCompress = builder.enableQualityCompress;
        this.enableReserveRaw = builder.enableReserveRaw;
        this.imageUrls = builder.imageUrls;
        this.onCompressMultiplePicsListener = builder.onCompressMultiplePicsListener;

        startCompressForMulty();
    }

    public static SinglePicBuilder withSinglePic(Context context, String imageUrl) {
        return new SinglePicBuilder(context, imageUrl);
    }

    public static MultiPicsBuilder withMultiPics(Context context, List<String> imageUrls) {
        return new MultiPicsBuilder(context, imageUrls);
    }

    /**
     * 开启压缩
     */
    private void startCompressForSingle() {
        if (onCompressSinglePicListener != null) {
            onCompressSinglePicListener.onStart();
        }
        //校验图片
        if (TextUtils.isEmpty(imageUrl)) {
            if (onCompressSinglePicListener != null) {
                onCompressSinglePicListener.onError("请传入要压缩的图片");
            }
            return;
        }
        Log.i(TAG, "原图片地址：" + imageUrl);
        Log.i(TAG, "保存地址：" + cacheDir);
        Log.i(TAG, Environment.getExternalStorageDirectory().getAbsolutePath());
        //校验权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int writePermission = context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                if (onCompressSinglePicListener != null) {
                    onCompressSinglePicListener.onError("请先申请对应的sd卡读写权限");
                }
                return;
            }
        }
        //对单张图片进行压缩
        compressImg();
    }

    /**
     * 开启压缩
     */
    private void startCompressForMulty() {
        if (onCompressMultiplePicsListener != null) {
            onCompressMultiplePicsListener.onStart();
        }
        if (imageUrls == null || imageUrls.size() == 0) {
            if (onCompressMultiplePicsListener != null) {
                List<ErrorBean> errors = new ArrayList<>();
                ErrorBean errorBean = new ErrorBean();
                errorBean.setErrorMsg("请传入要压缩的图片");
                errors.add(errorBean);
                onCompressMultiplePicsListener.onHasError(new ArrayList<File>(), errors);
            }
            return;
        }

        //对多张图片进行压缩
        compressImgs();
    }

    /**
     * 压缩单张图片
     */
    private void compressImg() {
        File originalFile = new File(imageUrl);
        if (!originalFile.isFile() || !originalFile.exists()) {
            if (onCompressSinglePicListener != null) {
                onCompressSinglePicListener.onError("出错了！ 您传入的文件不存在！或者不是一个文件");
            }
            return;
        }

        if (cacheDir.endsWith(".png") || cacheDir.contains(".jpg") || cacheDir.contains(".jpeg") || cacheDir.contains(".webp") || cacheDir.contains(".bmp")) {
            if (onCompressSinglePicListener != null) {
                onCompressSinglePicListener.onError("出错了，请检查保存路径格式，当前保存路径为：" + cacheDir + " 规范的保存路径示例：/data/data/<application package name>/cache 注意要传入一个文件夹的路径");
            }
            return;
        }
        //第一步 快速粗略的进行尺寸压缩 有效减小图片大小 防止oom
        Bitmap bm = ImgCompressUtil.compressBySampleSize(imageUrl, maxPx);
        if (bm == null && onCompressSinglePicListener != null) {
            onCompressSinglePicListener.onError("出错了，请检查文件是否具有读写权限");
            return;
        }
        //TODO 第二步 精确尺寸压缩
        //第三步 质量压缩 压缩到指定大小 比如100kb
        ByteArrayOutputStream baos = ImgCompressUtil.compressByQualityForByteArray(bm, maxSize);
        // 第四步 写入文件
        File files = new File(cacheDir);
        if (!files.exists()) {
            files.mkdirs();
        }
        File file = ImgCompressUtil.saveBitmap(baos, cacheDir + File.separator + System.currentTimeMillis() + ".jpg");
        if (onCompressSinglePicListener == null) {
            return;
        }
        if (file != null && file.exists()) {
            onCompressSinglePicListener.onSuccess(file);
        } else {
            onCompressSinglePicListener.onError("请检查：1、保存路径格式，当前保存路径为："
                    + cacheDir + " 规范的保存路径示例：/data/data/<application package name>/cache 注意要传入一个文件夹的路径"
                    + "2、当前保存路径是否有读写权限");
        }
    }

    /**
     * 压缩多张图片
     */
    private void compressImgs() {
        List<File> successFiles = new ArrayList<>();
        List<ErrorBean> errors = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            String imgUrl = imageUrls.get(i);
            File originalFile = new File(imgUrl);
            if (!originalFile.isFile() || !originalFile.exists()) {
                ErrorBean errorBean = new ErrorBean();
                errorBean.setErrorImgUrl(imgUrl);
                errorBean.setErrorMsg("出错了！ 您传入的文件不存在！或者不是一个文件");
                errors.add(errorBean);
                continue;
            }

            if (cacheDir.endsWith(".png") || cacheDir.contains(".jpg") || cacheDir.contains(".jpeg") || cacheDir.contains(".webp") || cacheDir.contains(".bmp")) {
                ErrorBean errorBean = new ErrorBean();
                errorBean.setErrorImgUrl(imgUrl);
                errorBean.setErrorMsg("出错了，请检查保存路径格式，当前保存路径为：" + cacheDir + " 规范的保存路径示例：/data/data/<application package name>/cache 注意要传入一个文件夹的路径");
                errors.add(errorBean);
                continue;
            }
            //第一步 快速粗略的进行尺寸压缩 有效减小图片大小 防止oom
            Bitmap bm = ImgCompressUtil.compressBySampleSize(imgUrl, maxPx);
            if (bm == null) {
                ErrorBean errorBean = new ErrorBean();
                errorBean.setErrorImgUrl(imgUrl);
                errorBean.setErrorMsg("出错了，请检查文件是否具有读写权限");
                errors.add(errorBean);
                continue;
            }
            //TODO 第二步 精确尺寸压缩
            //第三步 质量压缩 压缩到指定大小 比如100kb
            ByteArrayOutputStream baos = ImgCompressUtil.compressByQualityForByteArray(bm, maxSize);
            // 第四步 写入文件
            File files = new File(cacheDir);
            if (!files.exists()) {
                files.mkdirs();
            }
            File file = ImgCompressUtil.saveBitmap(baos, cacheDir + File.separator + System.currentTimeMillis() + ".jpg");
            if (file != null && file.exists()) {
                successFiles.add(file);
            } else {
                ErrorBean errorBean = new ErrorBean();
                errorBean.setErrorImgUrl(imgUrl);
                errorBean.setErrorMsg("请检查：1、保存路径格式，当前保存路径为："
                        + cacheDir + " 规范的保存路径示例：/data/data/<application package name>/cache 注意要传入一个文件夹的路径"
                        + "2、当前保存路径是否有读写权限");
                errors.add(errorBean);
            }
        }
        if (onCompressMultiplePicsListener != null) {
            if (errors.size() > 0) {
                onCompressMultiplePicsListener.onHasError(successFiles, errors);
            } else {
                onCompressMultiplePicsListener.onSuccess(successFiles);
            }
        }
    }


    /**
     * SinglePicBuilder 构造类
     */
    public static class SinglePicBuilder {

        private Context context;
        /**
         * 单张图片地址
         */
        private String imageUrl = "";
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
         * 图片压缩后的缓存路径 默认 /data/data/<application package>/cache/CompressCache
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
        private String cacheDir = "";
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

        public SinglePicBuilder(Context context, String imageUrl) {
            this.context = context;
            this.imageUrl = imageUrl;
            this.cacheDir = context.getCacheDir().getPath() + File.separator + "CompressCache";
        }

        public SinglePicBuilder unCompressMinPx(int unCompressMinPx) {
            this.unCompressMinPx = unCompressMinPx;
            return this;
        }

        public SinglePicBuilder maxPx(int maxPx) {
            this.maxPx = maxPx;
            return this;
        }

        public SinglePicBuilder maxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public SinglePicBuilder cacheDir(String cacheDir) {
            this.cacheDir = cacheDir;
            return this;
        }

        public SinglePicBuilder enablePxCompress(boolean enablePxCompress) {
            this.enablePxCompress = enablePxCompress;
            return this;
        }

        public SinglePicBuilder enableReserveRaw(boolean enableReserveRaw) {
            this.enableReserveRaw = enableReserveRaw;
            return this;
        }

        public SinglePicBuilder enableQualityCompress(boolean enableQualityCompress) {
            this.enableQualityCompress = enableQualityCompress;
            return this;
        }

        public SinglePicBuilder setOnCompressSinglePicListener(OnCompressSinglePicListener onCompressSinglePicListener) {
            this.onCompressSinglePicListener = onCompressSinglePicListener;
            return this;
        }

        public EasyImgCompress start() {
            return new EasyImgCompress(this);
        }

    }

    /**
     * MultiPicsBuilder 构造类
     */
    public static class MultiPicsBuilder {

        private Context context;
        /**
         * 多张图片地址
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
         * 图片压缩后的缓存路径 默认 /data/data/<application package>/cache/CompressCache
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
        private String cacheDir = "";
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
         * 多张图片压缩的监听
         */
        private OnCompressMultiplePicsListener onCompressMultiplePicsListener;

        public MultiPicsBuilder(Context context, List<String> imageUrls) {
            this.context = context;
            this.imageUrls = imageUrls;
            this.cacheDir = context.getCacheDir().getPath() + File.separator + "CompressCache";
        }

        public MultiPicsBuilder unCompressMinPx(int unCompressMinPx) {
            this.unCompressMinPx = unCompressMinPx;
            return this;
        }

        public MultiPicsBuilder maxPx(int maxPx) {
            this.maxPx = maxPx;
            return this;
        }

        public MultiPicsBuilder maxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public MultiPicsBuilder cacheDir(String autoReconnect) {
            this.cacheDir = cacheDir;
            return this;
        }

        public MultiPicsBuilder enablePxCompress(boolean enablePxCompress) {
            this.enablePxCompress = enablePxCompress;
            return this;
        }

        public MultiPicsBuilder enableReserveRaw(boolean enableReserveRaw) {
            this.enableReserveRaw = enableReserveRaw;
            return this;
        }

        public MultiPicsBuilder enableQualityCompress(boolean enableQualityCompress) {
            this.enableQualityCompress = enableQualityCompress;
            return this;
        }

        public MultiPicsBuilder setOnCompressMultiplePicsListener(OnCompressMultiplePicsListener onCompressMultiplePicsListener) {
            this.onCompressMultiplePicsListener = onCompressMultiplePicsListener;
            return this;
        }

        public EasyImgCompress start() {
            return new EasyImgCompress(this);
        }
    }

}