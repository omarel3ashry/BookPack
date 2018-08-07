package com.example.android.bookpack.books;

import android.provider.BaseColumns;

//Product Name, Price, Quantity, Supplier Name, and Supplier Phone Number
public final class DbContract {
    private DbContract() { }

    public static final class DbEntry implements BaseColumns {

        public static final String TABLE_NAME = "books";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_BOOK_NAME = "name";

        public static final String COLUMN_BOOK_PRICE = "price";

        public static final String COLUMN_BOOK_QUANTITY = "quantity";

        public static final String COLUMN_BOOK_SUPPLIER_NAME = "sname";

        public static final String COLUMN_BOOK_SUPPLIER_PHONE = "sphone";

    }
}
