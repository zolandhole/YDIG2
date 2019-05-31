package com.surampaksakosoy.ydig.models;

public class ModelPanduan {

    private int id;
    private String panduan_id;
    private String judul;
    private byte[] image;

    public ModelPanduan(int id, String panduan_id, String judul, byte[] image) {
        this.id = id;
        this.panduan_id = panduan_id;
        this.judul = judul;
        this.image = image;
    }

    public int getId() {
        return id;
    }

//    public void setId(int id) {
//        this.id = id;
//    }

    public String getPanduan_id() {
        return panduan_id;
    }

    public String getJudul() {
        return judul;
    }

    public byte[] getImage() {
        return image;
    }
}
