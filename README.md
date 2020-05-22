# EasyImageCompressor
## 简介

 通过采样率压缩 + 质量压缩，使用简单的配置即可完成图片指定大小指定尺寸的压缩

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
        implementation 'com.github.ZS-ZhangsShun:EasyImageCompressor:1.0.1'
    }


### 第三步：开始使用（6.0及以上版本需要在代码中动态申请Manifest.permission.WRITE_EXTERNAL_STORAGE 参考示例代码） 以下方法可直接拷贝到项目中使用 参数根据实际情况修改即可 详情参考demo中 MainActivity的示例代码

#### （1）单张图片压缩

        /**
         *  把单张图片压缩到100k以内 同时像素不超过1200（宽、高都不大于1200）
         */
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


