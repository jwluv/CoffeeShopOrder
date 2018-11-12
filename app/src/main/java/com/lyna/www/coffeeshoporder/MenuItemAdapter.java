package com.lyna.www.coffeeshoporder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MyViewHolder> {

    ArrayList<HashMap<String,Object>> arrayList = null;
    public MenuItemAdapter(ArrayList<HashMap<String,Object>> arrayList) {
        this.arrayList = new ArrayList<HashMap<String, Object>>();
        this.arrayList = arrayList;
    }
    public void addItem(int position, HashMap<String,Object> hashMap){
        this.arrayList.add(hashMap);
        notifyItemInserted(position);
    }
    public HashMap<String,Object> getItem(int i){
        return arrayList.get(i);
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflate = LayoutInflater.from(viewGroup.getContext());
        View view = inflate.inflate(R.layout.menuitem_layout, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        HashMap<String,Object> hashMap = arrayList.get(i);
        myViewHolder.textViewMenuItemMenu.setText((String)hashMap.get("menu"));
        myViewHolder.textViewMenuItemPrice.setText((String) hashMap.get("price"));

        myViewHolder.textViewMenuItemMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Menu clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void removeItem(int position){
        this.arrayList.remove(position);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textViewMenuItemMenu, textViewMenuItemPrice;
        Button buttonMenuItemDel;

        public MyViewHolder(View itemView) {
            super(itemView);

            textViewMenuItemMenu = itemView.findViewById(R.id.textViewMenuItemMenu);
            textViewMenuItemPrice = itemView.findViewById(R.id.textViewMenuItemPrice);
            buttonMenuItemDel = itemView.findViewById(R.id.buttonMenuItemDel);

            buttonMenuItemDel.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            removeItem(position);
            Log.d("ViewHolder Click", position+", "+getItemId());
        }


    }
}


