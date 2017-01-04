package br.com.accerti.hypergaragesale.Control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.com.accerti.hypergaragesale.Model.Posts;

/**
 * Created by thiagodosreis on 8/23/16.
 * Add PostsDbHelper.java to provide interface with database operations
 */
public class PostsDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Posts.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Posts.PostEntry.TABLE_NAME + " (" +
                    Posts.PostEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    Posts.PostEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    Posts.PostEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    Posts.PostEntry.COLUMN_NAME_PRICE + TEXT_TYPE + COMMA_SEP +
                    Posts.PostEntry.COLUMN_NAME_PICTURE + TEXT_TYPE + COMMA_SEP +
                    Posts.PostEntry.COLUMN_NAME_LATITUDE + TEXT_TYPE + COMMA_SEP +
                    Posts.PostEntry.COLUMN_NAME_LONGITUDE + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Posts.PostEntry.TABLE_NAME;

    public PostsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
