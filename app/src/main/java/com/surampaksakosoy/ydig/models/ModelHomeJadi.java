package com.surampaksakosoy.ydig.models;

public class ModelHomeJadi {
    private int id;
    private int type;
    private String data, videoPath, judul, kontent, arab, arti, upload_date;

    public ModelHomeJadi(int id, int type, String data, String videoPath, String judul, String kontent, String arab, String arti, String upload_date) {
        this.id = id;
        this.type = type;
        this.data = data;
        this.videoPath = videoPath;
        this.judul = judul;
        this.kontent = kontent;
        this.arab = arab;
        this.arti = arti;
        this.upload_date = upload_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public String getJudul() {
        return judul;
    }

    public String getKontent() {
        return kontent;
    }

    public String getArab() {
        return arab;
    }

    public String getArti() {
        return arti;
    }

    public String getUpload_date() {
        return upload_date;
    }
}
