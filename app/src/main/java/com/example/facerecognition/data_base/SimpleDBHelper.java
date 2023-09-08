package com.example.facerecognition.data_base;



import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
