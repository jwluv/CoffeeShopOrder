package com.lyna.www.coffeeshoporder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.MyViewHolder> {

    Context mContext;
    CustomItemClickListener listener;
    ArrayList<HashMap<String,Object>> arrayList = null;
    public OrderItemAdapter(Context mContext, ArrayList<HashMap<String,Object>> arrayList, CustomItemClickListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        this.arrayList = new ArrayList<HashMap<String, Object>>();
        this.arrayList = arrayList;
    }
    public void addItem(int position, HashMap<String,Object> hashMap){
        this.arrayList.add(hashMap);
        notifyItemInserted(position);
    }
    public HashMap<String,Object> getItem(int position){
        return arrayList.get(position);
    }
    public void updateItem(int position, HashMap<String,Object> hashMap){
        this.arrayList.remove(position);
        notifyDataSetChanged();
        this.arrayList.add(position, hashMap);
        notifyItemInserted(position);
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflate = LayoutInflater.from(viewGroup.getContext());
        View view = inflate.inflate(R.layout.orderitem_layout, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        HashMap<String,Object> hashMap = arrayList.get(i);
        myViewHolder.id = i;
        myViewHolder.textViewOrderItemMenu.setText((String)hashMap.get("menu"));
        myViewHolder.textViewOrderItemPrice.setText((String) hashMap.get("price"));
        myViewHolder.textViewOrderItemEA.setText((String) hashMap.get("ea"));

//        ((MainActivity)mContext).textViewOrderedMenu.setText("");
//        mContext.textViewOrderedMenu.setText("");

//        mContext.
//        myViewHolder.textViewMenuItemMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "Menu clicked", Toast.LENGTH_SHORT).show();
//            }
//        });
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

        int id;
        TextView textViewOrderItemMenu, textViewOrderItemPrice, textViewOrderItemEA;
        Button buttonOrderItemAdd, buttonOrderItemSub;

        public MyViewHolder(View itemView) {
            super(itemView);

            textViewOrderItemMenu = itemView.findViewById(R.id.textViewOrderItemMenu);
            textViewOrderItemPrice = itemView.findViewById(R.id.textViewOrderItemPrice);
            textViewOrderItemEA = itemView.findViewById(R.id.textViewOrderItemEA);
            buttonOrderItemAdd = itemView.findViewById(R.id.buttonOrderItemAdd);
            buttonOrderItemSub = itemView.findViewById(R.id.buttonOrderItemSub);

            buttonOrderItemAdd.setOnClickListener(this);
            buttonOrderItemSub.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            HashMap<String,Object> hashMap = new HashMap<String,Object>();
            int ea;
            switch(view.getId()) {
                case R.id.buttonOrderItemAdd:
                    ea =  Integer.parseInt(textViewOrderItemEA.getText().toString());
                    ea += 1;
                    textViewOrderItemEA.setText(String.valueOf(ea));

                    hashMap.put("menu", textViewOrderItemMenu.getText().toString());
                    hashMap.put("price", textViewOrderItemPrice.getText().toString());
                    hashMap.put("ea", String.valueOf(ea));

                    updateItem(id, hashMap);
                    break;
                case R.id.buttonOrderItemSub:
                    ea =  Integer.parseInt(textViewOrderItemEA.getText().toString());
                    if(ea > 0)
                        ea -= 1;
                    textViewOrderItemEA.setText(String.valueOf(ea));

                    hashMap.put("menu", textViewOrderItemMenu.getText().toString());
                    hashMap.put("price", textViewOrderItemPrice.getText().toString());
                    hashMap.put("ea", String.valueOf(ea));

                    updateItem(id, hashMap);
                    break;

            }
        }


    }


}

