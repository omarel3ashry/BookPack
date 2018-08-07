package com.example.android.bookpack.books;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.bookpack.books.DbContract.DbEntry;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shelf.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_TABLE_BOOKS = "CREATE TABLE " + DbEntry.TABLE_NAME + " ("
                + DbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DbEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
                + DbEntry.COLUMN_BOOK_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + DbEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL, "
                + DbEntry.COLUMN_BOOK_SUPPLIER_NAME + " TEXT, "
                + DbEntry.COLUMN_BOOK_SUPPLIER_PHONE + " TEXT);";

        Log.i("CREATE", SQL_CREATE_TABLE_BOOKS);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_BOOKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
