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

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getKontent() {
        return kontent;
    }

    public void setKontent(String kontent) {
        this.kontent = kontent;
    }

    public String getArab() {
        return arab;
    }

    public void setArab(String arab) {
        this.arab = arab;
    }

    public String getArti() {
        return arti;
    }

    public void setArti(String arti) {
        this.arti = arti;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }
}
