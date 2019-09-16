package com.zs.easy.imgcompress;

import java.io.File;
import java.util.List;

public interface OnCompressMultiplePicsListener {

    /**
     * 压缩开始
     */
    void onStart();

    /**
     * 压缩成功
     */
    void onSuccess(List<File> files);

    /**
     * 压缩出错
     */
    void onError(List<String> images, String error);
}
