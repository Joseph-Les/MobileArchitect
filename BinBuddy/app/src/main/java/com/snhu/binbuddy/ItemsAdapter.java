package com.snhu.binbuddy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {
    private final Context mContext; // Context to access application-specific resources
    private List<Item> mItems; // List of item data
    private final OnItemClickListener mListener; // Listener for item click events

    // Constructor for initializing adapter
    public ItemsAdapter(Context context, List<Item> items, OnItemClickListener listener) {
        mContext = context;
        mItems = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        // Bind item data to the view
        Item item = mItems.get(position);
        holder.nameText.setText(item.getName());
        holder.quantityText.setText(String.format("Quantity: %d", item.getQuantity()));
        holder.itemView.setOnClickListener(v -> mListener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        // Return the size of the item list
        return mItems.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateItems(List<Item> newItems) {
        // Update the list of items and notify the adapter
        mItems = newItems;
        notifyDataSetChanged();
    }

    // ViewHolder class for recycling views
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameText; // Text view for item name
        TextView quantityText; // Text view for item quantity

        public ItemViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.text_view_item_name);
            quantityText = itemView.findViewById(R.id.text_view_item_quantity);
        }
    }

    // Interface to handle click events on items
    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    // Inner class for representing item data
    public static class Item {
        private final String name; // Name of the item
        private final int quantity; // Quantity of the item

        public Item(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
