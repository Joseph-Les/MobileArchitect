package com.snhu.binbuddy;

import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;

public class ItemsActivity extends AppCompatActivity {
    private EditText editTextItemName; // Input field for item name
    private EditText editTextItemQuantity; // Input field for item quantity
    private ItemsAdapter itemsAdapter; // Adapter for managing item views in the RecyclerView
    private DatabaseHelper databaseHelper; // Helper for database operations
    private int selectedItemId = -1;  // ID of the selected item for edit or delete

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        // Initialization of database helper and UI elements
        databaseHelper = new DatabaseHelper(this);
        editTextItemName = findViewById(R.id.editTextItemName);
        editTextItemQuantity = findViewById(R.id.editTextItemQuantity);
        Button addButton = findViewById(R.id.addButton);
        Button updateButton = findViewById(R.id.updateButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        RecyclerView itemsRecyclerView = findViewById(R.id.itemsRecyclerView);

        // Setup initial list of items
        initializeItemList(itemsRecyclerView);

        // Set click listeners for buttons
        addButton.setOnClickListener(view -> addItem());
        updateButton.setOnClickListener(view -> updateItem());
        deleteButton.setOnClickListener(view -> deleteItem());
    }

    private void initializeItemList(RecyclerView recyclerView) {
        // Load items from the database and set up the RecyclerView
        Cursor itemsCursor = databaseHelper.getAllItems();
        List<ItemsAdapter.Item> items = cursorToItemList(itemsCursor);
        itemsAdapter = new ItemsAdapter(this, items, this::onItemClicked);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(itemsAdapter);
    }

    private void onItemClicked(ItemsAdapter.Item item) {
        // Handle click events on items
        Toast.makeText(this, "Clicked on: " + item.getName(), Toast.LENGTH_SHORT).show();
    }

    private List<ItemsAdapter.Item> cursorToItemList(Cursor cursor) {
        // Convert cursor data to a list of Item objects
        List<ItemsAdapter.Item> items = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int nameIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_NAME);
                int quantityIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_QUANTITY);
                String name = cursor.getString(nameIndex);
                int quantity = cursor.getInt(quantityIndex);
                items.add(new ItemsAdapter.Item(name, quantity));
                Log.d("ItemsActivity", "Loaded item: " + name + ", Quantity: " + quantity);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Log.d("ItemsActivity", "Cursor is empty or null");
        }
        return items;
    }

    private void addItem() {
        // Add a new item to the database
        String name = editTextItemName.getText().toString().trim();
        int quantity = Integer.parseInt(editTextItemQuantity.getText().toString().trim());
        databaseHelper.addItem(name, quantity, "");
        refreshItems();
        editTextItemName.setText("");  // Clear input fields after adding
        editTextItemQuantity.setText("");
    }

    private void updateItem() {
        // Update an existing item in the database
        if (selectedItemId == -1) {
            Toast.makeText(this, "Please select an item to update", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = editTextItemName.getText().toString().trim();
        int quantity = Integer.parseInt(editTextItemQuantity.getText().toString().trim());
        databaseHelper.updateItem(selectedItemId, name, quantity);
        refreshItems();
        editTextItemName.setText("");  // Clear input fields after updating
        editTextItemQuantity.setText("");
        selectedItemId = -1;  // Reset selected item ID
    }

    private void deleteItem() {
        // Delete an item from the database
        if (selectedItemId == -1) {
            Toast.makeText(this, "Please select an item to delete", Toast.LENGTH_SHORT).show();
            return;
        }
        databaseHelper.deleteItem(selectedItemId);
        refreshItems();
        Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
        selectedItemId = -1;  // Reset selected item ID
    }

    private void refreshItems() {
        // Refresh the list of items displayed in RecyclerView
        Cursor newCursor = databaseHelper.getAllItems();
        List<ItemsAdapter.Item> newItems = cursorToItemList(newCursor);
        itemsAdapter = new ItemsAdapter(this, newItems, this::onItemClicked);
        RecyclerView itemsRecyclerView = findViewById(R.id.itemsRecyclerView);
        itemsRecyclerView.setAdapter(itemsAdapter);
        Log.d("ItemsActivity", "Adapter refreshed with " + newItems.size() + " items.");
    }

    private void onItemClicked(ClipData.Item item) {
    }

    public void addItem(View view) {
        // Fetch the input from EditText fields
        String itemName = editTextItemName.getText().toString().trim();
        String itemQuantityString = editTextItemQuantity.getText().toString().trim();

        // Check if the inputs are valid
        if (itemName.isEmpty() || itemQuantityString.isEmpty()) {
            Toast.makeText(this, "Name and quantity cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        int itemQuantity;
        try {
            itemQuantity = Integer.parseInt(itemQuantityString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number for quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use the DatabaseHelper to add the item to the database
        databaseHelper.addItem(itemName, itemQuantity, "");

        // Refresh the list to show the new item
        refreshItems();

        // Optionally clear the input fields
        editTextItemName.setText("");
        editTextItemQuantity.setText("");

        Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show();
    }
}
