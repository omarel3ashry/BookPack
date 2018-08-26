package com.example.android.bookpack;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.bookpack.books.DbContract.DbEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    @BindView(R.id.b_name)
    EditText editTextName;
    @BindView(R.id.b_price)
    EditText editTextPrice;
    @BindView(R.id.b_quantity)
    EditText editTextQuantity;
    @BindView(R.id.minus_quantity)
    ImageButton minusImageBtn;
    @BindView(R.id.plus_quantity)
    ImageButton plusImageBtn;
    @BindView(R.id.edit_rate)
    EditText editTextRate;
    @BindView(R.id.s_name)
    EditText editTextS_Name;
    @BindView(R.id.s_phone)
    EditText editTextS_Phone;
    @BindView(R.id.call_provider)
    Button callProvider;
    private Uri bookCurrentUri;
    private static final int EXISTING_BOOK_LOADER = 1;
    String phone;
    int currentQuantity;
    private boolean booksHasChanged = false;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            booksHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);
        bookCurrentUri = getIntent().getData();

        editTextName.setOnTouchListener(touchListener);
        editTextPrice.setOnTouchListener(touchListener);
        editTextQuantity.setOnTouchListener(touchListener);
        editTextS_Name.setOnTouchListener(touchListener);
        editTextS_Phone.setOnTouchListener(touchListener);
        editTextRate.setOnTouchListener(touchListener);
        plusImageBtn.setOnTouchListener(touchListener);
        minusImageBtn.setOnTouchListener(touchListener);

        if (bookCurrentUri == null) {
            setTitle("Add Book");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Book");
            getSupportLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }
        callProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone = editTextS_Phone.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)) {
                    Uri uri = Uri.parse("tel:" + phone);
                    Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(EditorActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        minusImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adjustMinusQuantity(EditorActivity.this);
            }
        });
        plusImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adjustPlusQuantity(EditorActivity.this);
            }
        });

    }

    private void adjustPlusQuantity(Context context) {
        Integer rate;
        if (!TextUtils.isEmpty(editTextRate.getText().toString().trim())) {
            rate = Integer.parseInt(editTextRate.getText().toString().trim());
        } else {
            editTextRate.setText("1");
            rate = Integer.parseInt(editTextRate.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(editTextQuantity.getText().toString())) {
            currentQuantity = Integer.parseInt(editTextQuantity.getText().toString().trim());
        } else {
            editTextQuantity.setText("0");
            currentQuantity = Integer.parseInt(editTextQuantity.getText().toString().trim());
        }
        if (currentQuantity >= 0 && rate > 0) {
            currentQuantity += rate;
            editTextQuantity.setText(String.valueOf(currentQuantity));
        } else {
            Toast.makeText(context, "Can't adjust quantity !", Toast.LENGTH_SHORT).show();
        }
    }

    private void adjustMinusQuantity(Context context) {
        Integer rate;
        if (!TextUtils.isEmpty(editTextRate.getText().toString().trim())) {
            rate = Integer.parseInt(editTextRate.getText().toString().trim());
        } else {
            editTextRate.setText("1");
            rate = Integer.parseInt(editTextRate.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(editTextQuantity.getText().toString())) {
            currentQuantity = Integer.parseInt(editTextQuantity.getText().toString().trim());
        } else {
            editTextQuantity.setText("0");
            currentQuantity = Integer.parseInt(editTextQuantity.getText().toString().trim());
        }
        if (currentQuantity > 0 && rate > 0 && rate < currentQuantity) {
            currentQuantity -= rate;
            editTextQuantity.setText(String.valueOf(currentQuantity));
        } else {
            Toast.makeText(context, "Can't decrease quantity anymore !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                insertOrSaveBook();
                return true;
            case R.id.action_delete_current:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!booksHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (bookCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_current);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteCurrent() {
        int rowDeleted = getContentResolver().delete(bookCurrentUri, null, null);
        if (rowDeleted == -1) {
            Toast.makeText(this, "Error Deleting Book", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Book Deleted Successfully", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public void insertOrSaveBook() {
        String price = editTextPrice.getText().toString().trim();
        String quantity = editTextQuantity.getText().toString().trim();
        phone = editTextS_Phone.getText().toString().trim();
        if (bookCurrentUri == null && allIsEmpty()) {
            finish();
            return;

        }
        if (TextUtils.isEmpty(editTextName.getText()) || TextUtils.isEmpty(editTextPrice.getText()) ||
                TextUtils.isEmpty(editTextQuantity.getText()) || TextUtils.isEmpty(editTextS_Name.getText()) ||
                TextUtils.isEmpty(editTextS_Phone.getText())) {
            Toast.makeText(this, "One or more input are missing !!", Toast.LENGTH_SHORT).show();
            return;
        }
        int convPrice = 0;
        if (!TextUtils.isEmpty(price)) {
            convPrice = Integer.parseInt(price);
        }
        int convQuantity = 0;
        if (!TextUtils.isEmpty(quantity)) {
            convQuantity = Integer.parseInt(quantity);
        }


        ContentValues values = new ContentValues();
        values.put(DbEntry.COLUMN_BOOK_NAME, editTextName.getText().toString().trim());
        values.put(DbEntry.COLUMN_BOOK_PRICE, convPrice);
        values.put(DbEntry.COLUMN_BOOK_QUANTITY, convQuantity);
        values.put(DbEntry.COLUMN_BOOK_SUPPLIER_NAME, editTextS_Name.getText().toString().trim());
        values.put(DbEntry.COLUMN_BOOK_SUPPLIER_PHONE, phone);

        if (bookCurrentUri == null) {

            if (!isEmpty()) {
                Uri newUri = getContentResolver().insert(DbEntry.CONTENT_URI, values);
                if (newUri == null) {
                    Toast.makeText(this, "Error Inserting Book", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Book Inserted Successfully", Toast.LENGTH_SHORT).show();
                }
                finish();
            }

        } else {
            int updatedRow = getContentResolver().update(bookCurrentUri, values, null, null);
            if (updatedRow == -1) {
                Toast.makeText(this, "Failed to update the selected row", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Row updated successfully", Toast.LENGTH_SHORT).show();
            }
            finish();
        }


    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteCurrent();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!booksHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
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

    private boolean allIsEmpty() {
        String checkName = editTextName.getText().toString().trim();
        String checkPrice = editTextPrice.getText().toString().trim();
        String checkQuantity = editTextQuantity.getText().toString().trim();
        String checkSName = editTextS_Name.getText().toString().trim();
        String checkSPhone = editTextS_Phone.getText().toString().trim();
        return TextUtils.isEmpty(checkName) && TextUtils.isEmpty(checkPrice) && TextUtils.isEmpty(checkQuantity)
                && TextUtils.isEmpty(checkSName) && TextUtils.isEmpty(checkSPhone);
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {DbEntry._ID, DbEntry.COLUMN_BOOK_NAME,
                DbEntry.COLUMN_BOOK_PRICE, DbEntry.COLUMN_BOOK_QUANTITY,
                DbEntry.COLUMN_BOOK_SUPPLIER_NAME,
                DbEntry.COLUMN_BOOK_SUPPLIER_PHONE};
        return new CursorLoader(this, bookCurrentUri,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(DbEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(DbEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(DbEntry.COLUMN_BOOK_QUANTITY);
            int sNameColumnIndex = cursor.getColumnIndex(DbEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int sPhoneColumnIndex = cursor.getColumnIndex(DbEntry.COLUMN_BOOK_SUPPLIER_PHONE);

            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String sName = cursor.getString(sNameColumnIndex);
            String sPhone = cursor.getString(sPhoneColumnIndex);

            editTextName.setText(name);
            editTextPrice.setText(String.valueOf(price));
            editTextQuantity.setText(String.valueOf(quantity));
            editTextS_Name.setText(sName);
            editTextS_Phone.setText(sPhone);

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        editTextName.setText("");
        editTextPrice.setText("");
        editTextQuantity.setText("");
        editTextS_Name.setText("");
        editTextS_Phone.setText("");
    }
}
