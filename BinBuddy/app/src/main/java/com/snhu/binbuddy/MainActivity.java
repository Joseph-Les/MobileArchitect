package com.snhu.binbuddy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import android.text.InputType;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_SEND_SMS = 123; // Constant for SMS permission request code
    private DatabaseHelper databaseHelper; // Database helper for interacting with the database
    private ItemsAdapter itemsAdapter; // Adapter for handling item display and interaction in RecyclerView
    private String selectedItemName = ""; // Variable to keep track of the selected item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        List<ItemsAdapter.Item> items = fetchItemsFromDatabase(); // Fetch items from database

        itemsAdapter = new ItemsAdapter(this, items, this::onItemClicked);
        RecyclerView itemsRecyclerView = findViewById(R.id.itemsRecyclerView);
        itemsRecyclerView.setAdapter(itemsAdapter);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button addButton = findViewById(R.id.addButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        Button updateButton = findViewById(R.id.updateButton);

        addButton.setOnClickListener(v -> showAddItemDialog()); // Show dialog to add a new item
        deleteButton.setOnClickListener(v -> {
            if (!selectedItemName.isEmpty()) {
                showDeleteItemDialog(selectedItemName); // Show delete confirmation dialog
            } else {
                Toast.makeText(MainActivity.this, "No item selected", Toast.LENGTH_SHORT).show();
            }
        });

        updateButton.setOnClickListener(v -> {
            if (!selectedItemName.isEmpty()) {
                showUpdateItemDialog(selectedItemName);
            } else {
                Toast.makeText(MainActivity.this, "No item selected", Toast.LENGTH_SHORT).show();
            }
        });

        // Check and request SMS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can send SMS
                sendSMSMessage();
            } else {
                // Permission denied, handle the case where SMS functionality is not available
                Toast.makeText(this, "SMS permission denied, can't send SMS alerts.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void sendSMSMessage() {
        // The number would dynamically determined
        String phoneNo = "1234567890";
        String message = "Low inventory alert!";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void onItemClicked(ItemsAdapter.Item item) {
        // Update the selected item name when an item is clicked
        selectedItemName = item.getName();
        Toast.makeText(this, "Item selected: " + item.getName(), Toast.LENGTH_SHORT).show();
    }

    private void showAddItemDialog() {
        // Dialog for adding a new item
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add New Item");

        final View customLayout = getLayoutInflater().inflate(R.layout.add_item_dialog, null);
        builder.setView(customLayout);

        EditText editTextName = customLayout.findViewById(R.id.editTextItemName);
        EditText editTextQuantity = customLayout.findViewById(R.id.editTextItemQuantity);
        editTextQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setPositiveButton("Add", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            int quantity;
            try {
                quantity = Integer.parseInt(editTextQuantity.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "Please enter a valid number for quantity", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!name.isEmpty() && quantity > 0) {
                addItemToDatabase(name, quantity);
                dialog.dismiss(); // Close dialog only if everything is correct
            } else {
                Toast.makeText(MainActivity.this, "Name cannot be empty and quantity must be greater than zero", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteItemDialog(String itemName) {
        // Confirmation dialog for deleting an item
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Item");
        builder.setMessage("Are you sure you want to delete " + itemName + "?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            deleteItemFromDatabase(itemName);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteItemFromDatabase(String itemName) {
        // Delete an item from the database and refresh the item list
        databaseHelper.deleteItemByName(itemName);
        Toast.makeText(this, itemName + " deleted successfully", Toast.LENGTH_SHORT).show();
        refreshItemsList();
    }

    private void showUpdateItemDialog(String itemName) {
        // Dialog for updating an item
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Item");

        final View customLayout = getLayoutInflater().inflate(R.layout.update_item_dialog, null);
        builder.setView(customLayout);

        EditText editTextName = customLayout.findViewById(R.id.editTextItemName);
        EditText editTextQuantity = customLayout.findViewById(R.id.editTextItemQuantity);

        ItemsAdapter.Item item = databaseHelper.getItemByName(itemName);
        if (item != null) {
            editTextName.setText(item.getName());
            editTextQuantity.setText(String.valueOf(item.getQuantity()));
        }

        builder.setPositiveButton("Update", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String newName = editTextName.getText().toString().trim();
            int quantity;
            try {
                quantity = Integer.parseInt(editTextQuantity.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "Please enter a valid number for quantity", Toast.LENGTH_SHORT).show();
                return;
            }
            updateItemInDatabase(itemName, newName, quantity);
            dialog.dismiss();
        });
    }

    private void updateItemInDatabase(String oldName, String newName, int quantity) {
        // Update an item in the database and refresh the item list
        databaseHelper.updateItemByName(oldName, newName, quantity);
        Toast.makeText(this, "Item updated successfully!", Toast.LENGTH_SHORT).show();
        refreshItemsList();
    }

    private void addItemToDatabase(String name, int quantity) {
        // Add a new item to the database and refresh the item list
        databaseHelper.addItem(name, quantity, "Today's Date"); // Example date
        Toast.makeText(this, "Item added successfully!", Toast.LENGTH_SHORT).show();
        refreshItemsList();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshItemsList() {
        // Refresh the list of items in the RecyclerView
        List<ItemsAdapter.Item> newItems = fetchItemsFromDatabase();
        itemsAdapter.updateItems(newItems);
        itemsAdapter.notifyDataSetChanged();
    }

    private List<ItemsAdapter.Item> fetchItemsFromDatabase() {
        // Fetch items from the database and convert them into a list of Item objects
        List<ItemsAdapter.Item> items = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllItems();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_NAME));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ITEM_QUANTITY));
                items.add(new ItemsAdapter.Item(name, quantity));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return items;
    }

    private void onItemClicked(Item item) {
        // Update the selected item name when an item is clicked
        selectedItemName = item.getName();
        Toast.makeText(this, "Item selected: " + item.getName(), Toast.LENGTH_SHORT).show();
    }
}
