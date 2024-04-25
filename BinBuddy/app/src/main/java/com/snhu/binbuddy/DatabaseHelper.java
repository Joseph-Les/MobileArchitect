package com.snhu.binbuddy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// Helper class for managing database creation and version management.
public class DatabaseHelper extends SQLiteOpenHelper {
    // Database version and name constants.
    private static final String DATABASE_NAME = "UserManager.db";
    private static final int DATABASE_VERSION = 1;

    // Table names and column names constants.
    private static final String TABLE_USERS = "users";
    private static final String TABLE_ITEMS = "items";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_ITEM_ID = "item_id";
    public static final String COLUMN_ITEM_NAME = "name";
    public static final String COLUMN_ITEM_QUANTITY = "quantity";
    public static final String COLUMN_ITEM_DATE = "date";

    // Constructor to initialize the SQLiteOpenHelper.
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement for creating a new users table.
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_EMAIL + " TEXT UNIQUE,"
                + COLUMN_USER_PASSWORD + " TEXT)";
        db.execSQL(CREATE_USER_TABLE);

        // SQL statement for creating a new items table.
        String CREATE_ITEM_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ITEM_NAME + " TEXT,"
                + COLUMN_ITEM_QUANTITY + " INTEGER,"
                + COLUMN_ITEM_DATE + " TEXT)";
        db.execSQL(CREATE_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed and create fresh ones.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    // Method to add a new item into the items table.
    public void addItem(String name, int quantity, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, name);
        values.put(COLUMN_ITEM_QUANTITY, quantity);
        values.put(COLUMN_ITEM_DATE, date);
        long result = db.insert(TABLE_ITEMS, null, values);
        if (result == -1) {
            Log.d("DatabaseHelper", "Failed to add item");
        } else {
            Log.d("DatabaseHelper", "Item added successfully: " + name);
        }
        db.close();
    }

    // Method to fetch all items from the items table.
    public Cursor getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ITEMS, null);
    }

    // Method to update an existing item in the items table.
    public void updateItem(int itemId, String name, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, name);
        values.put(COLUMN_ITEM_QUANTITY, quantity);
        db.update(TABLE_ITEMS, values, COLUMN_ITEM_ID + " = ?", new String[] {String.valueOf(itemId)});
        db.close();
    }

    // Method to delete an item from the items table.
    public void deleteItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COLUMN_ITEM_ID + " = ?", new String[] {String.valueOf(itemId)});
        db.close();
    }

    // Method to check if a user exists in the users table with the given email and password.
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_EMAIL + " = ? AND " + COLUMN_USER_PASSWORD + " = ?", new String[] {email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Method to add a new user into the users table.
    public void addUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, password);
        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    // Method to fetch a single item by its ID from the items table.
    public Cursor getItemById(int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Execute a SQL query to get the item by its ID.
        return db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + COLUMN_ITEM_ID + " = ?", new String[] {String.valueOf(itemId)});
    }

    // Method to check if a user exists in the users table by email.
    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Query the users table to see if a row exists with the specified email.
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_EMAIL + " = ?", new String[] {email});
        boolean exists = cursor.moveToFirst(); // Returns true if a row is found (i.e., user exists).
        cursor.close();
        db.close();
        return exists;
    }

    // Method to delete an item by its name from the items table.
    public void deleteItemByName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Execute a SQL command to delete the item by its name.
        db.delete(TABLE_ITEMS, COLUMN_ITEM_NAME + " = ?", new String[]{name});
        db.close();
    }

    // Method to update an item's name and quantity by its old name.
    public void updateItemByName(String oldName, String newName, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, newName);
        values.put(COLUMN_ITEM_QUANTITY, quantity);
        // Update the items table where the old name matches.
        db.update(TABLE_ITEMS, values, COLUMN_ITEM_NAME + " = ?", new String[]{oldName});
        db.close();
    }

    // Method to fetch an item by its name and return as an Item object.
    public ItemsAdapter.Item getItemByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Query to select an item by its name.
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + COLUMN_ITEM_NAME + " = ?", new String[]{name});
        if (cursor != null && cursor.moveToFirst()) {
            // Extract the item quantity from the cursor.
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_QUANTITY));
            cursor.close();
            return new ItemsAdapter.Item(name, quantity);
        }
        if (cursor != null) cursor.close();
        return null; // Return null if no item is found with the specified name.
    }
}
