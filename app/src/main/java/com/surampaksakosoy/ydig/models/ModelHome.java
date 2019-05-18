package com.surampaksakosoy.ydig.models;

import java.util.List;

public class ModelHome {
    public static final int TEXT_TYPE = 0;
    public static final int IMAGE_TYPE = 1;
    public static final int AUDIO_TYPE = 2;
    public static final int VIDEO_TYPE = 3;

    public int type;
    private String data;
    public String videoPath;
    public String judul, konten, arab, arti;

    public ModelHome(int type, String data, String videoPath, String judul, String konten, String arab, String arti) {
        this.type = type;
        this.data = data;
        this.videoPath = videoPath;
        this.judul = judul;
        this.konten = konten;
        this.arab = arab;
        this.arti = arti;
    }
}
