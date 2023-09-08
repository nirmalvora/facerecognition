package com.example.facerecognition.data_base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.facerecognition.data_base.Contract.TableEntry;

/**
 * Class that manages the database creation and versioning.
 */
public class DbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DB_VERSION = 4;
    private static final String DB_NAME = "face.db";

    // SQL for table creation and deletion.
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TableEntry.TABLE_NAME + " (" +
                    TableEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TableEntry.COL_USER_NAME + " TEXT NOT NULL," +
                    TableEntry.COL_EMBEDDINGS + " TEXT)";
    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TableEntry.TABLE_NAME;

    /**
     * Public constructor for instantiating the DbHelper class with the database name and version.
     * @param context The context in which this method is called.
     */
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Override for the onCreate method.
     * This method calls SQL syntax for the database creation.
     * @param db The SQLite database to create.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    /**
     * Override for the onUpgrade method.
     * This method is used for the cases in which the database schema changes.
     * This method is also used to delete all the data from the current table.
     * @param db The SQLite database to upgrade.
     * @param oldVersion The version of the old database.
     * @param newVersion The version of the new database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Here we drop the current table and create a new one.
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }

    /**
     * Override for the onDowngrade method.
     * This method is used for the cases in which the database schema changes back to an older version.
     * @param db The SQLite database to downgrade.
     * @param oldVersion The version of the old database.
     * @param newVersion The version of the new database.
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Here we just call the onUpgrade method above because we do the same thing.
        onUpgrade(db, oldVersion, newVersion);
    }

}