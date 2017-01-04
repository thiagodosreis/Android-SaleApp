package br.com.accerti.hypergaragesale.Model;

import android.provider.BaseColumns;

/**
 * Created by thiagodosreis on 8/23/16.
 * Add Posts.java helper class as 'contract class'
 */
public class Posts {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public Posts() {}

    /* Inner class that defines the table contents */
    public static abstract class PostEntry implements BaseColumns {
        public static final String TABLE_NAME = "posts";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_PICTURE = "picture";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
    }
}
