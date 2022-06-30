package com.example.parkme;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
    private Context context;
    private ArrayList<Parkings> parking;
    

    public MyAdapter(ArrayList<Parkings> parking,Context context){
        this.parking = parking;
        this.context = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_card_view,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final Parkings modal = parking.get(position);

        holder.title.setText(modal.getTitle());
        holder.slots.setText(modal.getSlots());
        holder.description.setText(modal.getDescription());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, HomeActivity.class);
                i.putExtra("Title",modal.getTitle());
                i.putExtra("Description",modal.getDescription());
                i.putExtra("Empty slots",modal.getSlots());
                i.putExtra("Allocated",""+modal.getAllocated_slots());
                context.startActivity(i);
            }
        });
    }



    @Override
    public int getItemCount() {return parking.size();}

    
    public class MyHolder extends RecyclerView.ViewHolder{
        public TextView title,description,slots;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            slots = itemView.findViewById(R.id.slots);
        }
    }
}


