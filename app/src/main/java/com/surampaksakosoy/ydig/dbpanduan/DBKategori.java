package com.surampaksakosoy.ydig.dbpanduan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.provider.BaseColumns._ID;

public class DBKategori extends SQLiteOpenHelper {

    private static final String TAG = "DBKategori";
    private static final String DATABASE_NAME = "kategori.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;

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

    public void addToDb(String panduan_id, String judul, byte[] image){
        ContentValues cv = new ContentValues();
        cv.put(PANDUAN_ID, panduan_id);
        cv.put(JUDUL, judul);
        cv.put(IMAGE, image);
        db.insert( TABLE_NAME, null, cv);
        Log.e(TAG, "addToDb: BERHASIL");
    }

    public Cursor getData(String sql){
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(sql, null);
    }
}
