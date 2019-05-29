package com.surampaksakosoy.ydig.models;

public class ModelPanduan {
    private int id;
    private String judul, deskripsi, image_path, upload_date;
    private int status;

    public ModelPanduan(int id, String judul, String deskripsi, String image_path, String upload_date, int status) {
        this.id = id;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.image_path = image_path;
        this.upload_date = upload_date;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getImage_path() {
        return image_path;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public int getStatus() {
        return status;
    }
}
