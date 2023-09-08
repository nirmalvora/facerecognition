package com.example.facerecognition.data_base;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Static class that defines the table and columns names for the database.
 */
public final class Contract {

    private Contract() {}

    // The CONTENT_AUTHORITY is a name for the entire content provider, similar to the relationship
    // between a domain name and its website. A convenient string to use for the content authority
    // is the package name for the app, which is guaranteed to be unique on the device.
    public static final String CONTENT_AUTHORITY = "com.example.facerecognition";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // The path (table name) appended to the base content URI.
    public static final String PATH_TABLE = "products";

    /**
     * Inner class that defines constant values for the inventory database table.
     * Each entry in the table represents a single product.
     */
    public static abstract class TableEntry implements BaseColumns {

        // The content URI to access the products data in the provider.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TABLE);

        // The MIME type of the CONTENT_URI for a list of products.
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TABLE;

        // The MIME type of the CONTENT_URI for a single product.
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TABLE;

        // The name of the table.
        public static final String TABLE_NAME = "face_embedding";

        // The columns names for the table.
        public static final String _ID = BaseColumns._ID;
        public static final String COL_USER_NAME = "user_name";
        public static final String COL_EMBEDDINGS = "embeddings";

    }

}