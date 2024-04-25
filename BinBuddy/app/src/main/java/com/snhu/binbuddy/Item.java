package com.snhu.binbuddy;

// Class to represent an item with a name and quantity
public class Item {
    private final String name; // Name of the item
    private final int quantity; // Quantity of the item

    // Constructor to initialize an item with name and quantity
    public Item(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    // Method to get the name of the item
    public String getName() {
        return name;
    }

    // Method to get the quantity of the item
    public int getQuantity() {
        return quantity;
    }
}
