package com.zs.easy.imgcompress.listener;

import com.zs.easy.imgcompress.bean.ErrorBean;

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
    void onSuccess(List<File> successFiles);

    /**
     * 压缩出错
     */
    void onHasError(List<File> successFiles, List<ErrorBean> errors);
}
