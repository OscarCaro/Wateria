package com.example.wateria;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wateria.Activities.MainActivity;
import com.example.wateria.DataStructures.Plant;
import com.example.wateria.DataStructures.PlantList;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private PlantList plantList;
    WeakReference<Context> mContextWeakReference;

    public RecyclerViewAdapter (Context currentContext, PlantList myPlantList){
        this.plantList = myPlantList;
        this.mContextWeakReference = new WeakReference<Context>(currentContext);
    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        Context context = mContextWeakReference.get();
        if (context != null) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_view_layout, parent, false);

            return new ViewHolder(itemView, context);
        }

        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewPlantName;
        public TextView textViewDaysRemaining;
        public TextView textViewStringDays;
        public ImageView imageViewIcon;
        public AppCompatImageButton buttonWatering;

        public ViewHolder (View v, final Context context){
            super(v);

            textViewPlantName = v.findViewById(R.id.text_name);
            textViewDaysRemaining = v.findViewById(R.id.text_number);
            textViewStringDays = v.findViewById(R.id.text_days);
            imageViewIcon = v.findViewById(R.id.image);
            buttonWatering = v.findViewById(R.id.button);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) context).onRowClicked(getAdapterPosition());
                }
            });

            buttonWatering.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) context).onWateringButtonClicked(getAdapterPosition());
                }
            });
        }
    }

    @Override
    public void onBindViewHolder (final ViewHolder holder, int position){
        Context context = mContextWeakReference.get();

        if (context == null) {
            return;
        }

        final Plant currentPlant = plantList.get(position);

        final String plantName = String.valueOf(currentPlant.getPlantName());
        holder.textViewPlantName.setText(plantName);

        final String daysRemaining = Integer.toString(currentPlant.getDaysRemaining());
        holder.textViewDaysRemaining.setText(daysRemaining);

        int numberOfDays = Integer.parseInt(daysRemaining);
        final String days = context.getResources().getQuantityString(R.plurals.days, numberOfDays);
        holder.textViewStringDays.setText(days);

        //holder.imageViewIcon.setImageDrawable(plantList.getPlantIcon(position));      <- Error-proof way (load it again)
        holder.imageViewIcon.setImageDrawable(currentPlant.getIcon());             //   <- Assume it's loaded already in plant.icon

    }

    @Override
    public int getItemCount(){
        return plantList.getSize();
    }
}
