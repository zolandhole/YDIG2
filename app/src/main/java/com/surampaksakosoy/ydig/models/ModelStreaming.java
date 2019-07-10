package com.surampaksakosoy.ydig.models;

public class ModelStreaming {
    private int id;
    private String pesan, waktu, jam, id_login;

    public ModelStreaming(int id, String pesan, String waktu, String jam, String id_login) {
        this.id = id;
        this.pesan = pesan;
        this.waktu = waktu;
        this.jam = jam;
        this.id_login = id_login;
    }

    public int getId() {
        return id;
    }

    public String getPesan() {
        return pesan;
    }

    public String getWaktu() {
        return waktu;
    }

    public String getJam() {
        return jam;
    }

    public String getId_login() {
        return id_login;
    }
}
