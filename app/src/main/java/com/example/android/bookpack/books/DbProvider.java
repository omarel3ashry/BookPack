package com.example.android.bookpack.books;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.android.bookpack.R;
import com.example.android.bookpack.books.DbContract.DbEntry;

public class DbProvider extends ContentProvider {
    private DbHelper dbHelper;
    private static final int BOOKS = 10;
    private static final int BOOK_ID = 11;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_BOOKS, BOOKS);
        uriMatcher.addURI(DbContract.CONTENT_AUTHORITY, DbContract.PATH_BOOKS + "/#", BOOK_ID);
    }


    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;
        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(
                        DbEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = DbEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(
                        DbEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return DbEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return DbEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException(String.valueOf(R.string.insertion_not_supp) + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String bookName = values.getAsString(DbEntry.COLUMN_BOOK_NAME);
        Integer bookPrice = values.getAsInteger(DbEntry.COLUMN_BOOK_PRICE);
        Integer bookQuantity = values.getAsInteger(DbEntry.COLUMN_BOOK_QUANTITY);
        String bookSName = values.getAsString(DbEntry.COLUMN_BOOK_SUPPLIER_NAME);
        String bookSPhone = values.getAsString(DbEntry.COLUMN_BOOK_SUPPLIER_PHONE);
        if (bookName == null) {
            throw new IllegalArgumentException(String.valueOf(R.string.book_requires_name));
        } else if (bookPrice == null || bookPrice < 0) {
            throw new IllegalArgumentException(String.valueOf(R.string.book_requires_valid_price));
        } else if (bookQuantity == null || bookQuantity < 0) {
            throw new IllegalArgumentException(String.valueOf(R.string.book_requires_valid_quantity));
        } else if (bookSName == null) {
            throw new IllegalArgumentException(String.valueOf(R.string.book_requires_valid_s_name));
        } else if (bookSPhone == null) {
            throw new IllegalArgumentException(String.valueOf(R.string.book_requires_valid_s_phone));
        }
        long newRowId = database.insert(DbEntry.TABLE_NAME, null, values);
        if (newRowId == -1) {
            Toast.makeText(getContext(), "Error Saving Book", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Book Saved With Row Id" + newRowId, Toast.LENGTH_SHORT).show();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, newRowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case BOOKS:
                rowsDeleted = database.delete(DbEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = DbEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(DbEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(R.string.delete_not_supp) + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, values, selection, selectionArgs);
            case BOOK_ID:
                selection = DbEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(String.valueOf(R.string.update_not_supp) + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        if (values.containsKey(DbEntry.COLUMN_BOOK_NAME)) {
            String bookName = values.getAsString(DbEntry.COLUMN_BOOK_NAME);
            if (bookName == null) {
                throw new IllegalArgumentException(String.valueOf(R.string.book_requires_name));
            }
        }
        if (values.containsKey(DbEntry.COLUMN_BOOK_PRICE)) {
            Integer bookPrice = values.getAsInteger(DbEntry.COLUMN_BOOK_PRICE);
            if (bookPrice == null || bookPrice < 0) {
                throw new IllegalArgumentException(String.valueOf(R.string.book_requires_valid_price));
            }
        }
        if (values.containsKey(DbEntry.COLUMN_BOOK_QUANTITY)) {
            Integer bookQuantity = values.getAsInteger(DbEntry.COLUMN_BOOK_QUANTITY);
            if (bookQuantity == null || bookQuantity < 0) {

                throw new IllegalArgumentException(String.valueOf(R.string.book_requires_valid_quantity));
            }
        }
        if (values.containsKey(DbEntry.COLUMN_BOOK_SUPPLIER_NAME)) {
            String bookSName = values.getAsString(DbEntry.COLUMN_BOOK_SUPPLIER_NAME);
            if (bookSName == null) {
                throw new IllegalArgumentException(String.valueOf(R.string.book_requires_valid_s_name));
            }
        }
        if (values.containsKey(DbEntry.COLUMN_BOOK_SUPPLIER_PHONE)) {
            String bookSPhone = values.getAsString(DbEntry.COLUMN_BOOK_SUPPLIER_PHONE);
            if (bookSPhone == null) {
                throw new IllegalArgumentException(String.valueOf(R.string.book_requires_valid_s_phone));
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        int rowsUpdated = database.update(DbEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
