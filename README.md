# EasyImageCompressor
## 简介

    1、通过采样率压缩 + 缩放 + 质量压缩，使用简单的配置即可完成图片指定大小指定尺寸的精确压缩
    2、内部采用线程池设计，默认在子线程执行压缩任务，在主线程回调onStart onError onSuccess方法
    3、自动检测图片旋转角度，压缩后不影响图片原来的旋转角度
    4、框架内部调试日志支持通过 enableLog 开关进行配置，默认是关闭的

## 使用方法

### 第一步：在project的build.gradle 文件中添加JitPack依赖

    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }

### 第二步: 在Module的build.gradle文件中添加对本库的依赖

    dependencies {
        ...
        implementation 'com.github.ZS-ZhangsShun:EasyImageCompressor:1.0.7'
    }


### 第三步：开始使用（6.0及以上并且android 13以下版本需要在代码中动态申请Manifest.permission.WRITE_EXTERNAL_STORAGE 可以参考示例代码，Android 13 大家按照官方的权限分类和各自具体情况来处理，比如使用 Manifest.permission.READ_MEDIA_IMAGES权限） 以下方法可直接拷贝到项目中使用 参数根据实际情况修改即可 详情参考demo中 MainActivity的示例代码

#### （1）单张图片压缩

        //场景一 把单张图片压缩到100k以内 同时像素不超过1200（宽、高都不大于1200）
        EasyImgCompress.withSinglePic(this, "/mnt/sdcard/test1.jpg")
                .maxPx(1200)
                .maxSize(100)
                .enableLog(true)
                .setOnCompressSinglePicListener(new OnCompressSinglePicListener() {
                    @Override
                    public void onStart() {
                        Log.i("EasyImgCompress", "onStart");
                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.i("EasyImgCompress", "onSuccess size = " + GBMBKBUtil.getSize(file.length()) + " getAbsolutePath= " + file.getAbsolutePath());
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("EasyImgCompress", "onError error = " + error);
                    }
                }).start();

            过滤EasyImgCompress 看日志：
            2020-05-24 12:09:29.781 17286-17286/com.zs.easy.imgcompress I/EasyImgCompress: onStart
            2020-05-24 12:09:29.782 17286-17286/com.zs.easy.imgcompress I/EasyImgCompress: 原图片地址：/mnt/sdcard/test1.jpg
            2020-05-24 12:09:29.782 17286-17286/com.zs.easy.imgcompress I/EasyImgCompress: 保存地址：/data/user/0/com.zs.easy.imgcompress/cache/CompressCache
            2020-05-24 12:09:30.931 17286-17286/com.zs.easy.imgcompress I/EasyImgCompress: onSuccess size = 99.31KB    getAbsolutePath= /data/user/0/com.zs.easy.imgcompress/cache/CompressCache/1590293370924.jpg

#### （2）多张图片压缩

        //场景二 把多张图片每一张都压缩到100k以内 同时每张像素不超过1200（宽、高都不大于1200）
        List<String> imgs = new ArrayList<>();
        imgs.add(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test1.jpg");
        imgs.add(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test2.jpg");
        imgs.add(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test3.jpg");
        imgs.add(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test4.jpg");
        EasyImgCompress.withMultiPics(this, imgs)
                .maxPx(1200)
                .maxSize(100)
                .enableLog(true)
                .setOnCompressMultiplePicsListener(new OnCompressMultiplePicsListener() {
                    @Override
                    public void onStart() {
                        Log.i("EasyImgCompress", "onStart");
                    }

                    @Override
                    public void onSuccess(List<File> successFiles) {
                        for (int i = 0; i < successFiles.size(); i++) {
                            Log.i("EasyImgCompress", "onSuccess: successFile size = " + GBMBKBUtil.getSize(successFiles.get(i).length()) + "path = " + successFiles.get(i).getAbsolutePath());
                        }
                    }

                    @Override
                    public void onHasError(List<File> successFiles, List<ErrorBean> errorImages) {
                        for (int i = 0; i < successFiles.size(); i++) {
                            Log.i("EasyImgCompress", "onHasError: successFile  size = " + GBMBKBUtil.getSize(successFiles.get(i).length()) + "path = " + successFiles.get(i).getAbsolutePath());
                        }
                        for (int i = 0; i < errorImages.size(); i++) {
                            Log.e("EasyImgCompress", "onHasError: errorImg url = " + errorImages.get(i).getErrorImgUrl());
                            Log.e("EasyImgCompress", "onHasError: errorImg msg = " + errorImages.get(i).getErrorMsg());
                        }
                    }
                }).start();

          过滤EasyImgCompress 看日志：
          2020-05-24 12:09:30.949 17286-17286/com.zs.easy.imgcompress I/EasyImgCompress: onStart
          2020-05-24 12:09:34.984 17286-17286/com.zs.easy.imgcompress I/EasyImgCompress: onSuccess: successFile size = 99.31KB   path = /data/user/0/com.zs.easy.imgcompress/cache/CompressCache/1590293372046.jpg
          2020-05-24 12:09:34.988 17286-17286/com.zs.easy.imgcompress I/EasyImgCompress: onSuccess: successFile size = 93.11KB   path = /data/user/0/com.zs.easy.imgcompress/cache/CompressCache/1590293372918.jpg
          2020-05-24 12:09:34.993 17286-17286/com.zs.easy.imgcompress I/EasyImgCompress: onSuccess: successFile size = 95.21KB   path = /data/user/0/com.zs.easy.imgcompress/cache/CompressCache/1590293374030.jpg
          2020-05-24 12:09:34.997 17286-17286/com.zs.easy.imgcompress I/EasyImgCompress: onSuccess: successFile size = 94.50KB   path = /data/user/0/com.zs.easy.imgcompress/cache/CompressCache/1590293374976.jpg

#### （3）相关配置

        //1、压缩后的保存路径设置
        //默认是应用内部缓存 context.getCacheDir().getPath() + File.separator + "CompressCache"
        // 即：/data/data/<application package>/cache/CompressCache
        EasyImgCompress.withXXX().cacheDir("传入压缩完后存储的文件夹路径，注意是文件夹路径")



## 混淆配置
  -keep class com.zs.easy.imgcompress.** {*;}
