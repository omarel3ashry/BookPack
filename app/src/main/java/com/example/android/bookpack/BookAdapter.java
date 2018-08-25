package com.example.android.bookpack;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookpack.books.DbContract.DbEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookAdapter extends CursorAdapter {

    @BindView(R.id.book_name)
    TextView bookName;
    @BindView(R.id.book_price)
    TextView bookPrice;
    @BindView(R.id.book_quantity)
    TextView bookQuantity;
    @BindView(R.id.btn_sell)
    Button buttonSell;
    String quantityString;


    public BookAdapter(Context context, Cursor c) {
        super(context, c, 0);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ButterKnife.bind(this, view);
        int nameColumn = cursor.getColumnIndex(DbEntry.COLUMN_BOOK_NAME);
        int priceColumn = cursor.getColumnIndex(DbEntry.COLUMN_BOOK_PRICE);
        int quantityColumn = cursor.getColumnIndex(DbEntry.COLUMN_BOOK_QUANTITY);
        final int rowID = cursor.getColumnIndex(DbEntry._ID);
        final int currentRowID = cursor.getInt(rowID);
        String nameString = cursor.getString(nameColumn);
        String priceString = cursor.getString(priceColumn);
        quantityString = cursor.getString(quantityColumn);
        bookName.setText(nameString);
        bookPrice.setText(priceString);
        bookQuantity.setText(quantityString);
        final int quantity = Integer.parseInt(quantityString);
        buttonSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Uri uri = ContentUris.withAppendedId(DbEntry.CONTENT_URI, currentRowID);
                adjustBookQuantity(context, uri, quantity);
//                    quantityString = String.valueOf(quantity);
//                    bookQuantity.setText(quantityString);

            }
        });
    }

    private void adjustBookQuantity(Context context, Uri currentUri, int currentQuantity) {
        if (currentQuantity > 0) {
            currentQuantity -= 1;
            ContentValues values = new ContentValues();
            values.put(DbEntry.COLUMN_BOOK_QUANTITY, currentQuantity);
            int updatedRow = context.getContentResolver().update(currentUri, values, null, null);
        } else {
            Toast.makeText(context, "Can't sell this not available for now", Toast.LENGTH_SHORT).show();
        }
    }
}
