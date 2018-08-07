package com.example.android.bookpack;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookpack.books.DbContract.DbEntry;
import com.example.android.bookpack.books.DbHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.floatingActionButton)
     FloatingActionButton fab;
    @BindView(R.id.output)
     TextView out;
    @BindView(R.id.b_name)
     EditText editTextName;
    @BindView(R.id.b_price)
     EditText editTextPrice;
    @BindView(R.id.b_quantity)
      EditText editTextQuantity;
    @BindView(R.id.s_name)
       EditText editTextS_Name;
    @BindView(R.id.s_phone)
      EditText editTextS_Phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEmpty()) {
                    insertBook();
                    displayDatabaseInfo();
                } else {
                    Toast.makeText(MainActivity.this, "One or more input is missing ", Toast.LENGTH_SHORT).show();
                }
            }

        });
        displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        DbHelper bookDbHelper = new DbHelper(this);
        SQLiteDatabase database = bookDbHelper.getReadableDatabase();
        String[] projection = new String[]{
                DbEntry._ID,
                DbEntry.COLUMN_BOOK_NAME,
                DbEntry.COLUMN_BOOK_PRICE,
                DbEntry.COLUMN_BOOK_QUANTITY,
                DbEntry.COLUMN_BOOK_SUPPLIER_NAME,
                DbEntry.COLUMN_BOOK_SUPPLIER_PHONE,
        };
        Cursor cursor = database.query(
                DbEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        try {
            out.setText("The book table contains " + cursor.getCount() + " books. \n");
            out.append(DbEntry._ID + " | "
                    + DbEntry.COLUMN_BOOK_NAME + " | "
                    + DbEntry.COLUMN_BOOK_PRICE + " | "
                    + DbEntry.COLUMN_BOOK_QUANTITY + " | "
                    + DbEntry.COLUMN_BOOK_SUPPLIER_NAME + " | "
                    + DbEntry.COLUMN_BOOK_SUPPLIER_PHONE + "\n"
            );
            int idColumnIndex = cursor.getColumnIndex(DbEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(DbEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(DbEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(DbEntry.COLUMN_BOOK_QUANTITY);
            int s_nameColumnIndex = cursor.getColumnIndex(DbEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int s_phoneColumnIndex = cursor.getColumnIndex(DbEntry.COLUMN_BOOK_SUPPLIER_PHONE);
            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentBookName = cursor.getString(nameColumnIndex);
                int currentBookPrice = cursor.getInt(priceColumnIndex);
                int currentBookQuantity = cursor.getInt(quantityColumnIndex);
                String currentS_Name = cursor.getString(s_nameColumnIndex);
                int currentS_Phone = cursor.getInt(s_phoneColumnIndex);
                out.append(("\n" + currentID + " | "
                        + currentBookName + " | "
                        + currentBookPrice + " | "
                        + currentBookQuantity + " | "
                        + currentS_Name + " | "
                        + currentS_Phone));
            }
        } finally {
            cursor.close();
        }
    }

    private void insertBook() {
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String price = editTextPrice.getText().toString().trim();
        String quantity = editTextQuantity.getText().toString().trim();
        String phone = editTextS_Phone.getText().toString().trim();
        int convPrice = Integer.parseInt(price);
        int convQuantity = Integer.parseInt(quantity);
        int convPhone = Integer.parseInt(phone);
        ContentValues values = new ContentValues();
        values.put(DbEntry.COLUMN_BOOK_NAME, editTextName.getText().toString().trim());
        values.put(DbEntry.COLUMN_BOOK_PRICE, convPrice);
        values.put(DbEntry.COLUMN_BOOK_QUANTITY, convQuantity);
        values.put(DbEntry.COLUMN_BOOK_SUPPLIER_NAME, editTextS_Name.getText().toString().trim());
        values.put(DbEntry.COLUMN_BOOK_SUPPLIER_PHONE, convPhone);

        long RowID = database.insert(DbEntry.TABLE_NAME, null, values);
        if (RowID == -1) {
            Toast.makeText(this, "Error Saving Book", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Book Saved With Row Id" + RowID, Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isEmpty() {
        String checkName = editTextName.getText().toString().trim();
        String checkPrice = editTextPrice.getText().toString().trim();
        String checkQuantity = editTextQuantity.getText().toString().trim();
        String checkSName = editTextS_Name.getText().toString().trim();
        String checkSPhone = editTextS_Phone.getText().toString().trim();
        return TextUtils.isEmpty(checkName) || TextUtils.isEmpty(checkPrice) || TextUtils.isEmpty(checkQuantity)
                || TextUtils.isEmpty(checkSName) || TextUtils.isEmpty(checkSPhone);
    }

}
