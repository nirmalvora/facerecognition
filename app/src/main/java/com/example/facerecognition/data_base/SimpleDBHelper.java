package com.example.facerecognition.data_base;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SimpleDBHelper extends SQLiteOpenHelper {


    private static final String DB_NAME = "face";

    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "face_embeddings";

    private static final String ID_COL = "id";

    private static final String NAME_COL = "name";

    private static final String EMBEDDINGS = "embeddings";

    public SimpleDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT,"
                + EMBEDDINGS + " TEXT)";

        db.execSQL(query);
    }

    public void addNewCourse(String name, String embeddings) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(NAME_COL, name);
        values.put(EMBEDDINGS, embeddings);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void addNewCourse(String name, float[] embeddings) {
        String jsonString = convertFloatArrayToString(embeddings) ;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME_COL, name);
        values.put(EMBEDDINGS, jsonString);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void getData() throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorCourses
                = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursorCourses.moveToFirst()) {
            do {
                // on below line we are adding the data from
                // cursor to our array list.
                String name = cursorCourses.getString(1);
                String embeddings = cursorCourses.getString(2);
                float[] data =convertStringToFloatArray(embeddings);
                Log.e("GetDataFromDB", "getData: " + name + "  " + embeddings +"  "+data);
            } while (cursorCourses.moveToNext());
            cursorCourses.close();
            db.close();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }



    public String convertFloatArrayToString(float[] floatArray) {
        Gson gson = new Gson();
        return gson.toJson(floatArray);
    }
    public float[] convertStringToFloatArray(String jsonArrayString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonArrayString, float[].class);
    }
}
