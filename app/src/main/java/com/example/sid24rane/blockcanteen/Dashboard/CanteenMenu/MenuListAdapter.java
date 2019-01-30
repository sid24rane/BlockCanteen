package com.example.sid24rane.blockcanteen.Dashboard.CanteenMenu;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.sid24rane.blockcanteen.R;

import java.util.ArrayList;

public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.ViewHolder> implements Filterable {

    private ArrayList<MenuItemModel> menuItemArrayList;

    public MenuListAdapter(ArrayList<MenuItemModel> allitems) {
        this.menuItemArrayList = allitems;
    }

    @NonNull
    @Override
    public MenuListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menuitem, parent, false);
        return new ViewHolder(mview);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuListAdapter.ViewHolder holder, int position) {
        MenuItemModel menuItem = menuItemArrayList.get(position);
        holder.id.setText(String.valueOf(menuItem.getId()));
        holder.name.setText(menuItem.getName());
        holder.price.setText(String.valueOf(menuItem.getPrice()));
    }

    @Override
    public int getItemCount() {
        return menuItemArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                ArrayList<MenuItemModel> filteredList = new ArrayList<>();

                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    filteredList = menuItemArrayList;
                } else {
                    filteredList = getFilteredResults(charString);
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                menuItemArrayList = (ArrayList<MenuItemModel>) filterResults.values;
                Log.d("filtered-values",menuItemArrayList.toString());
                MenuListAdapter.this.notifyDataSetChanged();
            }
        };
    }

    protected ArrayList<MenuItemModel> getFilteredResults(String query){

        ArrayList<MenuItemModel> results = new ArrayList<>();
        for (MenuItemModel menuItem : menuItemArrayList) {

            String w = menuItem.getName().toString().toLowerCase();

            if (w.startsWith(query)){
                results.add(menuItem);
            }
        }
        return results;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView price;
        public TextView id;
        public Button buy;

        public ViewHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.name);
            this.id = (TextView) view.findViewById(R.id.id);
            this.price = (TextView) view.findViewById(R.id.price);
            this.buy = (Button) view.findViewById(R.id.buy);
        }
    }
}
