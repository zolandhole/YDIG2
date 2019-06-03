package com.surampaksakosoy.ydig.dbpanduan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.surampaksakosoy.ydig.SplashScreenActivity;

import static android.provider.BaseColumns._ID;

public class DBKategori extends SQLiteOpenHelper {

    private static final String TAG = "DBKategori";
    private static final String DATABASE_NAME = "kategori.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;

    private final static String VERSI = "versi";
    private final static String PANDUAN_ID = "panduanid";
    private final static String JUDUL = "judul";
    private final static String IMAGE = "image";
    private final static String TABLE_NAME = "tabelkategori";

    public DBKategori(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_IMAGE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                VERSI + " TEXT, " +
                PANDUAN_ID + " TEXT, " +
                JUDUL + " TEXT, " +
                IMAGE + " BLOB NOT NULL " + " );";
        db.execSQL(SQL_CREATE_IMAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addToDb(String versi, String panduan_id, String judul, byte[] image){
        ContentValues cv = new ContentValues();
        cv.put(VERSI, versi);
        cv.put(PANDUAN_ID, panduan_id);
        cv.put(JUDUL, judul);
        cv.put(IMAGE, image);
        db.insert( TABLE_NAME, null, cv);
        Log.e(TAG, "addToDb: "+ versi);
    }

    public Cursor getData(String sql){
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    public void deleteDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.execSQL("DELETE FROM " + TABLE_NAME);
        Log.e(TAG, "deleteDB: Database terhapus");
    }

    public int countKategori(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + TABLE_NAME, null);
    }
}
