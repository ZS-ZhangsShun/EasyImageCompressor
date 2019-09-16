package com.zs.easy.imgcompress;

import java.io.File;

public interface OnCompressSinglePicListener {

    /**
     * 压缩开始
     */
    void onStart();

    /**
     * 压缩成功
     */
    void onSuccess(File file);

    /**
     * 压缩出错
     */
    void onError(String error);
}
