package com.example.android.bookpack.books;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

//Product Name, Price, Quantity, Supplier Name, and Supplier Phone Number
public final class DbContract {
    private DbContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.bookpack";
    public static final String PATH_BOOKS = "bookpack";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class DbEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;



        public static final String TABLE_NAME = "books";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_BOOK_NAME = "name";

        public static final String COLUMN_BOOK_PRICE = "price";

        public static final String COLUMN_BOOK_QUANTITY = "quantity";

        public static final String COLUMN_BOOK_SUPPLIER_NAME = "sname";

        public static final String COLUMN_BOOK_SUPPLIER_PHONE = "sphone";

    }
}
