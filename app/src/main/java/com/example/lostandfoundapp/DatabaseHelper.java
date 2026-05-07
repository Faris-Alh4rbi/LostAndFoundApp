package com.example.lostandfoundapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lost_found_db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "adverts";

    private static final String COL_ID = "id";
    private static final String COL_POST_TYPE = "postType";
    private static final String COL_CATEGORY = "category";
    private static final String COL_NAME = "name";
    private static final String COL_PHONE = "phone";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_DATE = "date";
    private static final String COL_LOCATION = "location";
    private static final String COL_IMAGE_URI = "imageUri";
    private static final String COL_CREATED_AT = "createdAt";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_POST_TYPE + " TEXT, " +
                COL_CATEGORY + " TEXT, " +
                COL_NAME + " TEXT, " +
                COL_PHONE + " TEXT, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_LOCATION + " TEXT, " +
                COL_IMAGE_URI + " TEXT, " +
                COL_CREATED_AT + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertAdvert(String postType, String category, String name, String phone,
                                String description, String date, String location,
                                String imageUri, String createdAt) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_POST_TYPE, postType);
        values.put(COL_CATEGORY, category);
        values.put(COL_NAME, name);
        values.put(COL_PHONE, phone);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_DATE, date);
        values.put(COL_LOCATION, location);
        values.put(COL_IMAGE_URI, imageUri);
        values.put(COL_CREATED_AT, createdAt);

        long result = db.insert(TABLE_NAME, null, values);
        db.close();

        return result != -1;
    }

    public ArrayList<Advert> getAllAdverts() {

        ArrayList<Advert> advertList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_ID + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Advert advert = new Advert(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_POST_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URI)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CREATED_AT))
                );

                advertList.add(advert);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return advertList;
    }

    public ArrayList<Advert> getAdvertsByCategory(String category) {

        ArrayList<Advert> advertList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_CATEGORY + " = ? ORDER BY " + COL_ID + " DESC",
                new String[]{category}
        );

        if (cursor.moveToFirst()) {
            do {
                Advert advert = new Advert(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_POST_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URI)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CREATED_AT))
                );

                advertList.add(advert);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return advertList;
    }

    public Advert getAdvertById(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        Advert advert = null;

        if (cursor.moveToFirst()) {
            advert = new Advert(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_POST_TYPE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_CREATED_AT))
            );
        }

        cursor.close();
        db.close();

        return advert;
    }

    public boolean deleteAdvert(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{String.valueOf(id)});

        db.close();

        return result > 0;
    }
}