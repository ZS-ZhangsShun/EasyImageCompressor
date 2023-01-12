/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.zs.easy.imgcompress.demo;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageInfo implements Parcelable {

    public String filePath;
    public String fileUri;
    public String thumbnailPath;
    public String thumbnailUri;
    public String mimeType;
    public String title;
    public long startTime;
    public int orientation;
    public int id;
    public long addTime;
    public boolean isSquare;
    public int type;

    @Override
    public boolean equals(Object o) {
        if (o instanceof ImageInfo) {
            MediaInfo info = (MediaInfo)o;
            return id == info.id;
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filePath);
        dest.writeString(this.thumbnailPath);
        dest.writeString(this.mimeType);
        dest.writeString(this.title);
        dest.writeLong(this.startTime);
        dest.writeInt(this.orientation);
        dest.writeInt(this.id);
        dest.writeLong(this.addTime);
        dest.writeByte(this.isSquare ? (byte)1 : (byte)0);
        dest.writeInt(this.type);
    }

    public ImageInfo() {}

    protected ImageInfo(Parcel in) {
        this.filePath = in.readString();
        this.thumbnailPath = in.readString();
        this.mimeType = in.readString();
        this.title = in.readString();
        this.startTime = in.readLong();
        this.orientation = in.readInt();
        this.id = in.readInt();
        this.addTime = in.readLong();
        this.isSquare = in.readByte() != 0;
        this.type = in.readInt();
    }

    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(Parcel source) {
            return new ImageInfo(source);
        }

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };
}